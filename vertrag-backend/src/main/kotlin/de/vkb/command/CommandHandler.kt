package de.vkb.command

import de.vkb.command.commands.AendereBeginn
import de.vkb.command.commands.AendereEnde
import de.vkb.command.commands.Command
import de.vkb.command.commands.ErstelleVertrag
import de.vkb.event.events.*
import de.vkb.kafka.TopicConfig
import de.vkb.kafka.ValidationProducer
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext

@Factory
class CommandHandler(
    private val serializer: JsonObjectSerializer,
    private val topics: TopicConfig,
    private val commandValidator: CommandValidator,
    private val validationProducer: ValidationProducer
) {

    @Singleton
    fun createCommandStream(builder: ConfiguredStreamBuilder): KStream<String, Command> {

        val stream: KStream<String, Command> = builder.stream(
            topics.command,
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Command::class.java))
        )

        stream
            .transformValues(
                ValueTransformerWithKeySupplier {
                    object : ValueTransformerWithKey<String, Command, Event> {

                        override fun init(context: ProcessorContext?) {}
                        override fun close() {}

                        override fun transform(readOnlyKey: String, command: Command): Event? {

                            val validationResult = commandValidator.validate(command)
                            validationProducer.send(validationResult.validationId, validationResult)

                            return if (validationResult.valid) {
                                when (command) {
                                    is ErstelleVertrag -> {
                                        VertragErstellt(
                                            eventId = command.aggregateId,
                                            aggregateId = command.aggregateId,
                                            payload = VertragErstelltPayload(
                                                vertragId = "empty",
                                                bezeichnung = command.payload.bezeichnung,
                                                beginn = command.payload.beginn,
                                                ende = command.payload.ende
                                            )
                                        )
                                    }

                                    is AendereBeginn -> {
                                        BeginnGeaendert(
                                            eventId = command.aggregateId,
                                            aggregateId = command.aggregateId,
                                            payload = BeginnGeaendertPayload(
                                                vertragId = "empty",
                                                beginn = command.payload.beginn,
                                            )
                                        )
                                    }

                                    is AendereEnde -> {
                                        EndeGeaendert(
                                            eventId = command.aggregateId,
                                            aggregateId = command.aggregateId,
                                            payload = EndeGeaendertPayload(
                                                vertragId = "empty",
                                                ende = command.payload.ende,
                                            )
                                        )
                                    }

                                    else -> null
                                }
                            } else null
                        }
                    }
                }
            )
            .filter { _, value -> value != null }
            .selectKey { _, value -> value.eventId }
            .to(
                topics.internalEvent,
                Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java))
            )

        return stream
    }
}