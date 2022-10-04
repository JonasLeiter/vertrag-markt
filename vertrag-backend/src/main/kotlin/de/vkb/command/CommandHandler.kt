package de.vkb.command

import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
class CommandHandler() {

    @Topic("learning-vertrag-command")
    fun receive(command: ErstelleVertrag) {
        val executionResult = command.execute()
        if(executionResult.successful) {

        }
    }
}