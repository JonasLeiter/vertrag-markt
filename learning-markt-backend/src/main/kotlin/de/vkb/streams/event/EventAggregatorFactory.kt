package de.vkb.streams.event

import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import de.vkb.streams.MarktEventSourcing
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.apache.kafka.streams.kstream.KStream

@Factory
class EventAggregatorFactory(private val marktEventSourcing: MarktEventSourcing) {

    private val eventSourcingStreams = EventSourcingStreamFactory(replayMode = false)

    @Singleton
    fun buildEventAggregatorTopology(
        @Named("event-aggregator") builder: ConfiguredStreamBuilder,
    ): KStream<String, *> {
        return eventSourcingStreams.eventAggregator(
            builder,
            marktEventSourcing.eventTopologyDescription,
            marktEventSourcing.eventAggregator,
            skipFeedbackStoreCreation = true
        )
    }
}