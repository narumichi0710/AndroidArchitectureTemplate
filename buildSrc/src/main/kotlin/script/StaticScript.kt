package script

import ProjectProperty
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseFlavor
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import java.io.File
import java.util.*

/**
 * 全プロジェクトで弄ることがないビルドスクリプトを置く場所
 */

object StaticScript {

    fun baseExtension(
        baseExtension: BaseExtension,
        isRoot: Boolean = false,
        project: Project? = null
    ) {
        defaultConfig(baseExtension.defaultConfig)
        commonBaseExtension(baseExtension, isRoot, project)
    }

    /**
     * 各モジュール共通のdefaultConfigを設定
     */
    private fun defaultConfig(defaultConfig: DefaultConfig) =
        defaultConfig.apply {
            minSdkVersion(ProjectProperty.MIN_SDK_VERSION)
            multiDexEnabled = true
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }

    /**
     * 各モジュールの共通ビルド設定
     */
    private fun commonBaseExtension(baseExtension: BaseExtension, isRoot: Boolean, project: Project?) =
        baseExtension.apply {
            releaseBuildSetting(baseExtension, isRoot)
            buildFlavor(this, isRoot, project)
            packagingOptions {
                exclude("META-INF/*.kotlin_module")
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }

    /**
     * リリースビルド限定の設定を呼び出す
     */
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

    /**
     * ProductFlavorとBuildTypeごとの設定処理を呼び出す
     */
    private fun buildFlavor(
        baseExtension: BaseExtension,
        isRoot: Boolean,
        project: Project?
    ) = baseExtension.apply {
        flavorDimensions("environment")
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
                            if (
                                isRoot && project != null &&
                                flavorType == ProjectProperty.FlavorType.prod &&
                                buildTypeType == ProjectProperty.BuildTypeType.release
                            ) {
                                setSigningConfigs(this@apply, project)
                                signingConfig = signingConfigs.getAt(SIGNING_KEY)
                            }
                        }
                    }
                    if (isRoot && flavorType != ProjectProperty.FlavorType.prod) {
                        applicationIdSuffix = flavorType.name
                    }
                }
            }
        }
    }

    private const val SIGNING_KEY = "release"

    /**
     * SigningConfigsの設定。
     * パスワードは環境変数から取得し、KeystoreはBase64エンコードされた文字列をCIのシークレットに保存し、
     * ビルドのたびにデコードしてファイル化するようにする。
     */
    private fun setSigningConfigs(baseExtension: BaseExtension, project: Project) {
        baseExtension.signingConfigs {
            create(SIGNING_KEY) {
                keyAlias = SIGNING_KEY
                keyPassword = getConfigValue(project, ProjectProperty.BuildVariantType.ANDROID_KEY_PASSWORD.name)
                storePassword = getConfigValue(project, ProjectProperty.BuildVariantType.ANDROID_STORE_PASSWORD.name)
                storeFile = getConfigValue(project, ProjectProperty.BuildVariantType.ANDROID_KEYSTORE_FILE_PATH.name)
                    ?.let { File(it) }
            }
        }
    }

    /**
     * local.propertiesもしくはCIの環境変数からキーの値のStringを取得するための関数
     */
    private fun getConfigValue(
        project: Project,
        key: String
    ): String? = runCatching { project.properties[key] as String }
        .recover { System.getenv(key) }
        .getOrNull()

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

    /**
     * ManifestPlaceHolderTypeからManifestPlaceHolderに値を設定する
     */
    private fun setManifestPlaceHolder(
        baseExtension: BaseExtension,
        flavorType: ProjectProperty.FlavorType,
        buildTypeType: ProjectProperty.BuildTypeType
    ) {
        ProjectProperty.ManifestPlaceHolderType.values()
            .map { it.name to it.value(flavorType, buildTypeType) }
            .let { baseExtension.defaultConfig.addManifestPlaceholders(it.toMap()) }
    }

    /**
     * BuildConfigTypeからBuildConfig設定処理を呼び出す
     */
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

    /**
     * BuildConfigの型をenumから特定する処理
     */
    private fun getBuildConfigTypeFullPath(
        buildConfigType: ProjectProperty.IBuildConfigType
    ): String? = when (buildConfigType) {
        is ProjectProperty.BuildConfigType -> buildConfigType.name
        is ProjectProperty.CustomBuildConfigType -> buildConfigType.fileFullPath
        else -> null
    }
}