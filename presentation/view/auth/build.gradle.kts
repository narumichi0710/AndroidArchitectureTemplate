android {
    script.StaticScript.baseExtension(this)
}

plugins {
    id("com.android.library")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
}

moduleStructure()