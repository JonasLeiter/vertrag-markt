package de.vkb.kafka

import de.vkb.common.ValidationResult
import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic

@KafkaClient()
interface ValidationProducer {

    @Topic("\${topics.validation}")
    fun send(@KafkaKey key: String, value: ValidationResult)
}