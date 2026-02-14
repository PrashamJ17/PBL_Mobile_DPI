package com.example.dpi.di

import android.content.Context
import androidx.room.Room
import com.example.dpi.database.DpiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDpiDatabase(
        @ApplicationContext context: Context
    ): DpiDatabase {
        return Room.databaseBuilder(
            context,
            DpiDatabase::class.java,
            "dpi_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun providePacketDao(database: DpiDatabase) = database.packetDao()
    
    @Provides
    fun provideFlowDao(database: DpiDatabase) = database.flowDao()
    
    @Provides
    fun provideThreatDao(database: DpiDatabase) = database.threatDao()
}

/**
 * Hilt module for app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context
}
