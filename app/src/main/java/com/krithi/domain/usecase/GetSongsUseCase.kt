package com.krithi.domain.usecase

import com.krithi.domain.model.Song
import com.krithi.domain.repository.MusicRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): List<Song> {
        return repository.getSongs()
    }
}
