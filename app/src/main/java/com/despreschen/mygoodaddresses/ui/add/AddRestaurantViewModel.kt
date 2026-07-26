package com.despreschen.mygoodaddresses.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.despreschen.mygoodaddresses.data.PhotoStorage
import com.despreschen.mygoodaddresses.data.RestaurantRepository
import com.despreschen.mygoodaddresses.data.local.Restaurant
import com.despreschen.mygoodaddresses.location.AddressLookup
import com.despreschen.mygoodaddresses.location.DeviceLocation
import com.despreschen.mygoodaddresses.ui.list.application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/** Why a "use my location" attempt did not fill the fields. */
enum class LocationProblem {
    PermissionDenied,
    NoFix,
    NoAddress,
}

data class AddRestaurantUiState(
    val name: String = "",
    val type: String = "",
    val addressLine: String = "",
    val postalCode: String = "",
    val city: String = "",
    val photoPath: String? = null,
    val isLocating: Boolean = false,
    val isSaving: Boolean = false,
    val locationProblem: LocationProblem? = null,
    val savedId: Long? = null,
) {
    /** A restaurant with no name is not worth storing. */
    val canSave: Boolean get() = name.isNotBlank() && !isSaving
}

class AddRestaurantViewModel(
    private val repository: RestaurantRepository,
    private val addressLookup: AddressLookup,
    private val deviceLocation: DeviceLocation,
    private val photoStorage: PhotoStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRestaurantUiState())
    val uiState: StateFlow<AddRestaurantUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onTypeChange(value: String) = _uiState.update { it.copy(type = value) }
    fun onAddressLineChange(value: String) = _uiState.update { it.copy(addressLine = value) }
    fun onPostalCodeChange(value: String) = _uiState.update { it.copy(postalCode = value) }
    fun onCityChange(value: String) = _uiState.update { it.copy(city = value) }

    fun onPhotoCaptured(path: String?) {
        // Replacing a photo leaves the previous file orphaned.
        val previous = _uiState.value.photoPath
        _uiState.update { it.copy(photoPath = path) }
        if (previous != null && previous != path) {
            viewModelScope.launch { photoStorage.delete(previous) }
        }
    }

    /** The camera was cancelled, so the empty target file is cleaned up. */
    fun onPhotoDiscarded(file: File) {
        viewModelScope.launch { photoStorage.discardIfEmpty(file) }
    }

    fun needsLocationPermission(): Boolean = !deviceLocation.hasPermission()

    fun onProblemShown() = _uiState.update { it.copy(locationProblem = null) }

    /**
     * Fills the address fields from where the phone is.
     *
     * Each way this can fail is reported: the old version simply did nothing
     * when the permission was missing or no fix was cached.
     */
    fun useCurrentLocation() {
        if (_uiState.value.isLocating) return

        if (!deviceLocation.hasPermission()) {
            _uiState.update { it.copy(locationProblem = LocationProblem.PermissionDenied) }
            return
        }

        _uiState.update { it.copy(isLocating = true, locationProblem = null) }
        viewModelScope.launch {
            val position = deviceLocation.current()
            if (position == null) {
                _uiState.update { it.copy(isLocating = false, locationProblem = LocationProblem.NoFix) }
                return@launch
            }

            val address = addressLookup.fromCoordinates(position.first, position.second)
            _uiState.update { state ->
                if (address == null) {
                    state.copy(isLocating = false, locationProblem = LocationProblem.NoAddress)
                } else {
                    state.copy(
                        isLocating = false,
                        addressLine = address.addressLine,
                        postalCode = address.postalCode,
                        city = address.city,
                    )
                }
            }
        }
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val id = repository.add(
                Restaurant(
                    name = state.name.trim(),
                    type = state.type.trim(),
                    addressLine = state.addressLine.trim(),
                    postalCode = state.postalCode.trim(),
                    city = state.city.trim(),
                    photoPath = state.photoPath,
                ),
            )
            _uiState.update { it.copy(isSaving = false, savedId = id) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = application().container
                AddRestaurantViewModel(
                    repository = container.restaurantRepository,
                    addressLookup = container.addressLookup,
                    deviceLocation = container.deviceLocation,
                    photoStorage = container.photoStorage,
                )
            }
        }
    }
}
