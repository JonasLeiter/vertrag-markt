package de.vkb.model.result.command

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.model.event.OrtGeandert
import de.vkb.model.validation.CommandValidation

class AendereOrtResult(
    override val feedback: CommandValidation?,
    override val event: OrtGeandert?,
): GenericCommandHandlerResult<OrtGeandert, CommandValidation>