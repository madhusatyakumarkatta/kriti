package com.krithi.di

import com.krithi.data.repository.MusicRepositoryImpl
import com.krithi.domain.repository.MusicRepository
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
}
