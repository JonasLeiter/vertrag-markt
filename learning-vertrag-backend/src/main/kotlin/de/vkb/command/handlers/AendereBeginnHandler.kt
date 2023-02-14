package de.vkb.command.handlers

import de.vkb.command.commands.AendereBeginn
import de.vkb.command.commands.Command
import de.vkb.command.validations.AendereBeginnValidator
import de.vkb.command.validations.CommandValidation
import de.vkb.event.events.BeginnGeaendert
import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import jakarta.inject.Singleton

@Singleton
class AendereBeginnHandler(private val validator: AendereBeginnValidator) :
    ContextlessStringKeyCastingCommandHandler<AendereBeginn, Command, BeginnGeaendert, CommandValidation>(
        AendereBeginn::class.java
    ) {
    override fun processCasted(command: AendereBeginn): GenericCommandHandlerResult<BeginnGeaendert, CommandValidation> {
        return validator.validate(command)
    }

}