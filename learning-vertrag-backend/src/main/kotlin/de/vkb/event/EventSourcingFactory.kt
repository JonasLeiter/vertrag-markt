package de.vkb.event.events

import com.fasterxml.jackson.databind.ObjectMapper
import de.vkb.laser.es.helpers.JacksonSerdeFactoryBean
import de.vkb.laser.es.kafka.EventSourcingStreamFactory
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class EventSourcingFactory {
    @Singleton
    fun jacksonSerdeFactories(
        objectMapper: ObjectMapper
    ): JacksonSerdeFactoryBean {
        return JacksonSerdeFactoryBean(objectMapper)
    }

    @Singleton
    fun eventSourcingStreams(): EventSourcingStreamFactory {
        return EventSourcingStreamFactory(replayMode = false)
    }
}