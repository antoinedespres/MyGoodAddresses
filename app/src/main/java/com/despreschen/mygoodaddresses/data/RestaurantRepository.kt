package com.despreschen.mygoodaddresses.data

import com.despreschen.mygoodaddresses.data.local.Restaurant
import com.despreschen.mygoodaddresses.data.local.RestaurantDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * The saved restaurants.
 *
 * Everything is local. The previous version opened a JDBC connection to a
 * PostgreSQL server from the phone, using the `postgres` superuser with the
 * password shipped inside the APK.
 */
class RestaurantRepository(
    private val dao: RestaurantDao,
    private val ioDispatcher: CoroutineDispatcher,
) {

    val restaurants: Flow<List<Restaurant>> = dao.observeAll()

    suspend fun find(id: Long): Restaurant? = withContext(ioDispatcher) { dao.findById(id) }

    suspend fun add(restaurant: Restaurant): Long =
        withContext(ioDispatcher) { dao.insert(restaurant) }

    suspend fun update(restaurant: Restaurant) = withContext(ioDispatcher) { dao.update(restaurant) }

    suspend fun delete(restaurant: Restaurant) = withContext(ioDispatcher) { dao.delete(restaurant) }
}
