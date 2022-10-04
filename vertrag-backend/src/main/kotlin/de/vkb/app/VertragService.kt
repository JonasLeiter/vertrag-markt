package de.vkb.app

import de.vkb.command.ErstelleVertrag
import de.vkb.common.AggregateIdentifier
import de.vkb.kafka.ProducerClient
import de.vkb.models.Vertrag
import jakarta.inject.Singleton
import java.util.*

@Singleton
class VertragService(private val producerClient: ProducerClient) {

    fun send(vertrag: Vertrag) {

        val commandId = UUID.randomUUID().toString()
        val vertragId = UUID.randomUUID().toString()

        val command = ErstelleVertrag(
            id = commandId,
            aggregateIdentifier = AggregateIdentifier(
                id = vertragId,
                type = "Vertrag"
            ),
            payload = vertrag
        )

        producerClient.send(command.aggregateIdentifier.id, command)
    }
}