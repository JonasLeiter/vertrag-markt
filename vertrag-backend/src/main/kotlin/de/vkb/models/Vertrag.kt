package de.vkb.models

import java.util.*

data class Vertrag(
    var id: String,
    var bezeichnung: String,
    var beginn: Date,
    var ende: Date
)
