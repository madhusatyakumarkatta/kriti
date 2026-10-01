package com.krithi.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.krithi.lyric.provider.DefProvider
import com.krithi.lyric.provider.EmbeddedProvider
import com.krithi.lyric.provider.ILyricsProvider
import com.krithi.lyric.provider.IgnoredProvider
import com.krithi.lyric.provider.LocalFileProvider
import com.krithi.lyric.provider.network.KuGouProvider
import com.krithi.lyric.provider.network.NetEaseProvider
import com.krithi.lyric.provider.network.QQProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LyricProviderModule {

  @Provides
  @Singleton
  @IntoSet
  fun provideEmbedded(provider: EmbeddedProvider): ILyricsProvider = provider

  @Provides
  @Singleton
  @IntoSet
  fun provideIgnored(provider: IgnoredProvider): ILyricsProvider = provider

  @Provides
  @Singleton
  @IntoSet
  fun provideKuGou(kuGouProvider: KuGouProvider): ILyricsProvider = kuGouProvider

  @Provides
  @Singleton
  @IntoSet
  fun provideQQ(provider: QQProvider): ILyricsProvider = provider

  @Provides
  @Singleton
  @IntoSet
  fun provideNetEase(provider: NetEaseProvider): ILyricsProvider = provider

  @Provides
  @Singleton
  @IntoSet
  fun provideLocal(provider: LocalFileProvider): ILyricsProvider = provider

  @Provides
  @Singleton
  @IntoSet
  fun provideDef(provider: DefProvider): ILyricsProvider = provider
}