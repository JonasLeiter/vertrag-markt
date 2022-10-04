package de.vkb

import de.vkb.command.VertragCommand
import de.vkb.kafka.ProducerClient
import jakarta.inject.Singleton

@Singleton
class VertragService(private val producerClient: ProducerClient) {

    fun send(vertrag: Vertrag): Vertrag {
        val command = VertragCommand(vertrag)
        producerClient.send(command.commandId, command)
        return vertrag
    }
}