import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion

object StaticProperty {

    data class Version(
        val code: Int,
        val name: String
    )

    fun defaultConfig(defaultConfig: DefaultConfig, version: Version) = defaultConfig.apply {
        applicationId = "com.example.app"
        minSdkVersion(21)
        commonSetting(this, version)
    }

    fun baseExtension(baseExtension: BaseExtension) = baseExtension.apply {
        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
        }
        commonBaseExtension(this)
    }

    private fun commonSetting(defaultConfig: DefaultConfig, version: Version) = defaultConfig.apply {
        versionCode = version.code
        versionName = version.name
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    private fun commonBaseExtension(baseExtension: BaseExtension) = baseExtension.apply {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}