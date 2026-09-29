package com.krithi.di

import com.krithi.data.repository.CoverRepositoryImpl
import com.krithi.data.repository.MusicRepositoryImpl
import com.krithi.data.repository.PlaylistRepositoryImpl
import com.krithi.domain.repository.CoverRepository
import com.krithi.domain.repository.MusicRepository
import com.krithi.domain.repository.PlaylistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository

    @Binds
    @Singleton
    abstract fun bindCoverRepository(
        coverRepositoryImpl: CoverRepositoryImpl
    ): CoverRepository
    
    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        playlistRepositoryImpl: PlaylistRepositoryImpl
    ): PlaylistRepository
}
