package de.vkb.models

import java.time.LocalDate

data class Vertrag(
    var id: String,
    var bezeichnung: String,
    var beginn: LocalDate,
    var ende: LocalDate
)
