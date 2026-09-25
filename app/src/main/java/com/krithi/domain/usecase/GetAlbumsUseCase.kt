package com.krithi.domain.usecase

import com.krithi.domain.model.Album
import com.krithi.domain.repository.MusicRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): List<Album> {
        return repository.getAlbums()
    }
}
