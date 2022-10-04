package de.vkb.command

import de.vkb.common.ValidationResult

class CommandValidator {

    fun validate(command: VertragCommand): ValidationResult {
        if(command.payload.beginn < command.payload.ende) return ValidationResult(command.commandId, true)
        return ValidationResult(command.commandId, false)
    }
}