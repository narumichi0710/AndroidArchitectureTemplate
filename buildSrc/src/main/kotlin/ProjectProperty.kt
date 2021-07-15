import com.android.build.gradle.BaseExtension

object ProjectProperty {

    const val APPLICATION_ID = "com.example.app"

    const val MIN_SDK_VERSION = 23

    fun buildFlavor(baseExtension: BaseExtension) = baseExtension.apply {
        flavorDimensions("environment")
        defaultConfig {
            buildConfigField("Boolean", "IS_DEBUG_LOGGING", true.toString())
        }
        fun baseUrl(prefix: String?): String = "\"https://${prefix ?: ""}.arsaga.jp/v1/api/\""
        productFlavors {
            create("prod") {
                buildConfigField("Boolean", "IS_DEBUG_LOGGING", false.toString())
            }
            create("stg") {
                applicationIdSuffix = "stg"
                buildConfigField("String", "BASE_URL", baseUrl(applicationIdSuffix))
            }
            create("dev") {
                applicationIdSuffix = "dev"
                buildConfigField("String", "BASE_URL", baseUrl(applicationIdSuffix))
            }
        }
    }
}