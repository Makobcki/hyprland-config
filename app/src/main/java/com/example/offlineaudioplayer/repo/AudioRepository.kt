package com.example.offlineaudioplayer.repo

import com.example.offlineaudioplayer.data.ButtonMappingDao
import com.example.offlineaudioplayer.data.ButtonMappingEntity
import kotlinx.coroutines.flow.Flow

class AudioRepository(private val dao: ButtonMappingDao) {
    fun observeMappings(): Flow<List<ButtonMappingEntity>> = dao.observeAll()

    suspend fun saveMappings(mappings: List<ButtonMappingEntity>) {
        dao.insertAll(mappings)
    }
}
