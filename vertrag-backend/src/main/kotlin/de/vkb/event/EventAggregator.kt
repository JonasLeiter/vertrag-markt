package de.vkb.event

import de.vkb.kafka.ExternalEventProducer
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaListener
class EventAggregator(val externalEventProducer: ExternalEventProducer) {

    @Topic("learning-vertrag-internal-event")
    fun receive(event: InternalEvent) {
        // validate event
        // update aggreagate
        // transform to external event
        // produce to external-event-topic
    }

}