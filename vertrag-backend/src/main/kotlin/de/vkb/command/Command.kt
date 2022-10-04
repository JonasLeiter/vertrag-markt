package de.vkb.command

import de.vkb.models.Vertrag
import de.vkb.common.ExecutionResult

interface Command {
    val commandId: String
    val payload: Vertrag

    fun execute(): ExecutionResult
}