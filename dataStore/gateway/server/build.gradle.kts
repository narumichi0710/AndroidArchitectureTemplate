android {
    script.StaticScript.baseExtension(this)
}

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

moduleStructure()