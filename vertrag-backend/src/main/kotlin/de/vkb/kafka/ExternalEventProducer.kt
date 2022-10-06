package de.vkb.kafka

import de.vkb.event.ExternalEvent
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface ExternalEventProducer {

    @Topic("learning-vertrag-external-event")
    fun send(@KafkaKey key: String, value: ExternalEvent)
}