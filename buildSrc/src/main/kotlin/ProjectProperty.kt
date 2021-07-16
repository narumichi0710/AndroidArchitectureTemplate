import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BuildType

object ProjectProperty {

    const val APPLICATION_ID = "com.example.app"

    const val MIN_SDK_VERSION = 23

    private enum class FlavorType {
        prod, stg, dev
    }

    //FIXME:できればかっこいい名前にしたい
    private enum class BuildTypeType(val defaultExist: Boolean, val action: (BuildType) -> Unit) {
        debug(true, {}),
        release(true, {})
    }

    private enum class BuildConfig(
        val type: BuildConfigType,
        val value: (FlavorType, BuildTypeType) -> String
    ) {
        IS_DEBUG_LOGGING(BuildConfigType.Boolean, { flavorType, _ ->
            (flavorType != FlavorType.prod).toString()
        }),
        BASE_URL(BuildConfigType.String, { flavorType, _ ->
            if (flavorType == FlavorType.prod) baseUrl("")
            else baseUrl(flavorType.name)
        })
    }

    private enum class BuildConfigType {
        Boolean, String
    }

    private fun baseUrl(prefix: String?): String = "\"https://${prefix ?: ""}.arsaga.jp/v1/api/\""

    object Script {
        fun buildFlavor(baseExtension: BaseExtension, isRoot: Boolean) = baseExtension.apply {
            flavorDimensions("environment")
            productFlavors {
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
                FlavorType.values().forEach { flavorType ->
                    create(flavorType.name) {
                        buildTypes {
                            BuildTypeType.values().forEach { buildTypeType ->
                                if (buildTypeType.defaultExist) getByName(buildTypeType.name) {
                                    buildTypeType.action(this)
                                }
                                else create(buildTypeType.name) { buildTypeType.action(this) }
                                BuildConfig.values().forEach {
                                    buildConfigField(it.type.name, it.name, it.value(flavorType, buildTypeType))
                                }
                            }
                        }
                        if (isRoot && flavorType != FlavorType.prod) {
                            applicationIdSuffix = flavorType.name
                        }
                    }
                }
            }
        }
    }
}