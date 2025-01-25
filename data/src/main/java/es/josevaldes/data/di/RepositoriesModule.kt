package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.paging.MediaPagingSource
import es.josevaldes.data.repositories.AuthRepository
import es.josevaldes.data.services.AuthRemoteDataSource
import es.josevaldes.data.services.MediaRemoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Provides
    @Singleton
    fun provideMoviesPagingSource(mediaRemoteDataSource: MediaRemoteDataSource): MediaPagingSource {
        return MediaPagingSource(mediaRemoteDataSource = mediaRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authRemoteDataSource: AuthRemoteDataSource): AuthRepository {
        return AuthRepository(authRemoteDataSource)
    }
}