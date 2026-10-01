package com.krithi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.krithi.misc.manager.DynamicModuleManager
import com.krithi.misc.manager.GoogleDynamicModuleManager

@Module
@InstallIn(SingletonComponent::class)
abstract class DynamicModuleBinderModule {

  @Binds
  abstract fun bindDynamicModuleManager(impl: GoogleDynamicModuleManager): DynamicModuleManager
}
