package de.vkb.command.commands

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class AendereBeginn(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: AendereBeginnPayload,
) : Command

@Introspected
data class AendereBeginnPayload(
    @field:NotBlank
    val vertragId: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val beginn: LocalDate
)
