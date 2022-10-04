package de.vkb.kafka

import de.vkb.command.VertragCommand
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient
interface ProducerClient {

    @Topic("learning-vertrag-command")
    fun send(@KafkaKey key: String, value: VertragCommand)
}