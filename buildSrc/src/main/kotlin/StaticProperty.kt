import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion

object StaticProperty {

    fun baseExtension(project: BaseExtension) {
        defaultConfig(project.defaultConfig)
        commonBaseExtension(project)
    }

    private fun defaultConfig(defaultConfig: DefaultConfig) =
        defaultConfig.apply {
            applicationId = ProjectProperty.APPLICATION_ID
            minSdkVersion(ProjectProperty.MIN_SDK_VERSION)
            multiDexEnabled = true
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

    private fun commonBaseExtension(baseExtension: BaseExtension) = baseExtension.apply {
        ProjectProperty.buildFlavor(this)
        buildTypes {
            getByName("release") {
                isShrinkResources = true
                isMinifyEnabled = true
                isUseProguard = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
        }
        buildFeatures.run {
            listOf(
                ::dataBinding,
                ::viewBinding,
                ::compose
            ).forEach { it.set(true) }
        }
        packagingOptions {
            exclude("META-INF/*.kotlin_module")
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
}