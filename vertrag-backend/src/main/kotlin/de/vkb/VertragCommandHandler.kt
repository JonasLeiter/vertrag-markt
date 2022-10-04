package de.vkb

import de.vkb.command.VertragCommand
import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
class VertragCommandHandler() {

    @Topic("learning-vertrag-command")
    fun receive(command: VertragCommand) {
        val executionResult = command.execute()
        if(executionResult.successful) {

        }
    }
}