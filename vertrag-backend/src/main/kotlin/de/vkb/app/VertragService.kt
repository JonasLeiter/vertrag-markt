package de.vkb.app

import de.vkb.command.*
import de.vkb.common.AggregateIdentifier
import de.vkb.kafka.CommandProducer
import de.vkb.models.Vertrag
import jakarta.inject.Singleton
import java.util.*

@Singleton
class VertragService(private val commandProducer: CommandProducer) {

    fun vertragErstellen(vertrag: Vertrag) {

        val commandId = UUID.randomUUID().toString()
        val vertragId = UUID.randomUUID().toString()

        val command = VertragCommand(
            id = commandId,
            aggregateIdentifier = AggregateIdentifier(
                id = vertragId,
                type = "Vertrag"
            ),
            payload = vertrag,
            type = CommandType.ERSTELLE_VERTRAG
        )

        commandProducer.send(command.id, command)
    }

    fun beginnAendern(vertrag: Vertrag) {

        // todo
        // val command = VertragCommand()
        // commandProducer.send(command.id, command)
    }

    fun endeAendern(vertrag: Vertrag) {

//        todo
//        val command = VertragCommand()
//        commandProducer.send(command.id, command)
    }

}