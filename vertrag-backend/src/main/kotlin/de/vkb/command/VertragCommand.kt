package de.vkb.command

import de.vkb.common.AggregateIdentifier
import de.vkb.models.Vertrag

data class VertragCommand (
    override val id: String,
    override val aggregateIdentifier: AggregateIdentifier,
    override val payload: Vertrag,
    override val type: CommandType
) : Command
