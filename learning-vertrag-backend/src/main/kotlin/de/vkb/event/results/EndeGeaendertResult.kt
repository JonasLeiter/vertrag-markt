package de.vkb.event.results

import de.vkb.event.events.EndeGeaendert
import de.vkb.event.validations.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.models.Vertrag

data class EndeGeaendertResult(
    override val event: EndeGeaendert?,
    override val aggregate: Vertrag?,
    override val feedback: EventValidation
) : GenericEventAggregatorResult<EndeGeaendert, Vertrag, EventValidation>
