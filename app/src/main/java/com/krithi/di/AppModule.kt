package com.krithi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.krithi.ui.theme.ThemeController
import com.krithi.ui.theme.ThemeControllerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {
  @Singleton
  @Binds
  abstract fun bindThemeController(controller: ThemeControllerImpl): ThemeController
}