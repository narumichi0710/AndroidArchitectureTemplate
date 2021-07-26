package script

import ProjectProperty
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseFlavor
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion

/**
 * 全プロジェクトで弄ることがないビルドスクリプトを置く場所
 */

object StaticScript {

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
            buildFlavor(this, isRoot)
            packagingOptions {
                exclude("META-INF/*.kotlin_module")
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }

    private fun buildFlavor(baseExtension: BaseExtension, isRoot: Boolean) = baseExtension.apply {
        flavorDimensions("environment")
        releaseBuildSetting(baseExtension, isRoot)
        productBuildSetting(baseExtension, isRoot)
    }

    private fun releaseBuildSetting(baseExtension: BaseExtension, isRoot: Boolean) =
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
        }

    private fun productBuildSetting(
        baseExtension: BaseExtension,
        isRoot: Boolean
    ) = baseExtension.apply {
        productFlavors {
            ProjectProperty.FlavorType.values().forEach { flavorType ->
                create(flavorType.name) {
                    buildTypes {
                        ProjectProperty.BuildTypeType.values().forEach { buildTypeType ->
                            if (buildTypeType in defaultExistBuildType) getByName(buildTypeType.name) {
                                buildTypeType.action(this, flavorType)
                            }
                            else create(buildTypeType.name) {
                                buildTypeType.action(this, flavorType)
                            }
                            setBuildConfig(this@create, flavorType, buildTypeType)
                            setManifestPlaceHolder(baseExtension, flavorType, buildTypeType)
                        }
                    }
                    if (isRoot && flavorType != ProjectProperty.FlavorType.prod) {
                        applicationIdSuffix = flavorType.name
                    }
                }
            }
        }
    }

    /**
     * gradle.ktsのBuildTypeの仕様として
     * すでに存在するもの(release, debug)は取得、
     * 自分で新たに作ったものは新規作成となっているので
     * その条件分岐を判定するためのリスト
     */
    private val defaultExistBuildType = listOf(
        ProjectProperty.BuildTypeType.release,
        ProjectProperty.BuildTypeType.debug
    )

    private fun setBuildConfig(
        baseFlavor: BaseFlavor,
        flavorType: ProjectProperty.FlavorType,
        buildTypeType: ProjectProperty.BuildTypeType
    ) {
        ProjectProperty.BuildConfig.values().forEach {
            getBuildConfigTypeFullPath(it.type)?.let { type ->
                baseFlavor.buildConfigField(
                    type,
                    it.name,
                    it.value(flavorType, buildTypeType)
                )
            }
        }
    }

    private fun setManifestPlaceHolder(
        baseExtension: BaseExtension,
        flavorType: ProjectProperty.FlavorType,
        buildTypeType: ProjectProperty.BuildTypeType
    ) {
        ProjectProperty.ManifestPlaceHolderType.values()
            .map { it.name to it.value(flavorType, buildTypeType) }
            .let { baseExtension.defaultConfig.addManifestPlaceholders(it.toMap()) }
    }

    private fun getBuildConfigTypeFullPath(
        buildConfigType: ProjectProperty.IBuildConfigType
    ): String? = when (buildConfigType) {
        is ProjectProperty.BuildConfigType -> buildConfigType.name
        is ProjectProperty.CustomBuildConfigType -> buildConfigType.fileFullPath
        else -> null
    }
}