package de.vkb.streams.command.handler

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import de.vkb.model.aggregate.Vertrag
import de.vkb.model.command.ErstelleMarkt
import de.vkb.model.event.MarktErstellt
import de.vkb.model.validation.CommandValidation
import de.vkb.streams.command.validator.ErstelleMarktValidator
import jakarta.inject.Singleton

@Singleton
class ErstelleMarktCommandHandler(private val commandValidator: ErstelleMarktValidator):
    ContextlessStringKeyCastingCommandHandler<Pair<*, *>, Pair<*, *>, MarktErstellt, CommandValidation>(Pair::class.java) {

    override fun processCasted(pair: Pair<*, *>): GenericCommandHandlerResult<MarktErstellt, CommandValidation> {
        return commandValidator.validateErstelleMarkt(pair.first as ErstelleMarkt, pair.second as Vertrag)
    }

}