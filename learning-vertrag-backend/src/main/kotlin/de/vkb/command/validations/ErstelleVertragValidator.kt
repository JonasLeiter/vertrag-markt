package de.vkb.command.validations

import de.vkb.command.commands.ErstelleVertrag
import de.vkb.common.ValidationType
import de.vkb.event.events.VertragErstellt
import de.vkb.event.events.VertragErstelltPayload
import de.vkb.laser.es.dto.impl.CommandHandlerResult
import jakarta.inject.Singleton

@Singleton
class ErstelleVertragValidator {
    fun validate(command: ErstelleVertrag): CommandHandlerResult<VertragErstellt, CommandValidation> =
        if (command.payload.ende.isBefore(command.payload.beginn)) {
            val validation = CommandValidation(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Ende liegt vor Beginn",
                hasErrors = true
            )
            CommandHandlerResult(null, validation)
        } else {
            val event = VertragErstellt(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                payload = VertragErstelltPayload(
                    vertragId = command.aggregateId,
                    bezeichnung = command.payload.bezeichnung,
                    beginn = command.payload.beginn,
                    ende = command.payload.ende
                )
            )
            CommandHandlerResult(event, null)
        }
}