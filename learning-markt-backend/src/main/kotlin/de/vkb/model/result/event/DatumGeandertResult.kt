package de.vkb.model.result.event

import de.vkb.laser.es.dto.GenericEventAggregatorResult
import de.vkb.model.aggregate.Markt
import de.vkb.model.event.DatumGeandert
import de.vkb.model.validation.EventValidation

data class DatumGeandertResult(
    override val feedback: EventValidation,
    override val aggregate: Markt?,
    override val event: DatumGeandert?
): GenericEventAggregatorResult<DatumGeandert, Markt, EventValidation>