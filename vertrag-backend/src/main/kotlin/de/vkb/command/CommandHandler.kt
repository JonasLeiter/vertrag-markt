package de.vkb.command

import de.vkb.event.*
import de.vkb.kafka.InternalEventProducer
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
class CommandHandler(private val internalEventProducer: InternalEventProducer, private val commandValidator: CommandValidator) {

    @Topic("learning-vertrag-command")
    fun receive(command: VertragCommand) {
        if (commandValidator.validate(command).valid) {

            // transformation to event
            val internalEvent = VertragInternalEvent(
                id = command.id,
                aggregateIdentifier = command.aggregateIdentifier.copy(),
                payload = command.payload.copy(),
                type = EventType.VERTRAG_ERSTELLT
            )
            internalEventProducer.send(internalEvent.aggregateIdentifier.id, internalEvent)
        }
    }
}


//dispatchSErvice, event handler hat when statement um zu entscheiden welched command reinkommt und welcher dispatch service entsprechend aufgerufen wird
// evtl schönere Lösung möglich!?