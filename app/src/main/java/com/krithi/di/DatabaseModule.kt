package com.krithi.di

import android.content.Context
import androidx.room.Room
import com.krithi.data.local.dao.FavoriteDao
import com.krithi.data.local.database.KrithiDatabase
import com.krithi.data.local.dao.PlaylistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KrithiDatabase {
        return Room.databaseBuilder(
            context,
            KrithiDatabase::class.java,
            "krithi_database"
        ).build()
    }

    @Provides
    fun providePlaylistDao(database: KrithiDatabase): PlaylistDao {
        return database.playlistDao
    }

    @Provides
    fun provideFavoriteDao(database: KrithiDatabase): FavoriteDao {
        return database.favoriteDao
    }
}
