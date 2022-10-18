package de.vkb.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import javax.validation.constraints.NotBlank

@ConfigurationProperties("topics")
interface TopicConfig {
    @get:Bindable(defaultValue = "markt-command")
    @get:NotBlank
    val command: String

    @get:Bindable(defaultValue = "markt-internal-event")
    @get:NotBlank
    val internalEvent: String

    @get:Bindable(defaultValue = "markt-external-event")
    @get:NotBlank
    val externalEvent: String

    @get:Bindable(defaultValue = "markt-validation")
    @get:NotBlank
    val validation: String

    @get:Bindable(defaultValue = "markt-state")
    @get:NotBlank
    val state: String

    @get:Bindable(defaultValue = "vertrag-state")
    @get:NotBlank
    val vertragState: String
}