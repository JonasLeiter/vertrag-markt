package de.vkb.streams.command.validator

import de.vkb.model.command.AendereOrt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.event.OrtGeandertPayload
import de.vkb.model.result.command.AendereOrtResult
import de.vkb.model.validation.CommandValidation
import jakarta.inject.Singleton

@Singleton
class AendereOrtValidator {

    fun validateAendereOrt(aendereOrt: AendereOrt): AendereOrtResult{
        val event =
            OrtGeandert(
                commandId = aendereOrt.commandId,
                payload = OrtGeandertPayload(
                    id = aendereOrt.payload.id,
                    ort = aendereOrt.payload.ort
                ),
                aggregateIdentifier = aendereOrt.aggregateIdentifier)

        return AendereOrtResult(null, event)
    }
}
