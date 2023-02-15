package de.vkb.command.validations

import de.vkb.command.commands.AendereEnde
import de.vkb.common.ValidationType
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class AendereEndeValidator {
    fun validate(command: AendereEnde): CommandValidation {
        val validation = CommandValidation(
            commandId = command.commandId,
            aggregateId = command.aggregateId,
            isValid = false,
            validationType = ValidationType.Ungueltig,
            exception = "",
            hasErrors = true
        )
        return if (command.payload.ende.isBefore(LocalDate.now()))
            validation.copy(
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Vertragsende darf nicht in der Vergangenheit liegen",
            )
        else validation.copy(
            isValid = true,
            validationType = ValidationType.Gueltig,
            hasErrors = false
        )
    }
}