package de.vkb.command.commands

import com.fasterxml.jackson.annotation.JsonFormat
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class AendereEnde(
    override val commandId: String,
    override val aggregateId: String,
    override val payload: AendereEndePayload,
) : Command

@Introspected
data class AendereEndePayload(
    @field:NotBlank
    val vertragId: String,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val ende: LocalDate
)
