package de.vkb.model.validation

import de.vkb.laser.es.model.Feedback

data class CommandValidation(
    override val commandId: String,
    override val aggregateIdentifier: String,
    override val message: String,
    override val hasErrors: Boolean
): Feedback, Validation