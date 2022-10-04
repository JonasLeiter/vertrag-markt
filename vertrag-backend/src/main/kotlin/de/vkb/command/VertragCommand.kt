package de.vkb.command

import de.vkb.models.Vertrag
import de.vkb.common.ExecutionResult
import java.util.UUID

class VertragCommand(override val payload: Vertrag, private val commandValidator: CommandValidator) : Command {

    override val commandId = UUID.randomUUID().toString()

    override fun execute(): ExecutionResult {
        val validationResult = commandValidator.validate(this)
        if(validationResult.valid) return ExecutionResult(validationResult.commandId, true)
        return ExecutionResult(validationResult.commandId, false)
    }
}