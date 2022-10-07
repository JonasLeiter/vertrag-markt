package de.vkb.command

import java.util.Date

data class AendereEnde(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: AendereEndePayload,
) : Command

data class AendereEndePayload(
    val vertragId: String,
    val ende: Date
)
