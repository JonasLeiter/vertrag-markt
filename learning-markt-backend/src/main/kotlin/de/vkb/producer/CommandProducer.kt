package de.vkb.producer

import de.vkb.model.command.Command
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient(acks = KafkaClient.Acknowledge.ALL)
interface CommandProducer {

    @Topic("\${topics.command}")
    fun sendMarktCommand(@KafkaKey commandId: String, command: Command);


}