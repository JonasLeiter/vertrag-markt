package de.vkb.streams.command

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.model.command.Command
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

@Factory
class CommandHandler(private val serializer: JsonObjectSerializer,
                     private val topicConfig: TopicConfig,
                     private val storeConfig: StoreConfig
) {

    @Singleton
    fun handleCommand(builder: ConfiguredStreamBuilder): KStream<String, *> {
//        builder.addStateStore(
//            Stores.keyValueStoreBuilder(
//                Stores.persistentKeyValueStore(storeConfig.validationStore),
//                Serdes.StringSerde(), JsonObjectSerde(serializer, Validation::class.java)
//            )
//        )

        val stream = builder.stream(
            topicConfig.command, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Command::class.java)
            )
        ).transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Command, Pair<Event?, CommandValidation>> {
//                    lateinit var validationStore: KeyValueStore<String, Validation>

                    override fun init(context: ProcessorContext) {
//                        validationStore = context.getStateStore(storeConfig.validationStore)
                    }

                    override fun transform(readOnlyKey: String?, command: Command):  Pair<Event?, CommandValidation> {
                        val commandResult = CommandValidator().validate(command)


                        val intEvent = if(commandResult.validation.isValid) {
//                            validationStore.put(command.commandId, commandResult.validation)
                            commandResult.event
                        } else{
//                            validationStore.put(command.commandId, commandResult.validation)
                            null
                        }

                        return Pair(intEvent, commandResult.validation)
                    }

                    override fun close() {}

                }
            })

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
}