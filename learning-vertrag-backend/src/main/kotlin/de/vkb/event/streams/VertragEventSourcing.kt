package de.vkb.event.streams

import de.vkb.command.commands.Command
import de.vkb.command.validations.CommandValidation
import de.vkb.event.events.Event
import de.vkb.event.validations.EventValidation
import de.vkb.kafka.StoreConfig
import de.vkb.laser.es.config.impl.CqrsTopologyDescriptionBuilder
import de.vkb.laser.es.config.impl.DefaultSerdeFactories
import de.vkb.laser.es.processor.event.DelegatingEventAggregator
import de.vkb.laser.es.processor.event.PickyEventAggregator
import de.vkb.models.Vertrag
import de.vkb.kafka.TopicConfig
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import de.vkb.laser.es.model.CommandContext
import de.vkb.laser.es.processor.command.DelegatingCommandHandler
import de.vkb.laser.es.processor.command.PickyCommandHandler
import jakarta.inject.Named
import jakarta.inject.Singleton

@Singleton
class VertragEventSourcing(
    commandHandlers: Collection<PickyCommandHandler<String, String, CommandContext, Command, Event, CommandValidation>>,
    eventAggregators: Collection<PickyEventAggregator<String, String, Event, Vertrag, EventValidation>>,
    topicConfig: TopicConfig,
    storeConfig: StoreConfig,
    jacksonSerdes: JacksonSerdeFactoryBean,
) {
    val commandHandler: DelegatingCommandHandler<String, String, CommandContext, Command, Event, CommandValidation>
    val eventAggregator: DelegatingEventAggregator<String, String, Event, Vertrag, EventValidation>

    val commandHandlerTopologyDescription = CqrsTopologyDescriptionBuilder()
        .correlationIdSerdeFactory(DefaultSerdeFactories.Strings)
        .aggregateIdSerdeFactory(DefaultSerdeFactories.Strings)
        .commandTopicName(topicConfig.command)
        .commandSerdeFactory(jacksonSerdes.of(Command::class.java))
        .internalEventTopicName(topicConfig.internalEvent)
        .eventSerdeFactory(jacksonSerdes.of(Event::class.java))
        .feedbackTopicName(topicConfig.validation)
        .feedbackSerdeFactory(jacksonSerdes.of(CommandValidation::class.java))
        .correlationIdFromCommand { cmd: Command -> cmd.commandId }
        .aggregateIdFromEvent { evt: Event -> evt.aggregateId }
        .buildCommandHandlerTopologyDescription()

    val eventAggregateTopologyDescription = CqrsTopologyDescriptionBuilder()
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
        commandHandler = DelegatingCommandHandler(commandHandlers)
        eventAggregator = DelegatingEventAggregator(eventAggregators)
    }
}