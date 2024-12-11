package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.services.MovieRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideMoviesPagingSource(movieRemoteDataSource: MovieRemoteDataSource): MoviesPagingSource {
        return MoviesPagingSource(movieRemoteDataSource = movieRemoteDataSource)
    }
}