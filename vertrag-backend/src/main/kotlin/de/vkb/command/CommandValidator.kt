package de.vkb.command

import de.vkb.common.ValidationResult

class CommandValidator {

    fun validate(command: ErstelleVertrag): ValidationResult {
        if (command.payload.beginn < command.payload.ende) {
            return ValidationResult(command.id, true)

        }
        return ValidationResult(command.id, false)
    }
}