plugins {
    alias(libs.plugins.android.application)

    // Firebase
    id("com.google.gms.google-services")

    // SafeArgs
    id("androidx.navigation.safeargs")
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

    implementation("com.squareup.picasso:picasso:2.71828")


    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.annotation)
    testImplementation(libs.runner)
    testImplementation(libs.ext.junit)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    implementation("net.bytebuddy:byte-buddy:1.17.1")

    implementation("io.github.ParkSangGwon:tedimagepicker:1.6.1") {
        exclude(group = "com.android.support")
    }

    implementation("com.google.firebase:firebase-storage:20.2.1")
}
