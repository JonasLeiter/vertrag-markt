package de.vkb.event

import de.vkb.event.events.BeginnGeaendert
import de.vkb.event.events.Event
import de.vkb.event.events.VertragErstellt
import de.vkb.event.validation.EventValidation
import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.models.Vertrag

data class EndeGeaendertResult(
    override val event: BeginnGeaendert?,
    override val aggregate: Vertrag?,
    override val feedback: EventValidation
) : GenericEventAggregatorResult<BeginnGeaendert, Vertrag, EventValidation>
