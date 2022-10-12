package de.vkb.command.commands

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class ErstelleVertrag(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: ErstelleVertragPayload
) : Command {
}

@Introspected
data class ErstelleVertragPayload(
    @field:NotBlank
    val bezeichnung: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val beginn: LocalDate,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val ende: LocalDate
)
