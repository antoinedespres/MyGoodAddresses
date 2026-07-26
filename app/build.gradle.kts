import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

/**
 * Reads the Google Maps key from `local.properties`, which is git-ignored.
 *
 * It was previously committed in `res/values/google_maps_api.xml`. Falls back to
 * the `MAPS_API_KEY` environment variable for CI, and to an empty string so a
 * fresh clone still builds — the map is simply blank without one.
 */
val mapsApiKey: String = run {
    val localProperties = rootProject.file("local.properties")
    val fromFile = if (localProperties.exists()) {
        Properties().apply { localProperties.inputStream().use(::load) }
            .getProperty("MAPS_API_KEY")
    } else {
        null
    }
    val key = fromFile ?: System.getenv("MAPS_API_KEY") ?: ""
    if (key.isBlank()) {
        logger.warn(
            "MyGoodAddresses: no MAPS_API_KEY found. Add it to local.properties " +
                "(see local.properties.example); the map will not render without one.",
        )
    }
    key
}

android {
    namespace = "com.despreschen.mygoodaddresses"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.despreschen.mygoodaddresses"
        minSdk = 29
        targetSdk = 36
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["mapsApiKey"] = mapsApiKey
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

ksp {
    // Room writes its schema out here, so a migration shows up in the diff
    // rather than only existing at runtime.
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
