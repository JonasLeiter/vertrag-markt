package de.vkb.event

data class EventValidationResult(
    val eventId: String,
    val valid: Boolean,
    val validationType: ValidationType,
    val exception: String
)

enum class ValidationType {
    Gueltig,
    Ungueltig,
    UngueltigeEingabe
}
