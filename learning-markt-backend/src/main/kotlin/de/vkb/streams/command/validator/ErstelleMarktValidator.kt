package de.vkb.streams.command.validator

import de.vkb.laser.es.dto.impl.CommandHandlerResult
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.MarktErstellt
import de.vkb.model.event.MarktErstelltPayload
import de.vkb.model.validation.CommandValidation
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class ErstelleMarktValidator {

    fun validateErstelleMarkt(command: ErstelleMarkt, vertrag: Vertrag?): CommandHandlerResult<MarktErstellt, CommandValidation> {
        var validation = CommandValidation(
            commandId = command.commandId,
            hasErrors = true,
            aggregateIdentifier = command.aggregateIdentifier,
            message = "ErstelleMarkt Command ungültig - Unbekannter Fehler")

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
                validation = validation.copy(hasErrors = false, message = "ErstelleMarkt Command gültig")

            }
        }

        return if(validation.hasErrors){
            CommandHandlerResult(event = null, feedback = validation)
        } else {
            CommandHandlerResult(event = event, feedback = null)
        }

    } }