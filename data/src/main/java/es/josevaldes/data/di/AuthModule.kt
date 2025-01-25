package es.josevaldes.data.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.repositories.AuthRepository
import es.josevaldes.data.services.AuthService
import es.josevaldes.data.services.FirebaseAuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        firebaseAuth: FirebaseAuth,
        authRepository: AuthRepository
    ): AuthService {
        return FirebaseAuthService(firebaseAuth, authRepository)
    }
}