import com.android.build.gradle.internal.dsl.BuildType

object ProjectProperty {

    const val APPLICATION_ID = "com.example.app"

    const val MIN_SDK_VERSION = 23

    internal enum class FlavorType {
        prod, stg, dev
    }

    //FIXME:できればかっこいい名前にしたい
    internal enum class BuildTypeType(val action: (BuildType, FlavorType) -> Unit) {
        debug({_, _ ->}),
        release({_, _ ->})
    }

    internal enum class BuildConfig(
        val type: IBuildConfigType,
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

    internal enum class BuildConfigType : IBuildConfigType {
        Boolean, String
    }

    internal enum class CustomBuildConfigType(val fileFullPath: String) : IBuildConfigType {

    }

    interface IBuildConfigType

    private fun baseUrl(prefix: String?): String = "\"https://${prefix ?: ""}.arsaga.jp/v1/api/\""

}