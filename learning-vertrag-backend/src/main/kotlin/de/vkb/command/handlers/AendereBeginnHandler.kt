package de.vkb.command.handlers

import de.vkb.command.commands.AendereBeginn
import de.vkb.command.commands.Command
import de.vkb.command.validations.AendereBeginnValidator
import de.vkb.command.validations.CommandValidation
import de.vkb.event.events.BeginnGeaendert
import de.vkb.event.events.BeginnGeaendertPayload
import de.vkb.laser.es.dto.GenericCommandHandlerResult
import de.vkb.laser.es.dto.impl.CommandHandlerResult
import de.vkb.laser.es.processor.command.ContextlessStringKeyCastingCommandHandler
import jakarta.inject.Singleton

@Singleton
class AendereBeginnHandler(private val validator: AendereBeginnValidator) :
    ContextlessStringKeyCastingCommandHandler<AendereBeginn, Command, BeginnGeaendert, CommandValidation>(
        AendereBeginn::class.java
    ) {

    override fun processCasted(
        command: AendereBeginn
    ): GenericCommandHandlerResult<BeginnGeaendert, CommandValidation> {
        val validation = validator.validate(command)
        return if (validation.isValid) CommandHandlerResult(
            BeginnGeaendert(
                commandId = command.commandId,
                aggregateId = command.aggregateId,
                payload = BeginnGeaendertPayload(
                    vertragId = command.aggregateId,
                    beginn = command.payload.beginn
                )
            ), null
        )
        else CommandHandlerResult(null, validation)
    }
}