package de.vkb.event

import de.vkb.event.events.BeginnGeandert
import de.vkb.event.events.EndeGeandert
import de.vkb.event.events.Event
import de.vkb.event.events.VertragErstellt
import de.vkb.kafka.StateProducer
import de.vkb.kafka.StoreConfig
import de.vkb.kafka.TopicConfig
import de.vkb.kafka.ValidationProducer
import de.vkb.models.Vertrag
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.Stores

@Factory
class EventAggregator(
    private val stores: StoreConfig,
    private val topics: TopicConfig,
    private val serializer: JsonObjectSerializer,
    private val validator: EventValidator,
    private val stateProducer: StateProducer,
    private val validationProducer: ValidationProducer
) {

    @Singleton
    fun createExternalEventStream(builder: ConfiguredStreamBuilder): KStream<String, Event> {
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(stores.vertragStore),
                Serdes.StringSerde(), JsonObjectSerde(serializer, Vertrag::class.java)
            )
        )
        val stream: KStream<String, Event> = builder.stream(
            topics.internalEvent,
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
        )

        stream.transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Event, Event> {
                    lateinit var vertragStore: KeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        vertragStore = context.getStateStore(stores.vertragStore)
                    }
                    override fun close() {}

                    override fun transform(readOnlyKey: String, event: Event): Event? {
                        val existingVertrag: Vertrag? = vertragStore[readOnlyKey]

                        val validationResult = validator.validate(event, existingVertrag)
                        validationProducer.send(validationResult.validationId, validationResult)

                        if (validationResult.valid) {
                            val vertrag: Vertrag? =
                                when (event) {
                                    is VertragErstellt -> {
                                        event.payload.vertragId = event.aggregateId
                                        Vertrag(
                                            id = event.eventId,
                                            bezeichnung = event.payload.bezeichnung,
                                            beginn = event.payload.beginn,
                                            ende = event.payload.ende
                                        )
                                    }
                                    is BeginnGeandert -> {
                                        event.payload.vertragId = event.aggregateId
                                        Vertrag(
                                            id = event.eventId,
                                            bezeichnung = existingVertrag!!.bezeichnung,
                                            beginn = event.payload.beginn,
                                            ende = existingVertrag.ende
                                        )
                                    }
                                    is EndeGeandert -> {
                                        event.payload.vertragId = event.aggregateId
                                        Vertrag(
                                            id = event.eventId,
                                            bezeichnung = existingVertrag!!.bezeichnung,
                                            beginn = existingVertrag.beginn,
                                            ende = event.payload.ende
                                        )
                                    }
                                    else -> null
                                }

                            vertragStore.put(event.eventId, vertrag)
                            stateProducer.send(event.eventId, vertrag!!)

                            return event
                        } else {
                            return null
                        }
                    }
                }
            }, stores.vertragStore
        )

        stream
            .filter { _, value -> value != null }
            .selectKey { _, value -> value.eventId }
            .to(topics.externalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))

        return stream
    }
}