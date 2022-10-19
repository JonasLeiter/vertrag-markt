package de.vkb.streams.command

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
import org.apache.kafka.streams.processor.ProcessorContext

@Factory
class CommandHandler(private val serializer: JsonObjectSerializer,
                     private val topicConfig: TopicConfig,
                     private val validator: CommandValidator
) {

    @Singleton
    fun handleCommand(builder: ConfiguredStreamBuilder): KStream<String, *> {
        val vertragTable = builder.globalTable(
            topicConfig.vertragState, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Vertrag::class.java)))

        val stream = builder.stream(
            topicConfig.command, Consumed.with(
                Serdes.String(), JsonObjectSerde(serializer, Command::class.java)
            )
        )

        val createStream = createStream(stream, vertragTable)
        val updateStream = updateStream(stream)

        writeToTopic(createStream)
        writeToTopic(updateStream)

        return stream
    }

    private fun createStream(stream: KStream<String, Command>, globalKTable: GlobalKTable<String, Vertrag>): KStream<String, CommandResult> {
        return stream.filter { _, value -> value is ErstelleMarkt }
                     .leftJoin(
                         globalKTable,
                         { _, command -> (command as ErstelleMarkt).payload.vertragId },
                         { command, vertrag -> validator.validateCreateCommand((command as ErstelleMarkt), vertrag) })
    }

    private fun updateStream(stream: KStream<String, Command>): KStream<String, CommandResult> {
        return stream.filter { _, value -> value !is ErstelleMarkt }
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

    private fun writeToTopic(stream: KStream<String, CommandResult>) {
        stream
            .map { _, value -> KeyValue(value.validation.aggregateIdentifier, value.validation) }
            .to(
                topicConfig.validation,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, CommandValidation::class.java))
            )

        stream
            .filter { _, value -> value.event != null }
            .map { _, value -> KeyValue(value.event?.aggregateIdentifier, value.event as Event) }
            .to(
                topicConfig.internalEvent,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
            )
    }

}