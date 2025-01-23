package es.josevaldes.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.josevaldes.local.dao.LocalDatabase
import es.josevaldes.local.dao.VisitedMediaDao
import es.josevaldes.local.datasources.MediaLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "local_db"
        ).build()
    }

    @Provides
    fun provideVisitedMediaDao(database: LocalDatabase): VisitedMediaDao {
        return database.visitedMediaDao()
    }

    @Provides
    fun provideMoviesLocalDataSource(visitedMediaDao: VisitedMediaDao): MediaLocalDataSource {
        return MediaLocalDataSource(visitedMediaDao)
    }
}