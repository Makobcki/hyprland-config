package com.example.offlineaudioplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.offlineaudioplayer.data.ButtonMappingEntity
import com.example.offlineaudioplayer.repo.AudioRepository

class MainViewModel(private val repository: AudioRepository) : ViewModel() {
    val mappings: LiveData<List<ButtonUiModel>> = repository.observeMappings().asLiveData().map { existing ->
        val byId = existing.associateBy { it.buttonId }
        (1..10).map { idx ->
            val item = byId[idx]
            ButtonUiModel(idx, item?.label ?: "Button $idx", item?.uri)
        }
    }

    fun toEntityList(items: List<ButtonUiModel>): List<ButtonMappingEntity> =
        items.map { ButtonMappingEntity(it.buttonId, it.label, it.uri) }

    class Factory(private val repository: AudioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
