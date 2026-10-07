package com.krithi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis()
)
