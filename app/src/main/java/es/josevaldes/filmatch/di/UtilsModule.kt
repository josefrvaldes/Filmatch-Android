package es.josevaldes.filmatch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.josevaldes.filmatch.utils.DeviceLocaleProvider
import es.josevaldes.filmatch.utils.DeviceLocaleProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideDeviceLocaleProvider(): DeviceLocaleProvider {
        return DeviceLocaleProviderImpl()
    }
}