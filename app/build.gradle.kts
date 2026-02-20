import java.io.FileInputStream
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "dev.baharudin.tmdb_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.baharudin.tmdb_android"
        minSdk = 30
        targetSdk = 34
        versionCode = 8
        versionName = "1.2.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            if (System.getenv("GH_ACTIONS_FLAG").toBoolean()) {
                storeFile = file(System.getenv("GH_ACTIONS_SIGNING_STORE_FILE_PATH"))
                storePassword = System.getenv("GH_ACTIONS_SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("GH_ACTIONS_SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("GH_ACTIONS_SIGNING_KEY_PASSWORD")
            } else {
                val keystorePropertiesFile = rootProject.file("keystore.properties")
                val keystoreProperties = Properties().apply {
                    load(FileInputStream(keystorePropertiesFile))
                }

                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
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
            ndk {
                debugSymbolLevel = "FULL"
            }

            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.configureEach {
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

    // Functions
    applyBasicFunctionDependencies()
    applyAppSpecificDependencies()
    applyFirebaseDependencies()
    applyNetworkDependencies()
    applyCoroutinesDependencies()
    applyLifecycleDependencies()
    applyHiltDependencies()
    applyLocalDbDependencies()

    // UI
    applyComposeUiDependencies()
    applyPagingDependencies()
}
