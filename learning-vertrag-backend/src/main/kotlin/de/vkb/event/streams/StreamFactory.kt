package de.vkb.event.streams

import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.apache.kafka.streams.kstream.KStream

@Factory
class StreamFactory(private val vertragEventSourcing: VertragEventSourcing) {
    private val eventSourcingStreamFactory = EventSourcingStreamFactory(replayMode = false)

    @Singleton
    fun buildCommandHandlerTopology(
        @Named("default") builder: ConfiguredStreamBuilder
    ): KStream<String, *> =
        eventSourcingStreamFactory.commandHandler(
            builder = builder,
            topologyDescription = vertragEventSourcing.commandHandlerTopologyDescription,
            commandHandler = vertragEventSourcing.commandHandler
        )

    @Singleton
    fun buildEventAggregatorTopology(
        @Named("event-aggregator") builder: ConfiguredStreamBuilder
    ): KStream<String, *> =
        eventSourcingStreamFactory.eventAggregator(
            builder = builder,
            topologyDescription = vertragEventSourcing.eventAggregateTopologyDescription,
            eventAggregator = vertragEventSourcing.eventAggregator,
            skipFeedbackStoreCreation = false
        )
}