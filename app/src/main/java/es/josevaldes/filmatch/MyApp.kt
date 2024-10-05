package es.josevaldes.filmatch

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

}

private class CrashReportingTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        val throwable = t ?: Exception(message)

        // Crashlytics
        val crashlytics = Firebase.crashlytics
        crashlytics.setCustomKeys {
            key(CRASHLYTICS_KEY_PRIORITY, priority)
            key(CRASHLYTICS_KEY_TAG, tag.orEmpty())
            key(CRASHLYTICS_KEY_MESSAGE, message)
        }

        // Firebase Crash Reporting
        crashlytics.log("$priority $tag $message")
        crashlytics.recordException(throwable)
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}