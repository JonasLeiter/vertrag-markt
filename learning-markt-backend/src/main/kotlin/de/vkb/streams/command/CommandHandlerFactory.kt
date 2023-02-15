package de.vkb.streams.command

import de.vkb.config.TopicConfig
import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.Event
import de.vkb.streams.MarktEventSourcing
import io.micronaut.configuration.kafka.serde.JsonObjectSerde
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.json.JsonObjectSerializer
import jakarta.inject.Singleton
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream

@Factory
class CommandHandlerFactory(
    private val marktEventSourcing: MarktEventSourcing,
    private val topicConfig: TopicConfig,
    private val serializer: JsonObjectSerializer
) {

    private val eventSourcingStreams = EventSourcingStreamFactory(replayMode = false)

    @Singleton
    fun buildCommandHandlerTopology(
        builder: ConfiguredStreamBuilder,
    ): KStream<String, *> {
        val vertragTable = builder.globalTable(
            topicConfig.vertragState, Consumed.with(Serdes.String(), JsonObjectSerde(serializer, Vertrag::class.java))
        )

        val inputStream =
            eventSourcingStreams.commandInputStream(builder, marktEventSourcing.commandTopologyDescription)

        val updateStream = eventSourcingStreams.commandHandler(
            builder = builder,
            inputStream = inputStream,
            topologyDescription = marktEventSourcing.commandTopologyDescription.builder()
                .commandStreamTransformation { stream ->
                    stream
                        .filter { _, v -> v !is ErstelleMarkt }
                }
                .correlationIdFromCommand { it.commandId }
                .aggregateIdFromEvent { it.aggregateIdentifier }
                .buildCommandHandlerTopologyDescription(),
            commandHandler = marktEventSourcing.updateCommandHandler
        )

        val createStream = eventSourcingStreams.commandHandler(
            builder = builder,
            inputStream = inputStream,
            topologyDescription = marktEventSourcing.commandTopologyDescription.builder()
                .commandStreamTransformation { stream ->
                    stream.filter { _, value -> value is ErstelleMarkt }
                        .mapValues { _, value -> value as ErstelleMarkt }
                        .leftJoin(
                            vertragTable,
                            { _, command -> command.payload.vertragId },
                            { command, vertrag -> CommandAndVertrag(command, vertrag) })
                }
                .correlationIdFromCommand { pair -> pair.command.commandId }
                .aggregateIdFromEvent { evt: Event -> evt.aggregateIdentifier }
                .buildCommandHandlerTopologyDescription(),
            commandHandler = marktEventSourcing.createCommandHandler,
            skipFeedbackStoreCreation = true
        )

        return updateStream.mapValues { value -> value.command }.merge(
                createStream.mapValues { value -> value.command.command }
            )
    }
}

data class CommandAndVertrag(val command: ErstelleMarkt, val vertrag: Vertrag)