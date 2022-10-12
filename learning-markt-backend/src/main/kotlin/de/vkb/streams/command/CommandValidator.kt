package de.vkb.streams.command

import de.vkb.model.command.AendereDatum
import de.vkb.model.command.AendereOrt
import de.vkb.model.command.Command
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.*
import de.vkb.model.result.CommandResult
import de.vkb.model.validation.CommandValidation
import java.time.LocalDate

class CommandValidator{

    fun validate(command: Command): CommandResult{
        return when(command) {
            is ErstelleMarkt -> {
                val result = validate(command)
                val event = MarktErstellt(
                    commandId = command.commandId,
                    payload = MarktErstelltPayload(
                        ort = command.payload.ort,
                        datum = command.payload.datum
                    ),
                    aggregateIdentifier = command.aggregateIdentifier)
                CommandResult(result, event)
            }
            is AendereDatum -> {
                val result = validate(command)
                val event = DatumGeandert(
                    commandId = command.commandId,
                    payload = DatumGeandertPayload(
                        id = command.payload.id,
                        datum = command.payload.datum
                    ),
                    aggregateIdentifier = command.aggregateIdentifier)
                CommandResult(result, event)
            }
            is AendereOrt -> {
                val result = validate(command)
                val event =
                    OrtGeandert(
                        commandId = command.commandId,
                        payload = OrtGeandertPayload(
                            id = command.payload.id,
                            ort = command.payload.ort
                        ),
                        aggregateIdentifier = command.aggregateIdentifier)
                CommandResult(result, event)
            }
            else -> {
                val result = CommandValidation(
                    commandId = command.commandId,
                    isValid = false,
                    aggregateIdentifier = command.aggregateIdentifier,
                    message = "Unbekannter Fehler")
                CommandResult(result, null)
            }
        }
    }

    private fun validate(erstelleMarkt: ErstelleMarkt): CommandValidation {
        val datum = erstelleMarkt.payload.datum
        val today = LocalDate.now()

        if(datum.isEqual(today) || datum.isBefore(today)){
            return CommandValidation(
                    commandId = erstelleMarkt.commandId,
                    isValid = true,
                    aggregateIdentifier = erstelleMarkt.aggregateIdentifier,
                    message = "Markt gültig")
        } else {
            return CommandValidation(
                commandId = erstelleMarkt.commandId,
                isValid = false,
                aggregateIdentifier = erstelleMarkt.aggregateIdentifier,
                message = "Markt ungültig - Datum liegt in der Zukunft")
        }
    }

    private fun validate(aendereDatum: AendereDatum): CommandValidation {
        val datum = aendereDatum.payload.datum;
        val today = LocalDate.now()

        if(datum.isEqual(today) || datum.isBefore(today)){
            return CommandValidation(
                    commandId = aendereDatum.commandId,
                    isValid = true,
                    aggregateIdentifier = aendereDatum.aggregateIdentifier,
                    message = "Markt gültig")
        } else {
            return CommandValidation(
                    commandId = aendereDatum.commandId,
                    isValid = false,
                    aggregateIdentifier = aendereDatum.aggregateIdentifier,
                    message = "Markt ungültig - Datum liegt in der Zukunft")
        }
    }

    private fun validate(aendereOrt: AendereOrt): CommandValidation {
        return  CommandValidation(
            commandId = aendereOrt.commandId,
            isValid = true,
            aggregateIdentifier = aendereOrt.aggregateIdentifier,
            message = "Markt gültig")
    }
}