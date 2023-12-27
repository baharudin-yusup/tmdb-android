@file:Suppress("UnstableApiUsage")

import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "dev.baharudin.themoviedb"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.baharudin.themoviedb"
        minSdk = 31
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (System.getenv("GH_ACTIONS_FLAG").toBoolean()) {
                storeFile = file(System.getenv("GH_ACTIONS_SIGNING_STORE_FILE_PATH"))
                storePassword = System.getenv("GH_ACTIONS_SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("GH_ACTIONS_SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("GH_ACTIONS_SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    dynamicFeatures += setOf(":favorite")

    applicationVariants.all {
        val variant = this
        val variantName = variant.name
        val variantVersionName = variant.versionName ?: "Not found"

        tasks.register(
            "printVersionName${
                variantName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            }"
        ) {
            group = "Custom tasks"
            description = "Prints versionName for $variantName build type"
            doLast {
                println(variantVersionName)
            }
        }
    }
}

dependencies {
    implementation(project(":core"))

    applyBasicFunctionDependencies()
    applyBasicUiDependencies()
    applyNetworkDependencies()
    applyCoroutinesDependencies()
    applyLifecycleDependencies()
    applyHiltDependencies()
    applyPagingDependencies()
    applyLocalDbDependencies()
    applyGlideDependencies()

    // Specific dependencies
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
