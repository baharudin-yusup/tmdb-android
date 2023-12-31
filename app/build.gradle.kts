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
        versionCode = 3
        versionName = "1.2.0"

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
                // Create a variable called keystorePropertiesFile, and initialize it to your
                // keystore.properties file, in the rootProject folder.
                val keystorePropertiesFile = rootProject.file("keystore.properties")

                // Initialize a new Properties() object called keystoreProperties.
                val keystoreProperties = Properties()

                // Load your keystore.properties file into the keystoreProperties object.
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))

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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

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
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // UI
    applyBasicUiDependencies()
    applyPagingDependencies()
    applyComposeUiDependencies()

    applyBasicFunctionDependencies()


    applyNetworkDependencies()
    applyCoroutinesDependencies()
    applyLifecycleDependencies()
    applyHiltDependencies()
    applyLocalDbDependencies()
    applyGlideDependencies()

    // Specific dependencies
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")



    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}
