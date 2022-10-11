package de.vkb.event

import de.vkb.event.events.*
import de.vkb.kafka.producers.StateProducer
import de.vkb.kafka.StoreConfig
import de.vkb.kafka.TopicConfig
import de.vkb.kafka.producers.ValidationProducer
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

                        return if (validationResult.valid) {
                            when (event) {
                                is VertragErstellt -> {
                                    val vertrag = Vertrag(
                                        id = event.eventId,
                                        bezeichnung = event.payload.bezeichnung,
                                        beginn = event.payload.beginn,
                                        ende = event.payload.ende
                                    )
                                    vertragStore.put(event.eventId, vertrag)
                                    stateProducer.send(event.eventId, vertrag)
                                    VertragErstellt(
                                        eventId = event.eventId,
                                        aggregateId = event.aggregateId,
                                        payload = VertragErstelltPayload(
                                            vertragId = vertrag.id,
                                            bezeichnung = vertrag.bezeichnung,
                                            beginn = vertrag.beginn,
                                            ende = vertrag.ende
                                        )
                                    )
                                }

                                is BeginnGeaendert -> {
                                    val vertrag = Vertrag(
                                        id = event.eventId,
                                        bezeichnung = existingVertrag!!.bezeichnung,
                                        beginn = event.payload.beginn,
                                        ende = existingVertrag.ende
                                    )
                                    vertragStore.put(event.eventId, vertrag)
                                    stateProducer.send(event.eventId, vertrag)
                                    BeginnGeaendert(
                                        eventId = event.eventId,
                                        aggregateId = event.aggregateId,
                                        payload = BeginnGeaendertPayload(
                                            vertragId = vertrag.id,
                                            beginn = vertrag.beginn
                                        )
                                    )
                                }

                                is EndeGeaendert -> {
                                    val vertrag = Vertrag(
                                        id = event.eventId,
                                        bezeichnung = existingVertrag!!.bezeichnung,
                                        beginn = existingVertrag.beginn,
                                        ende = event.payload.ende
                                    )
                                    vertragStore.put(event.eventId, vertrag)
                                    stateProducer.send(event.eventId, vertrag)
                                    EndeGeaendert(
                                        eventId = event.eventId,
                                        aggregateId = event.aggregateId,
                                        payload = EndeGeaendertPayload(
                                            vertragId = vertrag.id,
                                            ende = vertrag.ende
                                        )
                                    )
                                }

                                else -> null
                            }
                        } else null
                    }
                }
            }, stores.vertragStore
        )
            .filter { _, value -> value != null }
            .selectKey { _, value -> value.eventId }
            .to(topics.externalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))

        return stream
    }
}