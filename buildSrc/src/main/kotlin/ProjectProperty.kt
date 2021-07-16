import com.android.build.gradle.BaseExtension

object ProjectProperty {

    const val APPLICATION_ID = "com.example.app"

    const val MIN_SDK_VERSION = 23

    private val FLAVORS = mapOf(
        "prod" to true,
        "stg" to false,
        "dev" to false
    )

    fun buildFlavor(baseExtension: BaseExtension, isRoot: Boolean) = baseExtension.apply {
        flavorDimensions("environment")
        defaultConfig {
            buildConfigField("Boolean", "IS_DEBUG_LOGGING", true.toString())
        }
        fun baseUrl(prefix: String?): String = "\"https://${prefix ?: ""}.arsaga.jp/v1/api/\""
        productFlavors {
            FLAVORS.forEach {
                create(it.key) {
                    val isProd = it.value
                    if (isProd) {
                        buildConfigField("Boolean", "IS_DEBUG_LOGGING", false.toString())
                    } else {
                        buildConfigField("String", "BASE_URL", baseUrl(it.key))
                        if (isRoot) applicationIdSuffix = it.key
                    }
                }
            }
        }
    }
}