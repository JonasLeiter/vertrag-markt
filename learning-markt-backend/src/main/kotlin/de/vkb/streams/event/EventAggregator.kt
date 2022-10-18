package de.vkb.streams.event

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
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
import org.apache.kafka.streams.state.Stores

@Factory
class EventAggregator(private val serializer: JsonObjectSerializer,
                      private val topicConfig: TopicConfig,
                      private val storeConfig: StoreConfig,
                      private val validator: EventValidator
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

        val stream = builder.stream(
            topicConfig.internalEvent, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)
            )
        ).transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Event, EventResult> {
                    lateinit var stateStore: KeyValueStore<String, Markt>

                    override fun init(context: ProcessorContext) {
                        stateStore = context.getStateStore(storeConfig.stateStore)
                    }


                    override fun transform(readOnlyKey: String?, event: Event): EventResult {
                        val markt: Markt? = stateStore[readOnlyKey]
                        println("Markt ist: $markt for event: ${event.javaClass} with key $readOnlyKey")

                        val result = validator.validateEvent(event, markt)

                        if(result.validation.isValid) {
                            println("Adding markt: ${result.markt}")
                            stateStore.put(readOnlyKey, result.markt)
                        }

                        return result
                    }

                    override fun close() {}

                }
            },
            storeConfig.stateStore
        )

        stream
            .filter{ _, value -> value.event != null }
            .map{ key, value -> KeyValue(key, value.event) }
            .to(topicConfig.externalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))


        stream
            .map{ _, value -> KeyValue(value.validation.aggregateIdentifier, value.validation) }
            .to(topicConfig.validation, Produced.with(Serdes.String(), JsonObjectSerde(serializer, EventValidation::class.java)))

        stream
            .filter{_, value -> value.markt != null}
            .map{key,value -> KeyValue(key, value.markt)}
            .to(topicConfig.state, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Markt::class.java)))

        return stream
    }
}