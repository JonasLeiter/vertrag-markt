package de.vkb.model.result

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.model.event.Event
import de.vkb.model.validation.CommandValidation

data class CommandResult(
    override val feedback: CommandValidation,
    override val event: Event?,
): GenericCommandHandlerResult<Event, CommandValidation>