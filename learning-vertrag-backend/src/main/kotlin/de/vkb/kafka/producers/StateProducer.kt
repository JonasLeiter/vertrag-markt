package de.vkb.kafka.producers

import de.vkb.models.Vertrag
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient()
interface StateProducer {

    @Topic("\${topics.state}")
    fun send(@KafkaKey key: String, value: Vertrag)
}