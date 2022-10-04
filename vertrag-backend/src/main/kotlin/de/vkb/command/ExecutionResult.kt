package de.vkb.command

interface ExecutionResult {
    val commandId: String
    val successful: Boolean
}