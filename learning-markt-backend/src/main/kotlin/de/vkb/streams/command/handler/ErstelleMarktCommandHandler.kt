package de.vkb.streams.command.handler

import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import de.vkb.model.event.MarktErstellt
import de.vkb.model.validation.CommandValidation
import de.vkb.streams.command.CommandAndVertrag
import de.vkb.streams.command.validator.ErstelleMarktValidator
import jakarta.inject.Singleton

@Singleton
class ErstelleMarktCommandHandler(private val commandValidator: ErstelleMarktValidator):
    ContextlessStringKeyCastingCommandHandler<CommandAndVertrag, CommandAndVertrag, MarktErstellt, CommandValidation>(CommandAndVertrag::class.java) {

    override fun processCasted(commandAndVertrag: CommandAndVertrag): GenericCommandHandlerResult<MarktErstellt, CommandValidation> {
        return commandValidator.validateErstelleMarkt(commandAndVertrag.command, commandAndVertrag.vertrag)
    }

}