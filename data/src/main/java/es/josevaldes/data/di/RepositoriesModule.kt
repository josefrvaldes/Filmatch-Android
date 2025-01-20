package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.paging.MoviesPagingSource
import es.josevaldes.data.repositories.AuthRepository
import es.josevaldes.data.services.AuthRemoteDataSource
import es.josevaldes.data.services.MoviesRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideMoviesPagingSource(moviesRemoteDataSource: MoviesRemoteDataSource): MoviesPagingSource {
        return MoviesPagingSource(moviesRemoteDataSource = moviesRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authRemoteDataSource: AuthRemoteDataSource): AuthRepository {
        return AuthRepository(authRemoteDataSource)
    }
}