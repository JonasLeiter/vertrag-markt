package de.vkb.streams

import de.vkb.config.StoreConfig
import de.vkb.config.TopicConfig
import de.vkb.laser.es.config.impl.CqrsTopologyDescriptionBuilder
import de.vkb.laser.es.config.impl.DefaultSerdeFactories
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import de.vkb.laser.es.model.CommandContext
import de.vkb.laser.es.processor.command.DelegatingCommandHandler
import de.vkb.laser.es.processor.command.PickyCommandHandler
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.laser.es.processor.event.PickyEventAggregator
import de.vkb.model.aggregate.Markt
import de.vkb.model.command.Command
import de.vkb.model.event.Event
import de.vkb.model.validation.CommandValidation
import de.vkb.model.validation.EventValidation
import de.vkb.streams.command.CommandAndVertrag
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
class MarktEventSourcing(
    topicConfig: TopicConfig,
    storeConfig: StoreConfig,
    jacksonSerdes: JacksonSerdeFactoryBean,
    createCommandHandlers: Collection<PickyCommandHandler<String, String, CommandContext, CommandAndVertrag, Event, CommandValidation>>,
    updateCommandHandlers: Collection<PickyCommandHandler<String, String, CommandContext, Command, Event, CommandValidation>>,
    aggregators: Collection<PickyEventAggregator<String, String, Event, Markt, EventValidation>>
) {

    val createCommandHandler: DelegatingCommandHandler<String, String, CommandContext, CommandAndVertrag, Event, CommandValidation>
    val updateCommandHandler: DelegatingCommandHandler<String, String, CommandContext, Command, Event, CommandValidation>

    @Named("event-aggregator")
    val eventAggregator: DelegatingEventAggregator<String, String, Event, Markt, EventValidation>

    val eventTopologyDescription =
        CqrsTopologyDescriptionBuilder()
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

    val commandTopologyDescription =
        CqrsTopologyDescriptionBuilder()
            .correlationIdSerdeFactory(DefaultSerdeFactories.Strings)
            .aggregateIdSerdeFactory(DefaultSerdeFactories.Strings)
            .commandTopicName(topicConfig.command)
            .commandSerdeFactory(jacksonSerdes.of(Command::class.java))
            .internalEventTopicName(topicConfig.internalEvent)
            .eventSerdeFactory(jacksonSerdes.of(Event::class.java))
            .feedbackTopicName(topicConfig.validation)
            .feedbackSerdeFactory(jacksonSerdes.of(CommandValidation::class.java))
            .correlationIdFromCommand { cmd: Command -> cmd.commandId }
            .aggregateIdFromEvent { evt: Event -> evt.aggregateIdentifier }
            .buildCommandHandlerTopologyDescription()

    init {
        createCommandHandler = DelegatingCommandHandler(createCommandHandlers)
        updateCommandHandler = DelegatingCommandHandler(updateCommandHandlers)
        eventAggregator = DelegatingEventAggregator(aggregators)
    }
}