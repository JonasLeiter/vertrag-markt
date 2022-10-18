package de.vkb.streams.command

import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.AendereDatum
import de.vkb.model.command.AendereOrt
import de.vkb.model.command.Command
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.*
import de.vkb.model.result.CommandResult
import de.vkb.model.validation.CommandValidation
import jakarta.inject.Singleton
import java.time.LocalDate
@Singleton
class CommandValidator{

    fun validateCreateCommand(command: ErstelleMarkt, vertrag: Vertrag?): CommandResult{
        var validation = CommandValidation(
            commandId = command.commandId,
            isValid = false,
            aggregateIdentifier = command.aggregateIdentifier,
            message = "Unbekannter Command")

        val event = MarktErstellt(
            commandId = command.commandId,
            payload = MarktErstelltPayload(
                ort = command.payload.ort,
                datum = command.payload.datum,
                vertragId = command.payload.vertragId
            ),
            aggregateIdentifier = command.aggregateIdentifier)

        val datum = command.payload.datum
        val today = LocalDate.now()

        if(datum.isAfter(today)){
            validation = validation.copy(message = "ErstelleMarkt Command ungültig - Datum liegt in der Zukunft")
        } else {
            if(vertrag == null){
                validation = validation.copy(message = "Erstelle Markt Command ungültig - Unbekannter Vertrag")
            } else {
                validation = validation.copy(isValid = true, message = "ErstelleMarkt Command gültig")

            }
        }

        return if(validation.isValid){
            CommandResult(validation, event)
        } else {
            CommandResult(validation, null)
        }

    }

    fun validateUpdateCommand(command: Command): CommandResult{
        var validation = CommandValidation(
            commandId = command.commandId,
            isValid = false,
            aggregateIdentifier = command.aggregateIdentifier,
            message = "Unbekannter Command")

        return when(command) {
            is AendereDatum -> {
                val datum = command.payload.datum
                if(datum.isAfter(LocalDate.now())){
                    validation = validation.copy(message = "AendereDatum Command ungültig - Datum liegt in der Zukunft")
                    CommandResult(validation, null)
                } else{
                    validation = validation.copy(isValid = true, message = "AendereDatum Command gültig")
                    val event = DatumGeandert(
                        commandId = command.commandId,
                        payload = DatumGeandertPayload(
                            id = command.payload.id,
                            datum = command.payload.datum
                        ),
                        aggregateIdentifier = command.aggregateIdentifier)
                    CommandResult(validation, event)
                }
            }
            is AendereOrt -> {
                validation = validation.copy(isValid = true, message = "AendereOrt Command gültig")
                val event =
                    OrtGeandert(
                        commandId = command.commandId,
                        payload = OrtGeandertPayload(
                            id = command.payload.id,
                            ort = command.payload.ort
                        ),
                        aggregateIdentifier = command.aggregateIdentifier)
                CommandResult(validation, event)
            }
            else -> {
                CommandResult(validation, null)
            }
        }
    }
}