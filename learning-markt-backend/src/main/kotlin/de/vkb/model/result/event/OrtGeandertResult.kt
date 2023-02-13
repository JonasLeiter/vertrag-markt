package de.vkb.model.result.event

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.OrtGeandert
import de.vkb.model.validation.EventValidation

data class OrtGeandertResult(
    override val feedback: EventValidation,
    override val aggregate: Markt?,
    override val event: OrtGeandert?
): GenericEventAggregatorResult<OrtGeandert, Markt, EventValidation>