package de.vkb.event

import de.vkb.event.events.VertragErstellt
import de.vkb.event.validation.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.models.Vertrag

data class VertragErstelltResult(
    override val event: VertragErstellt?,
    override val aggregate: Vertrag?,
    override val feedback: EventValidation
) : GenericEventAggregatorResult<VertragErstellt, Vertrag, EventValidation>
