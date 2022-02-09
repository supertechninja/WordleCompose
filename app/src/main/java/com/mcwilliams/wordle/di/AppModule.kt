package com.mcwilliams.wordle.di

import android.content.Context
import com.mcwilliams.wordle.WordleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module()
class AppModule {

    @Provides
    @Singleton
    fun provideWordleRepository(
        @ApplicationContext context: Context,
    ): WordleRepository =
        WordleRepository(context,)
}