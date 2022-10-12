package de.vkb.store

import de.vkb.config.StoreConfig
import de.vkb.model.aggregate.Markt
import io.micronaut.configuration.kafka.streams.InteractiveQueryService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.kafka.streams.state.QueryableStoreTypes

@Singleton
class StateStore(
    private val storeConfig: StoreConfig
) {

    @Inject
    lateinit var interactiveQueryService: InteractiveQueryService

    fun getMarkt(id: String): Markt? =
        interactiveQueryService
            .getQueryableStore(
                storeConfig.stateStore,
                QueryableStoreTypes.keyValueStore<String, Markt>())
            .map { it[id] }.orElse(null)
}