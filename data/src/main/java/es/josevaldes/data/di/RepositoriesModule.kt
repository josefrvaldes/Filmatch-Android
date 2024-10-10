package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.services.MovieService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideMoviesPagingSource(movieService: MovieService): MoviesPagingSource {
        return MoviesPagingSource(movieService = movieService)
    }
}