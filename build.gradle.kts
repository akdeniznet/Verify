buildscript {
    repositories {
        google()
        mavenCentral()
        // JitPack repository'sini kaldırıyoruz
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        // CloudStream gradle plugin'ini kaldırıyoruz
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // Sadece gerekli repository'leri tutuyoruz
        maven { url "https://maven.lagradost.cloud/releases" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}