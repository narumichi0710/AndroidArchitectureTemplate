import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import com.android.build.gradle.BaseExtension
import com.android.build.api.dsl.ApplicationVariantDimension

android {
    defaultConfig {
        applicationId = ProjectProperty.APPLICATION_ID
    }
    script.StaticScript.baseExtension(this, true, project)
    buildTypes {
        getByName(ProjectProperty.BuildTypeType.release.name) {
            signing(this@android, this)
        }
    }
}

fun signing(
    baseExtension: BaseExtension,
    applicationVariant: ApplicationVariantDimension
) {
    baseExtension.signingConfigs {
        val localProperties = gradleLocalProperties(rootDir)
        create(ProjectProperty.BuildTypeType.release.name) {
            val fileName = project.rootDir.path.plus("/app/keystore.jks")
            localProperties
                .getProperty(ProjectProperty.LocalVariantType.ENCODED_DEBUG_KEYSTORE.name)?.let {
                    ProcessBuilder("sh", "-c", "echo $it | base64 -d > $fileName")
                        .redirectErrorStream(true)
                        .start()?.let {
                            it.waitFor(20L, TimeUnit.SECONDS)
                            it.destroy()
                        }
                }
            storeFile = File(fileName)
            storePassword = localProperties
                .getProperty(ProjectProperty.LocalVariantType.ANDROID_STORE_PASSWORD.name)
            keyPassword = localProperties
                .getProperty(ProjectProperty.LocalVariantType.ANDROID_KEY_PASSWORD.name)
            keyAlias = localProperties
                .getProperty(ProjectProperty.LocalVariantType.ANDROID_KEY_ALIAS.name)
        }
    }
    applicationVariant.signingConfig = baseExtension
        .signingConfigs.getByName(ProjectProperty.BuildTypeType.release.name)
}

plugins {
    id("com.android.application")
    kotlin("android")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
}

moduleStructure()

val rootPath: String? = project.rootDir.path

val settingsGradlePath = rootPath?.plus("/settings.gradle.kts")

task("scaffold") {
    settingsGradlePath
        ?.run { script.ScaffoldExtension.settingModuleNameList(this) }
        ?.run { script.ScaffoldExtension.missingModuleNameList(this) }
        ?.takeIf { it.isNotEmpty() }
        ?.let {
            runBlocking(kotlinx.coroutines.Dispatchers.Default) {
                listOf(
                    async {
                        rootPath?.plus(ProjectModule.THIS_FILE_PATH)
                            ?.run(script.ScaffoldExtension::updateProjectModuleType)
                    },
                    async {
                        script.ScaffoldExtension.updateSettingModule(settingsGradlePath, it)
                    },
                    async {
                        rootPath
                            ?.run { script.ScaffoldExtension.generateNewModule(this, it) }
                            ?.let { parallelGenerateFile(this, it) }
                    }
                ).awaitAll()
            }
        }
}

suspend fun parallelGenerateFile(
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    commandList: List<() -> List<() -> Unit>>
) = coroutineScope.apply {
    commandList
        .map { async { it() } }
        .awaitAll()
        .map {
            it.map { async { it() } }
                .awaitAll()
        }
}