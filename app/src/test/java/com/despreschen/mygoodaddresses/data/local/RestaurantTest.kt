package com.despreschen.mygoodaddresses.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The full address is what gets handed to the geocoder, so a stray comma or a
 * doubled space is the difference between a marker on the map and a screen
 * saying the address could not be found.
 */
class RestaurantTest {

    @Test
    fun `full address joins the parts a geocoder expects`() {
        val restaurant = restaurant(
            addressLine = "12 rue de la Paix",
            postalCode = "75002",
            city = "Paris",
        )

        assertEquals("12 rue de la Paix, 75002 Paris", restaurant.fullAddress)
    }

    @Test
    fun `a missing street does not leave a leading separator`() {
        val restaurant = restaurant(addressLine = "", postalCode = "75002", city = "Paris")

        assertEquals("75002 Paris", restaurant.fullAddress)
    }

    @Test
    fun `an address with nothing but a street keeps just the street`() {
        val restaurant = restaurant(addressLine = "12 rue de la Paix", postalCode = "", city = "")

        // The postcode/city half collapses to a blank and is dropped, rather
        // than trailing a lone comma.
        assertEquals("12 rue de la Paix", restaurant.fullAddress)
    }

    @Test
    fun `an entirely empty address is blank rather than punctuation`() {
        assertEquals("", restaurant(addressLine = "", postalCode = "", city = "").fullAddress)
    }

    private fun restaurant(
        addressLine: String,
        postalCode: String,
        city: String,
    ) = Restaurant(
        id = 1,
        name = "Chez Antoine",
        type = "French",
        addressLine = addressLine,
        postalCode = postalCode,
        city = city,
    )
}
