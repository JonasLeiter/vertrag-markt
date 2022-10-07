package de.vkb.command

import java.util.Date

data class ErstelleVertrag(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: ErstelleVertragPayload
) : Command {
}

data class ErstelleVertragPayload(
    val bezeichnung: String,
    val beginn: Date,
    val ende: Date
)
