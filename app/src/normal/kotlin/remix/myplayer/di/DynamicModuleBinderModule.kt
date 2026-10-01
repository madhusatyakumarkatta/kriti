package com.krithi.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.krithi.misc.manager.DefaultDynamicModuleManager
import com.krithi.misc.manager.DynamicModuleManager

@Module
@InstallIn(SingletonComponent::class)
abstract class DynamicModuleBinderModule {

  @Binds
  abstract fun bindDynamicModuleManager(impl: DefaultDynamicModuleManager): DynamicModuleManager
}
