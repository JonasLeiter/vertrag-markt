package de.vkb.app

import de.vkb.command.commands.*
import de.vkb.kafka.CommandProducer
import de.vkb.models.Vertrag
import jakarta.inject.Singleton
import java.util.*

@Singleton
class VertragService(private val commandProducer: CommandProducer) {

    fun vertragErstellen(payload: ErstelleVertragPayload): Vertrag {

        val commandId = UUID.randomUUID().toString()
        val vertragId = UUID.randomUUID().toString()

        val command = ErstelleVertrag(
            commandId = commandId,
            aggregateId = vertragId,
            payload = payload
        )

        commandProducer.send(command.commandId, command)

        return Vertrag(vertragId, payload.bezeichnung, payload.beginn, payload.ende)
    }

    fun beginnAendern(payload: AendereBeginnPayload) {

        val commandId = UUID.randomUUID().toString()

        val command = AendereBeginn(
            commandId = commandId,
            aggregateId = payload.vertragId,
            payload = payload
        )

        commandProducer.send(command.commandId, command)
    }

    fun endeAendern(payload: AendereEndePayload) {

        val commandId = UUID.randomUUID().toString()

        val command = AendereEnde(
            commandId = commandId,
            aggregateId = payload.vertragId,
            payload = payload
        )

        commandProducer.send(command.commandId, command)
    }
}