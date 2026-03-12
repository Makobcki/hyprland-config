package com.example.offlineaudioplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ButtonMappingDao {
    @Query("SELECT * FROM button_mappings ORDER BY buttonId ASC")
    fun observeAll(): Flow<List<ButtonMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mappings: List<ButtonMappingEntity>)
}
