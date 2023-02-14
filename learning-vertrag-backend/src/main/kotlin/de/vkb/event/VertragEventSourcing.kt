package de.vkb.event

import de.vkb.event.events.Event
import de.vkb.event.validation.EventValidation
import de.vkb.kafka.StoreConfig
import de.vkb.laser.es.config.impl.CqrsTopologyDescriptionBuilder
import de.vkb.laser.es.config.impl.DefaultSerdeFactories
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.laser.es.processor.event.PickyEventAggregator
import de.vkb.models.Vertrag
import de.vkb.kafka.TopicConfig
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
class VertragEventSourcing(
//    commandHandlers: Collection<PickyCommandHandler<String, String, CommandContext, MyCommandClass, MyEventClass, MyFeedbackClass>>,
    eventAggregators: Collection<PickyEventAggregator<String, String, Event, Vertrag, EventValidation>>,
    topicConfig: TopicConfig,
    storeConfig: StoreConfig,
    jacksonSerdes: JacksonSerdeFactoryBean,
) {
//    val commandHandler: DelegatingCommandHandler<String, String, CommandContext, MyCommandClass, MyEventClass, MyFeedbackClass>
    @Named("event-aggregator")
    val eventAggregator: DelegatingEventAggregator<String, String, Event, Vertrag, EventValidation>
    val topologyDescription = CqrsTopologyDescriptionBuilder()
        .correlationIdSerdeFactory(DefaultSerdeFactories.Strings)
        .aggregateIdSerdeFactory(DefaultSerdeFactories.Strings)
        .internalEventTopicName(topicConfig.internalEvent)
        .externalEventTopicName(topicConfig.externalEvent)
        .eventSerdeFactory(jacksonSerdes.of(Event::class.java))
        .aggregateStoreName(storeConfig.vertragStore)
        .aggregateSerdeFactory(jacksonSerdes.of(Vertrag::class.java))
        .stateTopicName(topicConfig.state)
        .stateSerdeFactory(jacksonSerdes.of(Vertrag::class.java))
        .feedbackTopicName(topicConfig.validation)
        .feedbackSerdeFactory(jacksonSerdes.of(EventValidation::class.java))
        .correlationIdFromEvent { evt: Event -> evt.commandId }
        .aggregateIdFromEvent { evt: Event -> evt.aggregateId }
        .stateFromAggregate { agg: Vertrag -> agg }
        .buildEventAggregatorTopologyDescription()

    init {
//        commandHandler = DelegatingCommandHandler(commandHandlers)
        eventAggregator = DelegatingEventAggregator(eventAggregators)
    }
}