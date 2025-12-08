plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ripple.trackingmaster.devicetrackapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ripple.trackingmaster.devicetrackapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"

        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"   // ✅ Works with Kotlin 1.9.25
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}
kapt {
    correctErrorTypes = true
    includeCompileClasspath = false
}

dependencies {

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation(libs.support.annotations)
    implementation(libs.androidx.annotation)
    debugImplementation("androidx.compose.ui:ui-tooling")


    implementation("androidx.appcompat:appcompat:1.7.0")


    implementation("com.squareup:javapoet:1.13.0")


    implementation("androidx.datastore:datastore-preferences:1.1.1")




    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ✅ ADD THESE 3 LINES FOR RETROFIT (NETWORKING)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // For debugging

    // Accompanist - System bar control & Permissions
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")


    // ✅ ADD THESE TWO LINES TO FIX THE BUILD ERROR
    testImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptTest("com.google.dagger:hilt-compiler:2.51.1")

    // ✅ ADD THESE TWO LINES TO FIX THE NEW BUILD ERROR
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.51.1")
}