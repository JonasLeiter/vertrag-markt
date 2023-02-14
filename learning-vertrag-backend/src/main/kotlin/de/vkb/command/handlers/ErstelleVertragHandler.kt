package de.vkb.command.handlers

import de.vkb.command.commands.Command
import de.vkb.command.commands.ErstelleVertrag
import de.vkb.command.validations.CommandValidation
import de.vkb.command.validations.ErstelleVertragValidator
import de.vkb.event.events.Event
import de.vkb.event.events.VertragErstellt
import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import jakarta.inject.Singleton

@Singleton
class ErstelleVertragHandler(private val validator: ErstelleVertragValidator) :
    ContextlessStringKeyCastingCommandHandler<ErstelleVertrag, Command, VertragErstellt, CommandValidation>(
        ErstelleVertrag::class.java
    ) {

    override fun processCasted(
        command: ErstelleVertrag
    ): GenericCommandHandlerResult<VertragErstellt, CommandValidation> {
        return validator.validate(command)
    }
}