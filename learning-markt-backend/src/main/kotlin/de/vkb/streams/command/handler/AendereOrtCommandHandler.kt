package de.vkb.streams.command.handler

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import de.vkb.model.command.AendereOrt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.validation.CommandValidation
import de.vkb.streams.command.validator.AendereOrtValidator
import jakarta.inject.Singleton

@Singleton
class AendereOrtCommandHandler(private val aendereOrtValidator: AendereOrtValidator):
    ContextlessStringKeyCastingCommandHandler<AendereOrt, AendereOrt, OrtGeandert, CommandValidation>(AendereOrt::class.java) {

    override fun processCasted(aendereOrt: AendereOrt): GenericCommandHandlerResult<OrtGeandert, CommandValidation> {
         return aendereOrtValidator.validateAendereOrt(aendereOrt)
    }

}