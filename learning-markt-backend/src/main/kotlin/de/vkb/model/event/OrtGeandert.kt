package de.vkb.model.event

data class OrtGeandert(
    override val commandId: String,
    override val payload: OrtGeandertPayload,
    override val aggregateIdentifier: String,
):Event

data class OrtGeandertPayload(
    val id: String,
    val ort: String,
)