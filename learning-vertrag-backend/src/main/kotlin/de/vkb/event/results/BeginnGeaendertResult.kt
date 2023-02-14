package de.vkb.event.results

import de.vkb.event.events.BeginnGeaendert
import de.vkb.event.validations.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.models.Vertrag

data class BeginnGeaendertResult(
    override val event: BeginnGeaendert?,
    override val aggregate: Vertrag?,
    override val feedback: EventValidation
) : GenericEventAggregatorResult<BeginnGeaendert, Vertrag, EventValidation>
