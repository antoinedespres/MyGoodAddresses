package com.despreschen.mygoodaddresses.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.despreschen.mygoodaddresses.MyGoodAddressesApplication
import com.despreschen.mygoodaddresses.data.PhotoStorage
import com.despreschen.mygoodaddresses.data.RestaurantRepository
import com.despreschen.mygoodaddresses.data.local.Restaurant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RestaurantListViewModel(
    private val repository: RestaurantRepository,
    private val photoStorage: PhotoStorage,
) : ViewModel() {

    /** Backed by the database, so an insert or delete reaches the list on its own. */
    val restaurants: StateFlow<List<Restaurant>> = repository.restaurants.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = emptyList(),
    )

    fun delete(restaurant: Restaurant) {
        viewModelScope.launch {
            repository.delete(restaurant)
            // The row is gone, so its photo would otherwise sit in app storage
            // with nothing pointing at it.
            photoStorage.delete(restaurant.photoPath)
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = application().container
                RestaurantListViewModel(container.restaurantRepository, container.photoStorage)
            }
        }
    }
}

/** Shared helper for the `viewModelFactory` initializers across the ui package. */
internal fun CreationExtras.application(): MyGoodAddressesApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyGoodAddressesApplication
