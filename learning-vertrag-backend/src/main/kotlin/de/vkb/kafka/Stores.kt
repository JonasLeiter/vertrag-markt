package de.vkb.kafka

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("stores")
class StoreConfig {
    lateinit var vertragStore: String
}
