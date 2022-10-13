package de.vkb.event

import de.vkb.common.ValidationResult
import de.vkb.common.ValidationType

data class EventValidationResult(
    override val commandId: String,
    override val aggregateId: String,
    override val valid: Boolean,
    override val validationType: ValidationType,
    override val exception: String,
) : ValidationResult