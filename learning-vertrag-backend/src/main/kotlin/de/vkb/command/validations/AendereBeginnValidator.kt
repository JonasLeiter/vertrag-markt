package de.vkb.command.validations

import de.vkb.command.commands.AendereBeginn
import de.vkb.common.ValidationType
import de.vkb.event.events.BeginnGeaendert
import de.vkb.event.events.BeginnGeaendertPayload
import de.vkb.laser.es.dto.impl.CommandHandlerResult
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class AendereBeginnValidator {
    fun validate(command: AendereBeginn): CommandHandlerResult<BeginnGeaendert, CommandValidation> =
        if (command.payload.beginn.isBefore(LocalDate.now())) {
            val validation = CommandValidation(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Vertragsbeginn darf nicht in der Vergangenheit liegen",
                hasErrors = true
            )
            CommandHandlerResult(null, validation)
        } else {
            val event = BeginnGeaendert(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                payload = BeginnGeaendertPayload(
                    vertragId = command.aggregateId,
                    beginn = command.payload.beginn
                )
            )
            CommandHandlerResult(event, null)
        }
}