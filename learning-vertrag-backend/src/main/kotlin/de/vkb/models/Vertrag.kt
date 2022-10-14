package de.vkb.models

import java.time.LocalDate

data class Vertrag(
    val id: String,
    val bezeichnung: String,
    val beginn: LocalDate,
    val ende: LocalDate
)
