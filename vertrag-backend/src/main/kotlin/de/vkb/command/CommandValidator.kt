package de.vkb.command

import de.vkb.common.ValidationResult
import jakarta.inject.Singleton

@Singleton
class CommandValidator() {

    fun validate(command: VertragCommand): ValidationResult {
        return if (command.payload.beginn < command.payload.ende) ValidationResult(command.id, true)
        else ValidationResult(command.id, false)
    }
}