package de.vkb.streams.event

import com.fasterxml.jackson.databind.ObjectMapper
import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.laser.es.config.EventTopologyDescription
import de.vkb.laser.es.config.impl.CqrsTopologyDescriptionBuilder
import de.vkb.laser.es.config.impl.DefaultSerdeFactories
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.laser.es.processor.event.PickyEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.Event
import de.vkb.model.validation.EventValidation
import io.micronaut.context.annotation.Factory
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.yaml.snakeyaml.error.Mark

@Factory
class EventSourcingFactory {

    @Singleton
    fun jacksonSerdeFactories(
        objectMapper: ObjectMapper
    ): JacksonSerdeFactoryBean {
        return JacksonSerdeFactoryBean(objectMapper)
    }

    @Singleton
    fun eventSourcingStreams(): EventSourcingStreamFactory {
        return EventSourcingStreamFactory(replayMode = false)
    }

    @Singleton
    @Named("event-aggregator")
    fun eventAggregator(
        aggregators: Collection<PickyEventAggregator<String, String, Event, Markt, EventValidation>>
    ): DelegatingEventAggregator<String, String, Event, Markt, EventValidation> {
        return DelegatingEventAggregator(aggregators)
    }

    @Singleton
    fun eventAggregatorTopology(jacksonSerdes: JacksonSerdeFactoryBean,
                                topicConfig: TopicConfig,
                                storeConfig: StoreConfig
    ): EventTopologyDescription<String, String, Event, Markt, *, *, EventValidation> {
        return CqrsTopologyDescriptionBuilder()
            .correlationIdSerdeFactory(DefaultSerdeFactories.Strings)
            .aggregateIdSerdeFactory(DefaultSerdeFactories.Strings)
            .internalEventTopicName(topicConfig.internalEvent)
            .externalEventTopicName(topicConfig.externalEvent)
            .eventSerdeFactory(jacksonSerdes.of(Event::class.java))
            .aggregateStoreName(storeConfig.stateStore)
            .aggregateSerdeFactory(jacksonSerdes.of(Markt::class.java))
            .stateTopicName(topicConfig.state)
            .stateSerdeFactory(jacksonSerdes.of(Markt::class.java))
            .feedbackTopicName(topicConfig.validation)
            .feedbackSerdeFactory(jacksonSerdes.of(EventValidation::class.java))
            .correlationIdFromEvent { evt: Event -> evt.commandId }
            .aggregateIdFromEvent { evt: Event -> evt.aggregateIdentifier }
            .stateFromAggregate { agg: Markt -> agg }
            .buildEventAggregatorTopologyDescription()
    }
}