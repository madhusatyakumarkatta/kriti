package com.krithi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.krithi.repo.AlbumRepoImpl
import com.krithi.repo.AlbumRepository
import com.krithi.repo.ArtistRepoImpl
import com.krithi.repo.ArtistRepository
import com.krithi.repo.FolderRepoImpl
import com.krithi.repo.FolderRepository
import com.krithi.repo.GenreRepoImpl
import com.krithi.repo.GenreRepository
import com.krithi.repo.HistoryRepoImpl
import com.krithi.repo.HistoryRepository
import com.krithi.repo.PlayListRepoImpl
import com.krithi.repo.PlayListRepository
import com.krithi.repo.PlayQueueRepoImpl
import com.krithi.repo.PlayQueueRepository
import com.krithi.repo.SmbRepoImpl
import com.krithi.repo.SmbRepository
import com.krithi.repo.SongRepoImpl
import com.krithi.repo.SongRepository
import com.krithi.repo.WebDavRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
  @Singleton
  @Binds
  abstract fun bindSongRepo(repo: SongRepoImpl): SongRepository

  @Singleton
  @Binds
  abstract fun bindAlbumRepo(repo: AlbumRepoImpl): AlbumRepository

  @Singleton
  @Binds
  abstract fun bindArtistRepo(repo: ArtistRepoImpl): ArtistRepository

  @Singleton
  @Binds
  abstract fun bindGenreRepo(repo: GenreRepoImpl): GenreRepository

  @Singleton
  @Binds
  abstract fun bindPlayListRepo(repo: PlayListRepoImpl): PlayListRepository

  @Singleton
  @Binds
  abstract fun bindFolderRepo(repo: FolderRepoImpl): FolderRepository

  @Singleton
  @Binds
  abstract fun bindPlayQueueRepo(repo: PlayQueueRepoImpl): PlayQueueRepository

  @Singleton
  @Binds
  abstract fun bindHistoryRepo(repo: HistoryRepoImpl): HistoryRepository

  @Singleton
  @Binds
  abstract fun bindWebDavRepo(repo: com.krithi.repo.WebDavRepoImpl): WebDavRepository

  @Singleton
  @Binds
  abstract fun bindSmbRepo(repo: SmbRepoImpl): SmbRepository
}
