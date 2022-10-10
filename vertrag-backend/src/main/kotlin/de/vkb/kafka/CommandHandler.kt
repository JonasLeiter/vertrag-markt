package de.vkb.kafka

import de.vkb.command.AendereBeginn
import de.vkb.command.AendereEnde
import de.vkb.command.Command
import de.vkb.command.ErstelleVertrag
import de.vkb.event.*
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.processor.ProcessorContext

@Factory
class CommandHandler(private val serializer: JsonObjectSerializer, private val topicConfig: TopicConfig) {

    @Singleton
    fun createCommandStream(builder: ConfiguredStreamBuilder): KStream<String, Command> {


        val stream: KStream<String, Command> = builder.stream(
            topicConfig.command,
            Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Command::class.java)))

        stream
            .transformValues(
            ValueTransformerWithKeySupplier {
                object : ValueTransformerWithKey<String, Command, Event> {

                    override fun init(context: ProcessorContext?) {}
                    override fun close() {}

                    override fun transform(readOnlyKey: String?, command: Command?): Event? =
                        when(command) {
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
                                BeginnGeandert(
                                    eventId = command.aggregateId,
                                    aggregateId = command.aggregateId,
                                    payload = BeginnGeaendertPayload(
                                        vertragId = "empty",
                                        beginn = command.payload.beginn,
                                    )
                                )
                            }
                            is AendereEnde -> {
                                EndeGeandert(
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
                }
            }
        )
            .filter { _, value -> value != null }
            .selectKey { _, value -> value.eventId }
            .to(topicConfig.internalEvent, Produced.with(Serdes.String(), JsonObjectSerde(serializer, Event::class.java)))

        return stream
    }
}