package de.vkb.model.result.command

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.model.event.DatumGeandert
import de.vkb.model.validation.CommandValidation

class AendereDatumResult(
    override val feedback: CommandValidation?,
    override val event: DatumGeandert?,
): GenericCommandHandlerResult<DatumGeandert, CommandValidation>