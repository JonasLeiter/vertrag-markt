package de.vkb.command.validations

import de.vkb.command.commands.ErstelleVertrag
import de.vkb.common.ValidationType
import jakarta.inject.Singleton

@Singleton
class ErstelleVertragValidator {
    fun validate(command: ErstelleVertrag): CommandValidation {
        val validation = CommandValidation(
            commandId = command.commandId,
            aggregateId = command.aggregateId,
            isValid = false,
            validationType = ValidationType.Ungueltig,
            exception = "",
            hasErrors = true
        )
        return if (command.payload.ende.isBefore(command.payload.beginn))
            validation.copy(
                validationType = ValidationType.UngueltigeEingabe,
                exception = "Ende liegt vor Beginn",
            )
        else validation.copy(
            isValid = true,
            validationType = ValidationType.Gueltig,
            hasErrors = false
        )
    }
}