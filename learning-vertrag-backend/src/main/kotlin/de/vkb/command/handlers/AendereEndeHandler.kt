package de.vkb.command.handlers

import de.vkb.command.commands.AendereEnde
import de.vkb.command.commands.Command
import de.vkb.command.validations.AendereEndeValidator
import de.vkb.command.validations.CommandValidation
import de.vkb.event.events.EndeGeaendert
import de.vkb.event.events.Event
import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import jakarta.inject.Singleton

@Singleton
class AendereEndeHandler(private val validator: AendereEndeValidator) :
    ContextlessStringKeyCastingCommandHandler<AendereEnde, Command, EndeGeaendert, CommandValidation>(
        AendereEnde::class.java
    ) {
    override fun processCasted(
        command: AendereEnde
    ): GenericCommandHandlerResult<EndeGeaendert, CommandValidation> {
        return validator.validate(command)
    }
}