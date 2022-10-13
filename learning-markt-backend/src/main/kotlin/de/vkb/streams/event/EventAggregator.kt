package de.vkb.streams.event

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.model.aggregate.Markt
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.event.Event
import de.vkb.model.event.MarktErstellt
import de.vkb.model.result.EventResult
import de.vkb.model.validation.EventValidation
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.Stores

@Factory
class EventAggregator(private val serializer: JsonObjectSerializer,
                      private val topicConfig: TopicConfig,
                      private val storeConfig: StoreConfig
) {

    @Singleton
    @Named("event-aggregator")
    fun aggregateEvents(@Named("event-aggregator") builder: ConfiguredStreamBuilder): KStream<String, *>{
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(storeConfig.stateStore),
                Serdes.StringSerde(), JsonObjectSerde(serializer, Markt::class.java)
            )
        )

        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(storeConfig.vertragStore),
                Serdes.StringSerde(), JsonObjectSerde(serializer, Vertrag::class.java)
            )
        )

        val stream = builder.stream(
            topicConfig.internalEvent, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)
            )
        ).transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Event, Pair<Event?, EventResult> > {
                    lateinit var stateStore: KeyValueStore<String, Markt>
                    lateinit var vertragStore: ReadOnlyKeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        stateStore = context.getStateStore(storeConfig.stateStore)
                        vertragStore = context.getStateStore(storeConfig.vertragStore)
                    }


                    override fun transform(readOnlyKey: String?, event: Event): Pair<Event?, EventResult> {
                        val markt: Markt? = stateStore[readOnlyKey]
                        var vertrag: Vertrag? = null

                        if(event is MarktErstellt){
                            println(event.payload.vertragId)
                            vertrag = vertragStore[event.payload.vertragId]
                        }

                        println("Vertrag ist $vertrag")
                        val result: EventResult = EventValidator().validateEvent(markt, vertrag, event)

                        val extEvent = if(result.validation.isValid){
                            stateStore.put(readOnlyKey, result.markt)
                            event
                        } else{
                            null
                        }

                        return Pair(extEvent, result)
                    }

                    override fun close() {}

                }
            },
            storeConfig.stateStore,
            storeConfig.vertragStore
        )

        stream
            .filter{ _, value -> value.first != null }
            .map{ key, value -> KeyValue(key, value.first) }
            .to(topicConfig.externalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))


        stream
            .map{ _, value -> KeyValue(value.second.validation.commandId, value.second.validation) }
            .to(topicConfig.validation, Produced.with(Serdes.String(), JsonObjectSerde(serializer, EventValidation::class.java)))

        stream
            .filter{_, value -> value.second.markt != null}
            .map{key,value -> KeyValue(key, value.second.markt)}
            .to(topicConfig.state, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Markt::class.java)))

        buildVertragReader(builder)
        return stream
    }


    private fun buildVertragReader(builder: ConfiguredStreamBuilder): KStream<String, Vertrag>{
        val stream = builder.stream(
            topicConfig.vertragState, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Vertrag::class.java))
        ).transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Vertrag, Vertrag> {
                    lateinit var vertragStore: KeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        this.vertragStore = context.getStateStore(storeConfig.vertragStore)
                    }

                    override fun transform(key: String, vertrag: Vertrag): Vertrag {
                        println("put vertrag $vertrag")
                        vertragStore.put(key,vertrag)
                        vertragStore.all().forEach { println("Value: $it") }
                        return vertrag
                    }

                    override fun close() {}
                }
            }, storeConfig.vertragStore
        )

        return stream
    }
}