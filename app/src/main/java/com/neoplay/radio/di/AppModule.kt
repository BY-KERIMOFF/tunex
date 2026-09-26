package com.neoplay.radio.di

import android.content.Context
import com.neoplay.radio.data.preferences.SettingsDataStore
import com.neoplay.radio.data.repository.RadioRepository
import com.neoplay.radio.player.RadioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore {
        return SettingsDataStore(context)
    }

    @Provides
    @Singleton
    fun provideRadioRepository(
        settingsDataStore: SettingsDataStore
    ): RadioRepository {
        return RadioRepository(settingsDataStore)
    }

    @Provides
    @Singleton
    fun provideRadioPlayer(
        @ApplicationContext context: Context
    ): RadioPlayer {
        return RadioPlayer(context)
    }
}
