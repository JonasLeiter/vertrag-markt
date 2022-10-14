package de.vkb.streams.command

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.Command
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.Event
import de.vkb.model.validation.CommandValidation
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
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.Stores

@Factory
class CommandHandler(private val serializer: JsonObjectSerializer,
                     private val topicConfig: TopicConfig,
                     private val storeConfig: StoreConfig
) {

    @Singleton
    fun handleCommand(builder: ConfiguredStreamBuilder): KStream<String, *> {
        builder.addStateStore(
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(storeConfig.vertragStore),
                Serdes.StringSerde(), JsonObjectSerde(serializer, Vertrag::class.java)
            )
        )

        buildVertragStore(builder)

        val stream = builder.stream(
            topicConfig.command, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Command::class.java)
            )
        ).transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Command, Pair<Event?, CommandValidation>> {
                    lateinit var vertragStore: ReadOnlyKeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        vertragStore = context.getStateStore(storeConfig.vertragStore)
                    }

                    override fun transform(readOnlyKey: String?, command: Command):  Pair<Event?, CommandValidation> {
                        var vertrag: Vertrag? = null

                        if(command is ErstelleMarkt){
                            vertrag = vertragStore[command.payload.vertragId]
                        }

                        val commandResult = CommandValidator().validateCommand(command, vertrag)

                        val intEvent = if(commandResult.validation.isValid) {
                            commandResult.event
                        } else{
                            null
                        }

                        return Pair(intEvent, commandResult.validation)
                    }

                    override fun close() {}

                }
            }, storeConfig.vertragStore)

            stream
                .filter { _, value -> value.first != null }
                .map { _, value -> KeyValue(value.first?.aggregateIdentifier, value.first as Event) }
                .to(
                topicConfig.internalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
                )

            stream
                .map { _, value -> KeyValue(value.second.commandId, value.second) }
                .to(
                    topicConfig.validation, Produced.with(Serdes.String(), JsonObjectSerde(serializer, CommandValidation::class.java))
                )

        return stream
    }

    private fun buildVertragStore(builder: ConfiguredStreamBuilder): KStream<String, Vertrag>{
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