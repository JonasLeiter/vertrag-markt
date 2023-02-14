package de.vkb.event

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
    fun buildEventAggregatorTopology(
        @Named("event-aggregator") builder: ConfiguredStreamBuilder,
        // wird nicht mehr benutzt -> readme update
//        eventAggregator: DelegatingEventAggregator<String, String, Event, Vertrag, EventValidation>
    ): KStream<String, *> =
        eventSourcingStreamFactory.eventAggregator(
            builder = builder,
            topologyDescription = vertragEventSourcing.topologyDescription,
            eventAggregator = vertragEventSourcing.eventAggregator,
            skipFeedbackStoreCreation = false
        )
}