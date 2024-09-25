package es.josevaldes.data.network

import es.josevaldes.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class HttpClient {
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addNetworkInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.API_TOKEN}")
                    .build()
                chain.proceed(request)
            })
            .build()
    }
}