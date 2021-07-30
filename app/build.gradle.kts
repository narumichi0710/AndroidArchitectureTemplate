android {
    defaultConfig {
        setCompileSdkVersion(30)
        applicationId = ProjectProperty.APPLICATION_ID
    }
    script.StaticScript.baseExtension(this, true, project)
}

plugins {
    id("com.android.application")
    kotlin("android")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
}

moduleStructure()