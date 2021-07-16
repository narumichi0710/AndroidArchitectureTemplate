import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion

object StaticProperty {

    fun baseExtension(project: BaseExtension, isRoot: Boolean = false) {
        defaultConfig(project.defaultConfig)
        commonBaseExtension(project, isRoot)
    }

    private fun defaultConfig(defaultConfig: DefaultConfig) =
        defaultConfig.apply {
            minSdkVersion(ProjectProperty.MIN_SDK_VERSION)
            multiDexEnabled = true
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

    private fun commonBaseExtension(baseExtension: BaseExtension, isRoot: Boolean) = baseExtension.apply {
        ProjectProperty.buildFlavor(this, isRoot)
        if (isRoot) {
            buildTypes {
                getByName("release") {
                    isShrinkResources = true
                    isMinifyEnabled = true
                    isUseProguard = true
                    proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
                }
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