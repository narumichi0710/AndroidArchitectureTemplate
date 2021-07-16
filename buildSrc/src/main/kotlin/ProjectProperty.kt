import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseFlavor
import com.android.build.gradle.internal.dsl.DefaultConfig

object ProjectProperty {

    const val APPLICATION_ID = "com.example.app"

    const val MIN_SDK_VERSION = 23

    private val FLAVORS = mapOf(
        "prod" to true,
        "stg" to false,
        "dev" to false
    )

    private enum class BuildConfig(val type: BuildConfigType) {
        IS_DEBUG_LOGGING(BuildConfigType.Boolean),
        BASE_URL(BuildConfigType.String)
    }

    private enum class BuildConfigType {
        Boolean, String
    }

    fun buildFlavor(baseExtension: BaseExtension, isRoot: Boolean) = baseExtension.apply {
        flavorDimensions("environment")
        defaultConfig {
            setDefaultConfig(BuildConfig.IS_DEBUG_LOGGING, true.toString())
        }
        productFlavors {
            FLAVORS.forEach {
                create(it.key) {
                    val isProd = it.value
                    if (isProd) {
                        setDefaultConfig(BuildConfig.IS_DEBUG_LOGGING, false.toString())
                        setDefaultConfig(BuildConfig.BASE_URL, baseUrl(""))
                    } else {
                        setDefaultConfig(BuildConfig.BASE_URL, baseUrl(it.key))
                        if (isRoot) applicationIdSuffix = it.key
                    }
                }
            }
        }
    }

    private fun baseUrl(prefix: String?): String = "\"https://${prefix ?: ""}.arsaga.jp/v1/api/\""

    private fun BaseFlavor.setDefaultConfig(
        buildConfig: BuildConfig,
        config: String
    ) {
        buildConfigField(buildConfig.type.name, buildConfig.name, config)
    }
}