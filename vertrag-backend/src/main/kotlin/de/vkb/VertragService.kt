package de.vkb

import de.vkb.kafka.ProducerClient
import jakarta.inject.Singleton

@Singleton
class VertragService(private val producerClient: ProducerClient) {

    fun send(vertrag: Vertrag): Vertrag {
        producerClient.send("Vertrag-123", vertrag)
        return vertrag
    }
}