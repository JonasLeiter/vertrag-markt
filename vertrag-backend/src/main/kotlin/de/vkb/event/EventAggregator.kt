package de.vkb.event

import de.vkb.kafka.ExternalEventProducer
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaListener
class EventAggregator(private val externalEventProducer: ExternalEventProducer) {

    @Topic("learning-vertrag-internal-event")
    fun receive(internalEvent: VertragInternalEvent) {
        // validate event



        // update aggregate



        // transform to external event
        val externalEvent = VertragExternalEvent(
            id = internalEvent.id,
            aggregateIdentifier = internalEvent.aggregateIdentifier.copy(),
            payload = internalEvent.payload.copy(),
            type = EventType.VERTRAG_ERSTELLT
        )
        // external event actually changes the domain
        externalEvent.payload.id = internalEvent.aggregateIdentifier.id


        // produce to external-event-topic
        externalEventProducer.send(externalEvent.aggregateIdentifier.id, externalEvent)
    }

}