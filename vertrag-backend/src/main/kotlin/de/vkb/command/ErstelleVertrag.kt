package de.vkb.command

import de.vkb.common.AggregateIdentifier
import de.vkb.common.ExecutionResult
import de.vkb.models.Vertrag

class ErstelleVertrag(
    val id: String,
    val aggregateIdentifier: AggregateIdentifier,
    val payload: Vertrag) {



    fun execute(): ExecutionResult {
        val validationResult = CommandValidator().validate(this)
        if(validationResult.valid) return ExecutionResult(validationResult.commandId, true)
        return ExecutionResult(validationResult.commandId, false)
    }
}