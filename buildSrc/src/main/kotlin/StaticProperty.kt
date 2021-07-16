import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion

object StaticProperty {

    fun baseExtension(baseExtension: BaseExtension, isRoot: Boolean = false) {
        defaultConfig(baseExtension.defaultConfig)
        commonBaseExtension(baseExtension, isRoot)
    }

    private fun defaultConfig(defaultConfig: DefaultConfig) =
        defaultConfig.apply {
            minSdkVersion(ProjectProperty.MIN_SDK_VERSION)
            multiDexEnabled = true
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

    private fun commonBaseExtension(baseExtension: BaseExtension, isRoot: Boolean) =
        baseExtension.apply {
            buildTypes {
                getByName("release") {
                    if (isRoot) {
                        isShrinkResources = true
                    }
                    isMinifyEnabled = true
                    isUseProguard = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            ProjectProperty.buildFlavor(this, isRoot)
            packagingOptions {
                exclude("META-INF/*.kotlin_module")
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }
}