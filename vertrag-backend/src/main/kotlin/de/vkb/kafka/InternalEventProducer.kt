package de.vkb.kafka

import de.vkb.event.InternalEvent
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface InternalEventProducer {

    @Topic("learning-vertrag-internal-event")
    fun send(@KafkaKey key: String, value: InternalEvent)
}