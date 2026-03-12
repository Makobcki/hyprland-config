package com.example.offlineaudioplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.offlineaudioplayer.data.ButtonMappingEntity
import com.example.offlineaudioplayer.repo.AudioRepository
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AudioRepository) : ViewModel() {
    val mappings: LiveData<List<ButtonUiModel>> = repository.observeMappings().asLiveData().let { source ->
        androidx.lifecycle.Transformations.map(source) { existing ->
            val byId = existing.associateBy { it.buttonId }
            (1..10).map { idx ->
                val item = byId[idx]
                ButtonUiModel(idx, item?.label ?: "Button $idx", item?.uri)
            }
        }
    }

    fun save(items: List<ButtonUiModel>) {
        viewModelScope.launch {
            repository.saveMappings(items.map { ButtonMappingEntity(it.buttonId, it.label, it.uri) })
        }
    }

    class Factory(private val repository: AudioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
