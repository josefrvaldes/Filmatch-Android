import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.android.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "es.josevaldes.filmatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.catdevs.filmatch"
        minSdk = 29
        targetSdk = 35
        versionCode = 2
        versionName = "0.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val properties = Properties()

        create("prod") {
            properties.load(file("keystores/prod.config").inputStream())
            storeFile = file("keystores/prod.jks")

            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("prod")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        warningsAsErrors = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core"))


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.google.fonts)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Hilt
    implementation(libs.hilt)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    ksp(libs.hilt.compiler)

    // paging
    implementation(libs.paging.compose)
    implementation(libs.paging.common)
    implementation(libs.paging)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.timber)

    // serialization + navigation
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation)

    implementation(libs.splashScreen)



    testImplementation(project(":data"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.mockk)
    testImplementation(libs.core.testing)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.test.monitor)
    androidTestImplementation(libs.mockk)
    testImplementation(libs.paging.common)
    testImplementation(libs.paging)
    testImplementation(libs.paging.testing.android)
    testImplementation(libs.roboelectric)
}