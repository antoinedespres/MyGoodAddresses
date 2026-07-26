package com.despreschen.mygoodaddresses.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

/** A postal address split into the fields the add form collects. */
data class ResolvedAddress(
    val addressLine: String,
    val postalCode: String,
    val city: String,
)

/**
 * Geocoding, off the main thread.
 *
 * Both directions used to run inline on the UI thread — `getFromLocation` while
 * filling the add form, and `getFromLocationName` in the map Activity's
 * `onCreate`. Both are blocking network calls and either can stall the frame or
 * raise an ANR.
 */
class AddressLookup(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val geocoder: Geocoder get() = Geocoder(context, Locale.getDefault())

    /** Coordinates to a postal address, for the "use my location" button. */
    @Suppress("DEPRECATION") // The callback API needs API 33; minSdk here is 29.
    suspend fun fromCoordinates(latitude: Double, longitude: Double): ResolvedAddress? =
        withContext(ioDispatcher) {
            firstAddress { geocoder.getFromLocation(latitude, longitude, 1) }?.let { address ->
                ResolvedAddress(
                    // Street number and street name, skipping either if absent
                    // rather than rendering "null rue de la Paix".
                    addressLine = listOfNotNull(address.subThoroughfare, address.thoroughfare)
                        .joinToString(" "),
                    postalCode = address.postalCode.orEmpty(),
                    city = address.locality ?: address.subAdminArea.orEmpty(),
                )
            }
        }

    /** A written address to coordinates, for placing the map marker. */
    @Suppress("DEPRECATION") // The callback API needs API 33; minSdk here is 29.
    suspend fun toCoordinates(address: String): LatLng? = withContext(ioDispatcher) {
        if (address.isBlank()) return@withContext null
        firstAddress { geocoder.getFromLocationName(address, 1) }
            ?.let { LatLng(it.latitude, it.longitude) }
    }

    private inline fun firstAddress(query: () -> List<Address>?): Address? =
        try {
            query()?.firstOrNull()
        } catch (_: IOException) {
            // No connectivity, or the backend is unavailable. The caller shows
            // a message rather than the app failing silently.
            null
        }
}
