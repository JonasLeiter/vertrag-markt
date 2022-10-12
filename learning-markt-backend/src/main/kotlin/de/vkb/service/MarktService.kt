package de.vkb.service

import de.vkb.model.command.*
import de.vkb.model.result.MarktResult
import de.vkb.producer.CommandProducer
import jakarta.inject.Singleton
import java.util.*

@Singleton
class MarktService(private val commandProducer: CommandProducer) {

    fun saveMarkt(markt: ErstelleMarktPayload): MarktResult {
        val commandId = UUID.randomUUID().toString()
        val aggregateId = UUID.randomUUID().toString()

        val marktCommand = ErstelleMarkt(
            commandId = commandId,
            aggregateIdentifier = aggregateId,
            payload = markt,
        )

        commandProducer.sendMarktCommand(marktCommand.commandId, marktCommand)

        return MarktResult(aggregateId = aggregateId, "ok")
    }

    fun changeOrt(markt: AendereOrtPayload): MarktResult{
        val commandId = UUID.randomUUID().toString()

        val marktCommand = AendereOrt(
            commandId = commandId,
            aggregateIdentifier = markt.id,
            payload = markt,
        )

        commandProducer.sendMarktCommand(marktCommand.commandId, marktCommand)

        return MarktResult(aggregateId = markt.id, "ok")
    }

    fun changeDatum(markt: AendereDatumPayload): MarktResult{
        val commandId = UUID.randomUUID().toString()

        val marktCommand = AendereDatum(
            commandId = commandId,
            aggregateIdentifier = markt.id,
            payload = markt,
        )

        commandProducer.sendMarktCommand(marktCommand.commandId, marktCommand)

        return MarktResult(aggregateId = markt.id, "ok")
    }

}