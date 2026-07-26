package com.despreschen.mygoodaddresses

import android.app.Application
import android.content.Context
import com.despreschen.mygoodaddresses.data.PhotoStorage
import com.despreschen.mygoodaddresses.data.RestaurantRepository
import com.despreschen.mygoodaddresses.data.local.AppDatabase
import com.despreschen.mygoodaddresses.location.AddressLookup
import com.despreschen.mygoodaddresses.location.DeviceLocation
import kotlinx.coroutines.Dispatchers

/**
 * Manual dependency container.
 *
 * The app is small enough that a hand-written container is clearer than a DI
 * framework, and it keeps another annotation processor out of the build.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val restaurantRepository = RestaurantRepository(
        dao = AppDatabase.get(appContext).restaurantDao(),
        ioDispatcher = Dispatchers.IO,
    )

    val addressLookup = AddressLookup(appContext, Dispatchers.IO)

    val deviceLocation = DeviceLocation(appContext)

    val photoStorage = PhotoStorage(appContext, Dispatchers.IO)
}

class MyGoodAddressesApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
