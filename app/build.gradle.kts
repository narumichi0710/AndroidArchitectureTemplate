android {
    defaultConfig {
        setApplicationId(ProjectProperty.APPLICATION_ID)
    }
    StaticProperty.baseExtension(this, true)
}

plugins {
    id("com.android.application")
    kotlin("android")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
}