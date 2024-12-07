package es.josevaldes.filmatch.utils

import es.josevaldes.core.utils.getDeviceCountry as getCountry
import es.josevaldes.core.utils.getDeviceLocale as getLocale

// the reason why this class exist instead of using directly the utils functions,
// is to be able to inject it in the viewModels that should be agnostic of the platform
// and to be able to mock it in the tests
interface DeviceLocaleProvider {
    fun getDeviceLocale(): String
    fun getDeviceCountry(): String
}

class DeviceLocaleProviderImpl : DeviceLocaleProvider {
    override fun getDeviceLocale(): String {
        return getLocale()
    }

    override fun getDeviceCountry(): String {
        return getCountry()
    }
}
