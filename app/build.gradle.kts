plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.robustfrontend"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.robustfrontend"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "WEB_CLIENT_ID", "\"${providers.gradleProperty("WEB_CLIENT_ID").getOrElse("\"")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    lint {
        abortOnError = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx) // <-- DEPENDENCIA CORREGIDA
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // ... otras dependencias (core, appcompat, etc.)

    // Retrofit y GSON Converter
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    // ¡IMPORTANTE! La nueva librería para logging
    implementation(libs.okhttp.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle, ViewModel y LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coil para cargar imágenes
    implementation(libs.coil)

    // --- DEPENDENCIAS DE FIREBASE AÑADIDAS ---

    // Importa el Bill of Materials (BoM) de Firebase.
    // Esto asegura que las versiones de las librerías de Firebase son compatibles entre sí.
    implementation(platform(libs.firebase.bom))

    // Dependencia para Firebase Authentication
    implementation(libs.firebase.auth)

    // Dependencia para el servicio de autenticación de Google Play
    implementation(libs.play.services.auth)

    implementation(libs.mpandroidchart)
}
