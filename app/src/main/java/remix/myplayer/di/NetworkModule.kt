package com.krithi.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import com.krithi.request.kugou.KuGouClient
import com.krithi.request.netease.NetEaseClient
import com.krithi.request.network.GithubApi
import com.krithi.request.network.LastFMApi
import com.krithi.request.network.OkHttpHelper
import com.krithi.request.qq.QQClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@OptIn(ExperimentalSerializationApi::class)
object NetworkModule {

  private val json = Json { ignoreUnknownKeys = true }
  private val jsonContentType = "application/json".toMediaType()

  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    return OkHttpHelper.okHttpClient!!
  }

  @Provides
  @Singleton
  fun provideGithubApi(okHttpClient: OkHttpClient): GithubApi {
    return Retrofit.Builder()
      .baseUrl(GithubApi.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(json.asConverterFactory(jsonContentType))
      .build()
      .create(GithubApi::class.java)
  }

  @Provides
  @Singleton
  fun provideNetEaseEapiClient(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient
  ): NetEaseClient {
    return NetEaseClient(context, okHttpClient)
  }

  @Provides
  @Singleton
  fun provideKuGouClient(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient
  ): KuGouClient {
    return KuGouClient(okHttpClient)
  }

  @Provides
  @Singleton
  fun provideQQClient(
    @ApplicationContext context: Context,
    okHttpClient: OkHttpClient
  ): QQClient {
    return QQClient(context, okHttpClient)
  }

  @Provides
  @Singleton
  fun provideLastFMApi(okHttpClient: OkHttpClient): LastFMApi {
    return Retrofit.Builder()
      .baseUrl(LastFMApi.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(json.asConverterFactory(jsonContentType))
      .build()
      .create(LastFMApi::class.java)
  }
}
