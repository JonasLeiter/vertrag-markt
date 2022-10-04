package de.vkb.command

import de.vkb.Vertrag

interface Command {
    val commandId: String
    val payload: Vertrag

    fun execute(): ExecutionResult
}