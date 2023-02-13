package de.vkb.streams.command.handler

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import de.vkb.model.command.AendereDatum
import de.vkb.model.event.DatumGeandert
import de.vkb.model.validation.CommandValidation
import de.vkb.streams.command.validator.AendereDatumValidator
import jakarta.inject.Singleton

@Singleton
class AendereDatumCommandHandler(private val aendereDatumValidator: AendereDatumValidator):
    ContextlessStringKeyCastingCommandHandler<AendereDatum, AendereDatum, DatumGeandert, CommandValidation>(AendereDatum::class.java) {

    override fun processCasted(aendereDatum: AendereDatum): GenericCommandHandlerResult<DatumGeandert, CommandValidation> {
         return aendereDatumValidator.validateAendereDatum(aendereDatum)
    }

}