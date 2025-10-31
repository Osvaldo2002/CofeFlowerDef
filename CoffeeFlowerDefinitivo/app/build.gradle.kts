plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jetbrainsKotlinCompose)
}

android {
    namespace = "com.example.aplicacion"
    // 🔥 CORRECCIÓN 1: Actualizado a 35 (Necesario por las librerías más nuevas)
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aplicacion"
        minSdk = 24
        // 🔥 CORRECCIÓN 2: Actualizado a 35
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
        // Mantenemos 1.5.11, que es compatible con Compose 1.6.8
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {

    // --- LIBRERÍAS BASE DE COMPOSE (CORREGIDO) ---
    // Usamos el BOM, pero sobreescribimos las versiones problemáticas
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // 🔥 CORRECCIÓN 3: Forzamos versiones estables (1.6.8) para UI/Foundation
    // y evitamos los conflictos de la versión 1.9.4.
    val composeVersion = "1.6.8"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")

    implementation("androidx.activity:activity-compose:1.9.0")
    // Forzamos Material3 compatible con 1.6.x (para evitar la versión alpha 1.9.4)
    implementation("androidx.compose.material3:material3:1.2.1")

    implementation("androidx.navigation:navigation-compose:2.7.7")


    // --- LIBRERÍAS DE VIEWMODEL (CORREGIDO) ---
    // 🔥 CORRECCIÓN 4: Forzamos versiones estables (2.7.0) para Lifecycle
    // para evitar el conflicto de la versión 2.9.4.
    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // 4. Para cargar imágenes de internet (Coil)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- LIBRERÍAS BASE DE KOTLIN (Se queda) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.vectordrawable.animated)
    implementation(libs.androidx.foundation)

    // --- LIBRERÍAS DE TEST (Se quedan) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}