plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.3")
    implementation(kotlin("gradle-plugin","1.5.31"))
}