package de.vkb.command

import de.vkb.common.AggregateIdentifier
import java.util.Date

data class AendereEnde(
    override val id: String,
    override val aggregateIdentifier: AggregateIdentifier,
    override val payload: AendereEndePayload,
    override val type: CommandType
) : Command

data class AendereEndePayload(
    val id: String,
    val ende: Date
) : Payload
