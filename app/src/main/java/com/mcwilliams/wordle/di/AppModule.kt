package com.mcwilliams.wordle.di

import android.content.Context
import com.mcwilliams.wordle.ui.WordleRepository
import com.mcwilliams.wordle.ui.stats.StatsRepository
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

    @Provides
    @Singleton
    fun provideStatsRepository(
        @ApplicationContext context: Context,
    ): StatsRepository =
        StatsRepository(context)
}