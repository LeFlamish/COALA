plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3") // Android Gradle 플러그인 버전
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10") // Kotlin Gradle 플러그인 버전
    }
}