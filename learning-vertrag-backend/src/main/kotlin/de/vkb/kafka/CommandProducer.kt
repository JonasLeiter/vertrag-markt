package de.vkb.kafka

import de.vkb.command.commands.Command
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient()
interface CommandProducer {

    @Topic("\${topics.command}")
    fun send(@KafkaKey key: String, value: Command)
}