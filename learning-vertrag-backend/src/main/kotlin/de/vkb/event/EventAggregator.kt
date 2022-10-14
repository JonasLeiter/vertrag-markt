package de.vkb.event

import de.vkb.event.events.*
import de.vkb.kafka.StoreConfig
import de.vkb.kafka.TopicConfig
import de.vkb.models.Vertrag
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.Stores

@Factory
class EventAggregator(
    private val stores: StoreConfig,
    private val topics: TopicConfig,
    private val serializer: JsonObjectSerializer,
    private val validator: EventValidator
) {

    @Singleton
    fun createExternalEventStream(builder: ConfiguredStreamBuilder): KStream<String, Triple<Vertrag?, Event?, EventValidationResult>> {
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(stores.vertragStore),
                Serdes.StringSerde(), JsonObjectSerde(serializer, Vertrag::class.java)
            )
        )
        val stream = builder.stream(
            topics.internalEvent,
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
        )
            .transformValues(
                ValueTransformerWithKeySupplier {
                    object : ValueTransformerWithKey<String, Event, Triple<Vertrag?, Event?, EventValidationResult>> {
                        lateinit var vertragStore: KeyValueStore<String, Vertrag>

                        override fun init(context: ProcessorContext) {
                            vertragStore = context.getStateStore(stores.vertragStore)
                        }

                        override fun close() {}

                        override fun transform(
                            readOnlyKey: String,
                            event: Event
                        ): Triple<Vertrag?, Event?, EventValidationResult> {
                            val existingVertrag: Vertrag? = vertragStore[readOnlyKey]

                            val validationResult = validator.validate(event, existingVertrag)

                            return if (validationResult.valid) {
                                when (event) {
                                    is VertragErstellt -> {
                                        val vertrag = Vertrag(
                                            id = event.aggregateId,
                                            bezeichnung = event.payload.bezeichnung,
                                            beginn = event.payload.beginn,
                                            ende = event.payload.ende
                                        )
                                        val externalEvent = VertragErstellt(
                                            commandId = event.commandId,
                                            aggregateId = event.aggregateId,
                                            payload = VertragErstelltPayload(
                                                vertragId = vertrag.id,
                                                bezeichnung = vertrag.bezeichnung,
                                                beginn = vertrag.beginn,
                                                ende = vertrag.ende
                                            )
                                        )
                                        vertragStore.put(vertrag.id, vertrag)
                                        Triple(vertrag, externalEvent, validationResult)
                                    }

                                    is BeginnGeaendert -> {
                                        val vertrag = existingVertrag!!.copy(beginn = event.payload.beginn)
                                        val externalEvent = BeginnGeaendert(
                                            commandId = event.commandId,
                                            aggregateId = event.aggregateId,
                                            payload = BeginnGeaendertPayload(
                                                vertragId = vertrag.id,
                                                beginn = vertrag.beginn
                                            )
                                        )
                                        vertragStore.put(vertrag.id, vertrag)
                                        Triple(vertrag, externalEvent, validationResult)
                                    }

                                    is EndeGeaendert -> {
                                        val vertrag = existingVertrag!!.copy(ende = event.payload.ende)
                                        val externalEvent = EndeGeaendert(
                                            commandId = event.commandId,
                                            aggregateId = event.aggregateId,
                                            payload = EndeGeaendertPayload(
                                                vertragId = vertrag.id,
                                                ende = vertrag.ende
                                            )
                                        )
                                        vertragStore.put(vertrag.id, vertrag)
                                        Triple(vertrag, externalEvent, validationResult)
                                    }

                                    else -> Triple(null, null, validationResult)
                                }
                            } else Triple(null, null, validationResult)
                        }
                    }
                }, stores.vertragStore
            )

        stream
            .filter { _, value -> value.first != null }
            .map { _, value -> KeyValue(value.first!!.id, value.first) }
            .to(
                topics.state,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, Vertrag::class.java))
            )

        stream
            .filter { _, value -> value.second != null }
            .map { _, value -> KeyValue(value.second!!.aggregateId, value.second) }
            .to(
                topics.externalEvent,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
            )

        stream
            .map { _, value -> KeyValue(value.third.aggregateId, value.third) }
            .to(
                topics.validation,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, EventValidationResult::class.java))
            )

        return stream
    }
}