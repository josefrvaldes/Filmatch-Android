package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.services.AuthService
import es.josevaldes.data.services.FirebaseAuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return FirebaseAuthService()
    }
}