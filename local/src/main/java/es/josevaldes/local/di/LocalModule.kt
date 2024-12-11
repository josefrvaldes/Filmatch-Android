package es.josevaldes.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.josevaldes.local.dao.LocalDatabase
import es.josevaldes.local.dao.VisitedMoviesDao
import es.josevaldes.local.datasources.MoviesLocalDataSource
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
    fun provideVisitedMoviesDao(database: LocalDatabase): VisitedMoviesDao {
        return database.visitedMoviesDao()
    }

    @Provides
    fun provideMoviesLocalDataSource(visitedMoviesDao: VisitedMoviesDao): MoviesLocalDataSource {
        return MoviesLocalDataSource(visitedMoviesDao)
    }
}