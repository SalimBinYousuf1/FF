package com.vibeplayer.di

import android.content.Context
import androidx.room.Room
import com.vibeplayer.data.local.PlaylistDao
import com.vibeplayer.data.local.SongDao
import com.vibeplayer.data.local.VibePlayerDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): VibePlayerDatabase {
        return Room.databaseBuilder(
            context,
            VibePlayerDatabase::class.java,
            "vibe_player.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSongDao(db: VibePlayerDatabase): SongDao = db.songDao()

    @Provides
    fun providePlaylistDao(db: VibePlayerDatabase): PlaylistDao = db.playlistDao()
}
