plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://recloudstream.github.io/maven") // 🔥 CloudStream repository
}

android {
    namespace = "com.keyiflerolsun"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
        targetSdk = 35
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    // CloudStream ana kütüphane
    implementation("com.lagradost:cloudstream3:4.5.6")

    // Diğer bağımlılıklar
    implementation("com.github.Blatzar:NiceHttp:0.4.13")
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("com.github.vidstige:jadb:v1.2.1")
}
