package de.vkb.command.validations

import de.vkb.command.commands.AendereBeginn
import de.vkb.common.ValidationType
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class AendereBeginnValidator {
    fun validate(command: AendereBeginn): CommandValidation {
        val validation = CommandValidation(
            commandId = command.commandId,
            aggregateId = command.aggregateId,
            isValid = false,
            validationType = ValidationType.Ungueltig,
            exception = "",
            hasErrors = true
        )
        return if (command.payload.beginn.isBefore(LocalDate.now()))
            validation.copy(
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Vertragsbeginn darf nicht in der Vergangenheit liegen"

            )
        else validation.copy(
            isValid = true,
            validationType = ValidationType.Gueltig,
            hasErrors = false
        )
    }
}