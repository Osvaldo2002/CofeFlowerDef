plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jetbrainsKotlinCompose)
}

android {
    namespace = "com.example.aplicacion"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aplicacion"
        minSdk = 24
        targetSdk = 34
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

    // --- AQUÍ ESTABA EL ERROR ---
    // Ahora 'composeOptions' está correctamente descomentado
    buildFeatures {
        compose = true
    }
    composeOptions {
        // Esta versión del compilador es compatible con las librerías que usamos
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    // ----------------------------------------------------
}

dependencies {

    // --- LIBRERÍAS BASE DE COMPOSE (Las que faltaban) ---
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.0")

    // --- LIBRERÍAS QUE TÚ AÑADISTE (¡Están bien!) ---
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- LIBRERÍAS BASE DE KOTLIN (Se queda) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.vectordrawable.animated)

    // --- LIBRERÍAS DE TEST (Se quedan) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}