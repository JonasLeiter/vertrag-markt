package de.vkb.streams.command.validator

import de.vkb.laser.es.dto.impl.CommandHandlerResult
import de.vkb.model.command.AendereDatum
import de.vkb.model.event.DatumGeandert
import de.vkb.model.event.DatumGeandertPayload
import de.vkb.model.validation.CommandValidation
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class AendereDatumValidator {
    
    fun validateAendereDatum(aendereDatum: AendereDatum): CommandHandlerResult<DatumGeandert, CommandValidation> {
        val datum = aendereDatum.payload.datum
        return if(datum.isAfter(LocalDate.now())){
            var validation = CommandValidation(
                commandId = aendereDatum.commandId,
                hasErrors = true,
                aggregateIdentifier = aendereDatum.aggregateIdentifier,
                message = "AendereDatum ungültig - Datum liegt in der Zukunft")
           CommandHandlerResult(event = null, feedback = validation)
        } else{
            val event = DatumGeandert(
                commandId = aendereDatum.commandId,
                payload = DatumGeandertPayload(
                    id = aendereDatum.payload.id,
                    datum = aendereDatum.payload.datum
                ),
                aggregateIdentifier = aendereDatum.aggregateIdentifier)
            CommandHandlerResult(event = event , feedback = null)
        }
    }

}
