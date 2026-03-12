package com.example.offlineaudioplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "button_mappings")
data class ButtonMappingEntity(
    @PrimaryKey val buttonId: Int,
    val label: String,
    val uri: String?
)
