package de.vkb.model.result.command

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.model.event.MarktErstellt
import de.vkb.model.validation.CommandValidation

class ErstelleMarktResult(
    override val feedback: CommandValidation?,
    override val event: MarktErstellt?,
): GenericCommandHandlerResult<MarktErstellt, CommandValidation>