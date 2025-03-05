// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Firebase
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Safe Args for Navigation
    id("androidx.navigation.safeargs") version "2.8.8" apply false
}