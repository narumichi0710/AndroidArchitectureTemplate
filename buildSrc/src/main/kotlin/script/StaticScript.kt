package script

import ProjectProperty
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseFlavor
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.ProductFlavor
import org.gradle.api.*
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
    private fun defaultConfig(defaultConfig: DefaultConfig) {
        defaultConfig.apply {
            minSdk = ProjectProperty.MIN_SDK_VERSION
            multiDexEnabled = true
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables.useSupportLibrary = true
        }
    }

    /**
     * 各モジュールの共通ビルド設定
     */
    private fun commonBaseExtension(
        baseExtension: BaseExtension,
        isRoot: Boolean,
        project: Project?
    ) = baseExtension.apply {
        parameterizeBuildFlavorSetting(this, isRoot, project)
        packagingOptions {
            excludes.addAll(excludeList)
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        lintOptions {
            isAbortOnError = true
        }
    }

    /**
     * コンパイル時にファイル名が重複した時に無視するルールリスト
     */
    private val excludeList = listOf(
        "META-INF/*.kotlin_module",
        "META-INF/*.md",
        "/META-INF/AL2.0",
        "/META-INF/LGPL2.1",
        "**/kotlin/**"
    )

    /**
     * ProductFlavorとBuildTypeごとの設定処理を呼び出す
     */
    private fun parameterizeBuildFlavorSetting(
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
                            executeBuildType(isRoot, baseExtension, this, flavorType, buildTypeType)
                            executeBuildConfig(this@create, flavorType, buildTypeType)
                            executeManifestPlaceHolder(baseExtension, flavorType, buildTypeType)
                            executeSigningConfigs(
                                isRoot,
                                baseExtension,
                                project,
                                this@create,
                                flavorType,
                                buildTypeType
                            )
                        }
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

    /**
     * BuildTypeに応じた設定
     */
    private fun executeBuildType(
        isRoot: Boolean,
        baseExtension: BaseExtension,
        objectContainer: NamedDomainObjectContainer<BuildType>,
        flavorType: ProjectProperty.FlavorType,
        buildTypeType: ProjectProperty.BuildTypeType
    ) = objectContainer.apply {
        Action<BuildType> {
            buildTypeType.action(this, flavorType)
            if (buildTypeType == ProjectProperty.BuildTypeType.release) {
                releaseBuildSetting(isRoot, baseExtension, this)
            } else {
                debugBuildSetting(baseExtension, this)
            }
            if (isRoot && flavorType != ProjectProperty.FlavorType.prod) {
                applicationIdSuffix = flavorType.name
            }
        }.let {
            if (buildTypeType in defaultExistBuildType) getByName(buildTypeType.name, it)
            else create(buildTypeType.name, it)
        }
    }

    /**
     * リリースビルドに対する設定
     */
    private fun releaseBuildSetting(
        isRoot: Boolean,
        baseExtension: BaseExtension,
        buildType: BuildType
    ) = buildType.apply {
        if (isRoot) {
            isShrinkResources = true
        }
        isMinifyEnabled = true
        proguardFiles(
            baseExtension.getDefaultProguardFile("proguard-android.txt"),
            "proguard-rules.pro"
        )
    }

    /**
     * デバッグビルドに対する設定
     */
    private fun debugBuildSetting(
        baseExtension: BaseExtension,
        buildType: BuildType
    ) = buildType.apply {
        baseExtension.splits {
            abi.isEnable = false
            density.isEnable = false
        }
    }

    private const val SIGNING_KEY = "release"

    /**
     * SigningConfigsの設定。
     * パスワードは環境変数から取得し、KeystoreはBase64エンコードされた文字列をCIのシークレットに保存し、
     * ビルドのたびにデコードしてファイル化するようにする。
     */
    private fun executeSigningConfigs(
        isRoot: Boolean,
        baseExtension: BaseExtension,
        project: Project?,
        productFlavor: ProductFlavor,
        flavorType: ProjectProperty.FlavorType,
        buildTypeType: ProjectProperty.BuildTypeType
    ) {
        if (
            isRoot && project != null &&
            flavorType == ProjectProperty.FlavorType.prod &&
            buildTypeType == ProjectProperty.BuildTypeType.release
        ) baseExtension.signingConfigs {
            create(SIGNING_KEY) {
                keyAlias = SIGNING_KEY
                keyPassword = getConfigValue(
                    project,
                    ProjectProperty.BuildVariantType.ANDROID_KEY_PASSWORD.name
                )
                storePassword = getConfigValue(
                    project,
                    ProjectProperty.BuildVariantType.ANDROID_STORE_PASSWORD.name
                )
                storeFile = getConfigValue(
                    project,
                    ProjectProperty.BuildVariantType.ANDROID_KEYSTORE_FILE_PATH.name
                )?.let { File(it) }
            }
            productFlavor.signingConfig = baseExtension.signingConfigs.getAt(SIGNING_KEY)
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
     * ManifestPlaceHolderTypeからManifestPlaceHolderに値を設定する
     */
    private fun executeManifestPlaceHolder(
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
    private fun executeBuildConfig(
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