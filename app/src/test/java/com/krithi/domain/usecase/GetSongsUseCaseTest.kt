package com.krithi.domain.usecase

import android.net.Uri
import com.krithi.domain.model.Album
import com.krithi.domain.model.Song
import com.krithi.domain.repository.MusicRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class GetSongsUseCaseTest {

    @Test
    fun `invoke should return list of songs from repository`() = runBlocking {
        // Arrange
        val mockUri = Mockito.mock(Uri::class.java)
        val mockSongs = listOf(
            Song(1, 1, mockUri, "Title1", "Artist1", "Album1", 1, 1, 1000, 1, 1, "", 2022, 123, 100, "", ""),
            Song(2, 2, mockUri, "Title2", "Artist2", "Album2", 2, 2, 2000, 2, 1, "", 2023, 124, 200, "", "")
        )
        val mockRepository = object : MusicRepository {
            override suspend fun getSongs(): List<Song> = mockSongs
            override suspend fun getAlbums(): List<Album> = emptyList()
        }
        val useCase = GetSongsUseCase(mockRepository)

        // Act
        val result = useCase()

        // Assert
        assertEquals(2, result.size)
        assertEquals("Title1", result[0].title)
        assertEquals("Title2", result[1].title)
    }
}
