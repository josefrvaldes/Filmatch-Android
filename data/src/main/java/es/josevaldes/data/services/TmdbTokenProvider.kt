package es.josevaldes.data.services

import es.josevaldes.data.BuildConfig

class TmdbTokenProvider : TokenProvider {
    override suspend fun getToken(): String = BuildConfig.API_TOKEN
}