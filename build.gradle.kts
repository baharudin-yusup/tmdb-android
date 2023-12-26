buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
    }
    repositories {
        google()
    }
}

plugins {
    id("com.android.application") version Versions.AGP apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN apply false
    id("com.google.dagger.hilt.android") version Versions.HILT apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
    id("com.android.library") version Versions.AGP apply false
    id("com.android.dynamic-feature") version Versions.AGP apply false
}
