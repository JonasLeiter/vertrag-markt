package de.vkb.event

import de.vkb.kafka.StoreConfig
import de.vkb.kafka.TopicConfig
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
    private val validator: EventValidator) {

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
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))

        stream.transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Event, Event> {
                    lateinit var vertragStore: KeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        vertragStore = context.getStateStore(stores.vertragStore)
                    }

                    override fun transform(readOnlyKey: String, value: Event): Event? {
                        val existingVertrag: Vertrag? = vertragStore[readOnlyKey]

                        val validation = validator.validate(value, existingVertrag)

                        if(validation.valid) {
                            when(value) {
                                is VertragErstellt -> {
                                    vertragStore.put(value.eventId,
                                        Vertrag(
                                            id = value.eventId,
                                            bezeichnung = value.payload.bezeichnung,
                                            beginn = value.payload.beginn,
                                            ende = value.payload.ende
                                        )
                                    )
                                }
                                is BeginnGeandert -> {
                                    vertragStore.put(value.eventId,
                                        Vertrag(
                                            id = value.eventId,
                                            bezeichnung = existingVertrag!!.bezeichnung,
                                            beginn = value.payload.beginn,
                                            ende = existingVertrag.ende
                                        )
                                    )
                                }
                                is EndeGeandert -> {
                                    vertragStore.put(value.eventId,
                                        Vertrag(
                                            id = value.eventId,
                                            bezeichnung = existingVertrag!!.bezeichnung,
                                            beginn = existingVertrag.beginn,
                                            ende = value.payload.ende
                                        )
                                    )
                                }
                            }
                            return value
                        } else {
                            return null
                        }
                    }

                    override fun close() {}
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