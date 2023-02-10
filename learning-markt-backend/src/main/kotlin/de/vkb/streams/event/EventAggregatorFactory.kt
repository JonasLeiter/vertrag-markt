package de.vkb.streams.event

import de.vkb.laser.es.config.EventTopologyDescription
import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
import de.vkb.model.validation.EventValidation
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.apache.kafka.streams.kstream.KStream

@Factory
class EventAggregatorFactory(private val eventSourcingStreams: EventSourcingStreamFactory) {

    @Singleton
    fun buildEventAggregatorTopology(
        @Named("event-aggregator") builder: ConfiguredStreamBuilder,
        topologyDescription: EventTopologyDescription<String, String, Event, Markt, *, *, EventValidation>,
        @Named("event-aggregator") eventAggregator: DelegatingEventAggregator<String, String, Event, Markt, EventValidation>
    ): KStream<String, *> {
        return eventSourcingStreams.eventAggregator(
            builder,
            topologyDescription,
            eventAggregator,
            skipFeedbackStoreCreation = true
        )
    }
}