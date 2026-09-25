package com.krithi.domain.repository

import kotlinx.coroutines.flow.Flow

interface CoverRepository {
    fun getCustomCoverUri(songId: Long): String?
    fun observeCustomCoverUri(songId: Long): Flow<String?>
    fun setCustomCoverUri(songId: Long, uri: String?)
}
