package de.vkb.model.command

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

data class AendereOrt(
    override val commandId: String,
    override val payload: AendereOrtPayload,
    override val aggregateIdentifier: String,
):Command

@Introspected
data class AendereOrtPayload(
    @NotBlank
    val id: String,
    @NotBlank
    val ort: String)