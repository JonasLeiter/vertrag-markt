package de.vkb.model.validation

data class CommandValidation(
    override val commandId: String,
    override val isValid: Boolean,
    override val aggregateIdentifier: String,
    override val message: String
): Validation