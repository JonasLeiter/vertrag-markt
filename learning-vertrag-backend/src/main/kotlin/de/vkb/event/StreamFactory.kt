package de.vkb.event.events

import de.vkb.event.EventValidation
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.models.Vertrag
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.apache.kafka.streams.kstream.KStream

@Factory
class StreamFactory(
    private val eventSourcingStreams: EventSourcingStreamFactory,
    private val vertragEventSourcing: VertragEventSourcing,
    private val jacksonSerdes: JacksonSerdeFactoryBean
) {
    @Singleton
    fun buildEventAggregatorTopology(
        builder: ConfiguredStreamBuilder,
        eventAggregator: DelegatingEventAggregator<String, String, Event, Vertrag, EventValidation>
    ): KStream<String, *> =
        eventSourcingStreams.eventAggregator(
            builder = builder,
            topologyDescription = vertragEventSourcing.topologyDescription,
            eventAggregator = vertragEventSourcing.eventAggregator,
            skipFeedbackStoreCreation = false
        )
}