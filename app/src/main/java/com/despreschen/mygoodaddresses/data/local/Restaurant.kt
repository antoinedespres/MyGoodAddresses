package com.despreschen.mygoodaddresses.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A saved restaurant.
 *
 * The Room entity doubles as the model the UI renders. For an app this size a
 * separate domain type would be mapping for its own sake; if persistence and
 * presentation ever diverge, that is the point to split them.
 */
@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val addressLine: String,
    val postalCode: String,
    val city: String,
    /**
     * Absolute path to a photo in the app's own storage, or null.
     *
     * The previous version captured a photo and then dropped it on save; it is
     * kept now, which is what the UI always implied.
     */
    val photoPath: String? = null,
) {
    /** "12 rue de la Paix, 75002 Paris" — what a geocoder can resolve. */
    val fullAddress: String
        get() = listOf(addressLine, "$postalCode $city")
            .filter { it.isNotBlank() }
            .joinToString(", ")
}
