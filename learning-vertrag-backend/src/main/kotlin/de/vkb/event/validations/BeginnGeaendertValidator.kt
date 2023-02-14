package de.vkb.event.validations

import de.vkb.common.ValidationType
import de.vkb.event.results.BeginnGeaendertResult
import de.vkb.event.events.BeginnGeaendert
import de.vkb.laser.es.dto.impl.EventAggregatorResult
import de.vkb.models.Vertrag
import jakarta.inject.Singleton

@Singleton
class BeginnGeaendertValidator {
    fun validate(event: BeginnGeaendert, vertragAggregate: Vertrag?): EventAggregatorResult<BeginnGeaendert, Vertrag, EventValidation> {
        var validation = EventValidation(
            commandId = event.commandId,
            aggregateId = event.aggregateId,
            valid = false,
            validationType = ValidationType.UngueltigeEingabe,
            exception = "Unbekannter Fehler",
            hasErrors = true
        )

        return if (vertragAggregate == null) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Vertrag existiert nicht"
            )
            EventAggregatorResult(null, null, validation)
        } else if (event.payload.beginn.isAfter(vertragAggregate.ende)) {
            validation = validation.copy(
                validationType = ValidationType.Ungueltig,
                exception = "Neuer Beginn liegt nach Vertrags-Ende"
            )
            EventAggregatorResult(null, null, validation)
        } else {
            validation = validation.copy(
                validationType = ValidationType.Gueltig,
                valid = true,
                exception = "",
                hasErrors = false
            )
            val vertrag = vertragAggregate.copy(
                beginn = event.payload.beginn
            )
            EventAggregatorResult(event, vertrag, validation)
        }
    }
}