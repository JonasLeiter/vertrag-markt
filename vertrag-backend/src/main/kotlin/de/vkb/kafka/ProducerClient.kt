package de.vkb.kafka

import de.vkb.Vertrag
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface ProducerClient {

    @Topic("learning-vertrag-test")
    fun send(@KafkaKey key: String, value: Vertrag)
}