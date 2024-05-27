/*buildscript {
    ext {
        var kotlin_version = "1.9.20"
        var koin_version = "3.5.0"
        var ksp_version = "1.9.10-1.0.13"
        var koin_ksp_version = "1.3.0"
    }
}*/

plugins {
    val ksp_version = "1.9.20-1.0.14"
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
   // kotlin("kapt")
   id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.protobuf")
    //  id("com.google.devtools.ksp") version ksp_version apply false
   // kotlin("ksp") version "1.9.10-1.0.13"
   // kotlin("ksp") version "1.9.10-1.0.13"
}

android {
    namespace = "com.endofjanuary.placement_example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.endofjanuary.placement_example"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    protobuf {
        protoc {
            artifact = ("com.google.protobuf:protoc:3.20.1")
        }

        generateProtoTasks {
            all().forEach{ task ->
                task.builtins {
                    create("java") {
                        option("lite")
                    }
                    create("kotlin") {
                        option("lite")
                    }
                }
            }
        }
    }
}



// Compile time check

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("io.github.sceneview:sceneview:2.0.3")
    implementation ("io.github.sceneview:arsceneview:2.0.3")
    implementation("com.google.ar:core:1.41.0")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    //Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    //CameraX
    val camerax_version = "1.3.0"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")

    //Koin

    val koin_version = "3.5.0"
    val koin_ksp_version = "1.3.0"

    implementation("io.insert-koin:koin-core:$koin_version")
    implementation( "io.insert-koin:koin-androidx-compose:$koin_version")
    implementation ("io.insert-koin:koin-android:$koin_version")
    implementation ("io.insert-koin:koin-androidx-navigation:$koin_version")


    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("io.coil-kt:coil:2.5.0")
    implementation ("com.google.accompanist:accompanist-coil:0.15.0")

  //  implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
   // com.google.devtools.ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")

  //  ksp("com.google.dagger:hilt-android-compiler:2.33-beta")
  //  implementation ("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
//    ksp("androidx.hilt:hilt-compiler:1.0.0-beta01")
    val nav_version = "2.7.6"

    implementation("androidx.navigation:navigation-compose:$nav_version")

  //  implementation ('com.android.support:palette-v7:28.0.0')

    //room
    val room_version = "2.6.1"

    implementation("androidx.room:room-ktx:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    ksp("androidx.room:room-compiler:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    //ksp("androidx.room:room-compiler:$room_version")

    //lottie lib
    val version = "4.2.0"
    implementation("com.airbnb.android:lottie-compose:$version")

    //AWS
    implementation("aws.sdk.kotlin:s3:1.0.41")

    //flows
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    //firebase
    implementation("com.google.firebase:firebase-auth:23.0.0")

    //dataStore
    implementation ("androidx.datastore:datastore:1.1.1")
    implementation ("com.google.protobuf:protobuf-javalite:3.25.1")
    implementation ("com.google.protobuf:protobuf-kotlin-lite:3.25.1")

}