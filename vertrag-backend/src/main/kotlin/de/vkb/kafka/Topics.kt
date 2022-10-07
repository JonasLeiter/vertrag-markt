package de.vkb.kafka

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import javax.validation.constraints.NotBlank

@ConfigurationProperties("topics")
interface TopicConfig {
    @get:Bindable(defaultValue = "learning-vertrag-command")
    @get:NotBlank
    val command: String

    @get:Bindable(defaultValue = "learning-vertrag-internal-event")
    @get:NotBlank
    val internalEvent: String

    @get:Bindable(defaultValue = "learning-vertrag-external-event")
    @get:NotBlank
    val externalEvent: String

    @get:Bindable(defaultValue = "learning-vertrag-state")
    @get:NotBlank
    val state: String
}