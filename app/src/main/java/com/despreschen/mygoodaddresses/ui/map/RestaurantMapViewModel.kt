package com.despreschen.mygoodaddresses.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.despreschen.mygoodaddresses.data.RestaurantRepository
import com.despreschen.mygoodaddresses.data.local.Restaurant
import com.despreschen.mygoodaddresses.location.AddressLookup
import com.despreschen.mygoodaddresses.ui.list.application
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RestaurantMapUiState(
    val isLoading: Boolean = true,
    val restaurant: Restaurant? = null,
    val position: LatLng? = null,
    /** The address exists but no geocoder could place it. */
    val notFound: Boolean = false,
)

class RestaurantMapViewModel(
    private val repository: RestaurantRepository,
    private val addressLookup: AddressLookup,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantMapUiState())
    val uiState: StateFlow<RestaurantMapUiState> = _uiState.asStateFlow()

    /**
     * Looks the restaurant up and geocodes its address.
     *
     * The old screen passed the name and address through Intent extras and
     * geocoded them on the main thread in `onCreate`, leaving the map with a
     * null position if that failed.
     */
    fun load(restaurantId: Long) {
        viewModelScope.launch {
            val restaurant = repository.find(restaurantId)
            if (restaurant == null) {
                _uiState.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }

            val position = addressLookup.toCoordinates(restaurant.fullAddress)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    restaurant = restaurant,
                    position = position,
                    notFound = position == null,
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = application().container
                RestaurantMapViewModel(container.restaurantRepository, container.addressLookup)
            }
        }
    }
}
