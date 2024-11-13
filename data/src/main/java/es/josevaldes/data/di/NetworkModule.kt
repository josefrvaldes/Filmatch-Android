package es.josevaldes.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.core.utils.serialization.JsonMapper
import es.josevaldes.data.adapters.ApiResultCallAdapterFactory
import es.josevaldes.data.network.HttpClient
import es.josevaldes.data.services.GenreService
import es.josevaldes.data.services.MovieService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org"
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return HttpClient().getOkHttpClient()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(JacksonConverterFactory.create(JsonMapper.objectMapper))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoviesService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    @Provides
    @Singleton
    fun provideGenresService(retrofit: Retrofit): GenreService {
        return retrofit.create(GenreService::class.java)
    }
}