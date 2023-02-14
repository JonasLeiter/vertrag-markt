package de.vkb.command.validations

import de.vkb.command.commands.AendereEnde
import de.vkb.common.ValidationType
import de.vkb.event.events.EndeGeaendert
import de.vkb.event.events.EndeGeaendertPayload
import de.vkb.laser.es.dto.impl.CommandHandlerResult
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class AendereEndeValidator {
    fun validate(command: AendereEnde): CommandHandlerResult<EndeGeaendert, CommandValidation> =
        if (command.payload.ende.isBefore(LocalDate.now())) {
            val validation = CommandValidation(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                valid = false,
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Vertragsende darf nicht in der Vergangenheit liegen",
                hasErrors = true
            )
            CommandHandlerResult(null, validation)
        } else {
            val event = EndeGeaendert(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                payload = EndeGeaendertPayload(
                    vertragId = command.aggregateId,
                    ende = command.payload.ende
                )
            )
            CommandHandlerResult(event, null)
        }
}