package es.josevaldes.data.di

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.data.adapters.ApiResultCallAdapterFactory
import es.josevaldes.data.deserializers.DetailsItemResponseDeserializer
import es.josevaldes.data.network.HttpClient
import es.josevaldes.data.responses.DetailsItemResponse
import es.josevaldes.data.services.GenreRemoteDataSource
import es.josevaldes.data.services.MoviesRemoteDataSource
import es.josevaldes.data.services.ProviderRemoteDataSource
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
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


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org"
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
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .addConverterFactory(JacksonConverterFactory.create(JsonMapper.objectMapper))
            .build()
    }

    @Provides
    @Singleton
    fun provideMoviesService(retrofit: Retrofit): MoviesRemoteDataSource {
        return retrofit.create(MoviesRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideGenresService(retrofit: Retrofit): GenreRemoteDataSource {
        return retrofit.create(GenreRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideProvidersService(retrofit: Retrofit): ProviderRemoteDataSource {
        return retrofit.create(ProviderRemoteDataSource::class.java)
    }
}