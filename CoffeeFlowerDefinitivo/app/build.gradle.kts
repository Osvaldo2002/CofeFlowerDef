plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jetbrainsKotlinCompose)
}

android {
    namespace = "com.example.aplicacion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aplicacion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {

    // --- LIBRERÍAS BASE DE COMPOSE ---
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    val composeVersion = "1.6.8"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")


    // --- LIBRERÍAS DE VIEWMODEL ---
    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // --- OTRAS LIBRERÍAS ---
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.vectordrawable.animated)

    // --- 👇 LIBRERÍAS AÑADIDAS PARA EL CARRUSEL (Accompanist Pager) 👇 ---
    val accompanist_version = "0.34.0"
    // Pager (Carrusel) Core
    implementation("com.google.accompanist:accompanist-pager:$accompanist_version")
    // Indicadores para el carrusel (los puntos de navegación)
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanist_version")
    // ----------------------------------------------------------------------

    // --- 👇 LÍNEA CORREGIDA 👇 ---

    implementation(libs.androidx.appcompat)

    // --- LIBRERÍAS DE TEST ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
