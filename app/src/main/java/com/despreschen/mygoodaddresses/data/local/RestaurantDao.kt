package com.despreschen.mygoodaddresses.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {

    /**
     * Emits the whole list again whenever it changes, so a deletion or an
     * insert reaches the screen without anything having to re-query. The old
     * version refetched everything in `onResume`.
     */
    @Query("SELECT * FROM restaurants ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun findById(id: Long): Restaurant?

    @Insert
    suspend fun insert(restaurant: Restaurant): Long

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)

    @Query("DELETE FROM restaurants WHERE id = :id")
    suspend fun deleteById(id: Long)
}
