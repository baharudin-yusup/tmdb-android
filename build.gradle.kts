buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:${Versions.KOTLIN}")
    }
    repositories {
        google()
    }
}

plugins {
    id("com.android.application") version Versions.AGP apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN apply false
    id("org.jetbrains.kotlin.plugin.compose") version Versions.KOTLIN apply false
    id("com.google.dagger.hilt.android") version Versions.HILT apply false
    id("com.google.devtools.ksp") version Versions.KSP apply false
    id("com.android.library") version Versions.AGP apply false
    id("com.android.dynamic-feature") version Versions.AGP apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
