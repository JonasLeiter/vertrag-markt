package de.vkb.command

import de.vkb.command.commands.AendereBeginn
import de.vkb.command.commands.AendereEnde
import de.vkb.command.commands.Command
import de.vkb.command.commands.ErstelleVertrag
import de.vkb.event.events.*
import de.vkb.kafka.TopicConfig
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
class CommandHandler(
    private val serializer: JsonObjectSerializer,
    private val topics: TopicConfig,
    private val commandValidator: CommandValidator
) {

    @Singleton
    fun createCommandStream(builder: ConfiguredStreamBuilder): KStream<String, *> {

        val stream = builder.stream(
            topics.command,
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Command::class.java))
        )
            .transformValues(
                ValueTransformerWithKeySupplier {
                    object : ValueTransformerWithKey<String, Command, Pair<Event?, CommandValidationResult>> {

                        override fun init(context: ProcessorContext?) {}
                        override fun close() {}

                        override fun transform(readOnlyKey: String, command: Command): Pair<Event?, CommandValidationResult> {

                            val validationResult = commandValidator.validate(command)


                            return if (validationResult.valid) {
                                when (command) {
                                    is ErstelleVertrag -> {
                                        val event = VertragErstellt(
                                            commandId = command.commandId,
                                            aggregateId = command.aggregateId,
                                            payload = VertragErstelltPayload(
                                                vertragId = command.aggregateId,
                                                bezeichnung = command.payload.bezeichnung,
                                                beginn = command.payload.beginn,
                                                ende = command.payload.ende
                                            )
                                        )
                                        Pair(event, validationResult)
                                    }

                                    is AendereBeginn -> {
                                        val event = BeginnGeaendert(
                                            commandId = command.commandId,
                                            aggregateId = command.aggregateId,
                                            payload = BeginnGeaendertPayload(
                                                vertragId = command.aggregateId,
                                                beginn = command.payload.beginn,
                                            )
                                        )
                                        Pair(event, validationResult)
                                    }

                                    is AendereEnde -> {
                                        val event = EndeGeaendert(
                                            commandId = command.commandId,
                                            aggregateId = command.aggregateId,
                                            payload = EndeGeaendertPayload(
                                                vertragId = command.aggregateId,
                                                ende = command.payload.ende,
                                            )
                                        )
                                        Pair(event, validationResult)
                                    }

                                    else -> Pair(null, validationResult)
                                }
                            } else Pair(null, validationResult)
                        }
                    }
                }
            )

        stream
            .filter { _, value -> value.first != null }
            .map { _, value -> KeyValue(value.first!!.aggregateId, value.first) }
            .to(
                topics.internalEvent,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
            )

        stream
            .map { _, value -> KeyValue(value.second.aggregateId, value.second) }
            .to(
                topics.validation,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, CommandValidationResult::class.java))
            )

        return stream
    }
}