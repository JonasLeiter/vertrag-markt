package de.vkb.app

import de.vkb.command.*
import de.vkb.common.AggregateIdentifier
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
            id = commandId,
            aggregateIdentifier = AggregateIdentifier(
                id = vertragId,
                type = "Vertrag"
            ),
            payload = payload,
            type = CommandType.ERSTELLE_VERTRAG
        )

        commandProducer.send(command.id, command)

        return Vertrag(id = vertragId, bezeichnung = payload.bezeichnung, beginn = payload.beginn, ende = payload.ende)
    }

    fun beginnAendern(payload: AendereBeginnPayload) {

        val commandId = UUID.randomUUID().toString()

        val command = AendereBeginn(
            id = commandId,
            aggregateIdentifier = AggregateIdentifier(
                id = payload.id,
                type = "Vertrag"
            ),
            payload = payload,
            type = CommandType.AENDERE_BEGINN
        )

        commandProducer.send(command.id, command)
    }

    fun endeAendern(payload: AendereEndePayload) {

        val commandId = UUID.randomUUID().toString()

        val command = AendereEnde(
            id = commandId,
            aggregateIdentifier = AggregateIdentifier(
                id = payload.id,
                type = "Vertrag"
            ),
            payload = payload,
            type = CommandType.AENDERE_ENDE
        )

        commandProducer.send(command.id, command)
    }

}