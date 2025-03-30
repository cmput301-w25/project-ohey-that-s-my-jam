
plugins {
    alias(libs.plugins.android.application)

    // Firebase
    id("com.google.gms.google-services")

    // SafeArgs
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.properties"
}


android {
    namespace = "com.otmj.otmjapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.otmj.otmjapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation("com.google.android.material:material:1.6.0")  //newer version

    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.annotation)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.storage)
    implementation(libs.security.crypto)
    testImplementation(libs.runner)
    testImplementation(libs.ext.junit)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("androidx.test:rules:1.4.0")


    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    implementation("net.bytebuddy:byte-buddy:1.17.1")

    // for getting location
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.google.android.gms:play-services-maps:19.1.0")

    // for sending http requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0");
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // for retrieving environment variables
    implementation("io.github.cdimascio:java-dotenv:5.2.2") // Or the latest version

    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation("com.google.code.gson:gson:2.10.1")
}
