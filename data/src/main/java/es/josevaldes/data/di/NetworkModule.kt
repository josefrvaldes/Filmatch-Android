package es.josevaldes.data.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.adapters.ApiResultCallAdapterFactory
import es.josevaldes.data.deserializers.DetailsItemResponseDeserializer
import es.josevaldes.data.interceptors.AuthInterceptor
import es.josevaldes.data.responses.DetailsItemResponse
import es.josevaldes.data.services.AuthRemoteDataSource
import es.josevaldes.data.services.FirebaseTokenProvider
import es.josevaldes.data.services.GenreRemoteDataSource
import es.josevaldes.data.services.MoviesRemoteDataSource
import es.josevaldes.data.services.ProviderRemoteDataSource
import es.josevaldes.data.services.TmdbTokenProvider
import es.josevaldes.data.services.TokenProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

object JsonMapper {
    val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val module = SimpleModule().addDeserializer(
            DetailsItemResponse::class.java,
            DetailsItemResponseDeserializer()
        )
        registerModule(module)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TmdbRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FilmatchRetrofit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org"
        const val FILMATCH_BASE_URL = "https://josevaldes.ovh"
    }

    @Provides
    @Singleton
    @TmdbRetrofit
    fun provideTmdbTokenProvider(): TokenProvider {
        return TmdbTokenProvider()
    }

    @Provides
    @Reusable
    @FilmatchRetrofit
    fun provideFilmatchTokenProvider(firebaseAuth: FirebaseAuth): TokenProvider {
        return FirebaseTokenProvider(firebaseAuth)
    }

    @Provides
    @Singleton
    @TmdbRetrofit
    fun provideTmdbAuthInterceptor(@TmdbRetrofit tokenProvider: TokenProvider): AuthInterceptor {
        return AuthInterceptor(tokenProvider::getToken)
    }

    @Provides
    @Singleton
    @FilmatchRetrofit
    fun provideFilmatchAuthInterceptor(@FilmatchRetrofit tokenProvider: TokenProvider): AuthInterceptor {
        return AuthInterceptor(tokenProvider::getToken)
    }

    @Provides
    @Singleton
    @TmdbRetrofit
    fun provideTmdbHttpClient(@TmdbRetrofit authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addNetworkInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @FilmatchRetrofit
    fun provideFilmatchHttpClient(@FilmatchRetrofit authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addNetworkInterceptor(authInterceptor)
            .build()
    }


    @Provides
    @Singleton
    @TmdbRetrofit
    fun provideTmdbRetrofit(@TmdbRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(JacksonConverterFactory.create(JsonMapper.objectMapper))
            .build()
    }

    @Provides
    @Singleton
    @FilmatchRetrofit
    fun provideFilmatchRetrofit(@FilmatchRetrofit okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FILMATCH_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(JacksonConverterFactory.create(JsonMapper.objectMapper))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoviesRemoteDataSource(@TmdbRetrofit retrofit: Retrofit): MoviesRemoteDataSource {
        return retrofit.create(MoviesRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideGenresRemoteDataSource(@TmdbRetrofit retrofit: Retrofit): GenreRemoteDataSource {
        return retrofit.create(GenreRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideProvidersRemoteDataSource(@TmdbRetrofit retrofit: Retrofit): ProviderRemoteDataSource {
        return retrofit.create(ProviderRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(@FilmatchRetrofit retrofit: Retrofit): AuthRemoteDataSource {
        return retrofit.create(AuthRemoteDataSource::class.java)
    }
}