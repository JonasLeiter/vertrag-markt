package de.vkb.event.validation

import de.vkb.common.ValidationType
import de.vkb.event.EndeGeaendertResult
import de.vkb.event.events.EndeGeaendert
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class EndeGeaendertValidator {
    fun validate(event: EndeGeaendert, vertragAggregate: Vertrag?): EndeGeaendertResult {
        var validation = EventValidation(
            commandId = event.commandId,
            aggregateId = event.aggregateId,
            valid = false,
            validationType = ValidationType.UngueltigeEingabe,
            exception = "Unbekannter Fehler"
        )

        return if (vertragAggregate == null) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert nicht"
            )
            EndeGeaendertResult(null, null, validation)
        } else if (event.payload.ende.isBefore(vertragAggregate.beginn)) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Neues Ende liegt vor Vertrags-Beginn"
            )
            EndeGeaendertResult(null, null, validation)
        } else {
            validation = validation.copy(
                valid = true,
                validationType = ValidationType.Gueltig,
                exception = ""
            )
            val vertrag = vertragAggregate.copy(
                ende = event.payload.ende
            )
            EndeGeaendertResult(event, vertrag, validation)
        }
    }
}