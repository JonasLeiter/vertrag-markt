package de.vkb.command.commands

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class AendereBeginn(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: AendereBeginnPayload,
) : Command

data class AendereBeginnPayload(
    @field:NotBlank
    val vertragId: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val beginn: LocalDate
)
