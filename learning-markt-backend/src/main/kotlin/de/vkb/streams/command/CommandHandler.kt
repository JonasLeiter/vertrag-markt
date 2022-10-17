package de.vkb.streams.command

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.Command
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.Event
import de.vkb.model.result.CommandResult
import de.vkb.model.validation.CommandValidation
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.Processor
import org.apache.kafka.streams.processor.ProcessorContext
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.Stores

@Factory
class CommandHandler(private val serializer: JsonObjectSerializer,
                     private val topicConfig: TopicConfig,
                     private val storeConfig: StoreConfig,
                     private val validator: CommandValidator
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
            ))

        val createStream = createStream(stream)
        val updateStream = updateStream(stream)

        writeToTopic(createStream)
        writeToTopic(updateStream)

        return stream
    }

    private fun createStream(stream: KStream<String, Command>): KStream<String, CommandResult>{
        return stream.filter{_, value -> value is ErstelleMarkt }
            .map{_, value -> KeyValue((value as ErstelleMarkt).payload.vertragId, value)}
            .repartition(Repartitioned.with(Serdes.String(), JsonObjectSerde(serializer, ErstelleMarkt::class.java)))
            .transformValues(
                ValueTransformerWithKeySupplier {
                    object : ValueTransformerWithKey<String, ErstelleMarkt, CommandResult> {
                        lateinit var vertragStore: ReadOnlyKeyValueStore<String, Vertrag>

                        override fun init(context: ProcessorContext) {
                            vertragStore = context.getStateStore(storeConfig.vertragStore)
                        }

                        override fun transform(readOnlyKey: String?, command: ErstelleMarkt): CommandResult {
                            val vertrag: Vertrag? = vertragStore[command.payload.vertragId]

                            return validator.validateCreateCommand(command, vertrag)
                        }

                        override fun close() {}

                    }
                }, storeConfig.vertragStore)
    }

    private fun updateStream(stream: KStream<String, Command>): KStream<String, CommandResult> {
        return stream.filter{_, value -> value !is ErstelleMarkt }
            .transformValues(
                ValueTransformerWithKeySupplier {
                    object : ValueTransformerWithKey<String, Command, CommandResult> {
                        override fun init(context: ProcessorContext) {}

                        override fun transform(readOnlyKey: String?, command: Command): CommandResult {
                            return validator.validateUpdateCommand(command)
                        }

                        override fun close() {}

                    }
                })
    }

    private fun writeToTopic(stream: KStream<String, CommandResult>){
        stream
            .map { _, value -> KeyValue(value.validation.aggregateIdentifier, value.validation) }
            .to(
                topicConfig.validation, Produced.with(Serdes.String(), JsonObjectSerde(serializer, CommandValidation::class.java))
            )

        stream
            .filter { _, value -> value.event != null }
            .map { _, value -> KeyValue(value.event?.aggregateIdentifier, value.event as Event) }
            .to(
                topicConfig.internalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
            )
    }

    private fun buildVertragStore(builder: ConfiguredStreamBuilder){
        builder.stream(
            topicConfig.vertragState, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Vertrag::class.java))
        ).process(
            {
                object: Processor<String, Vertrag> {
                    lateinit var vertragStore: KeyValueStore<String, Vertrag>

                    override fun init(context: ProcessorContext) {
                        this.vertragStore = context.getStateStore(storeConfig.vertragStore)
                    }

                    override fun process(key: String, vertrag: Vertrag) {
                        vertragStore.put(key,vertrag)
                    }

                    override fun close() {}
                }
            }, storeConfig.vertragStore
        )
    }
}