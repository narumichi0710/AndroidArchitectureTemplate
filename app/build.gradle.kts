android {
    defaultConfig {
        setCompileSdkVersion(30)
        applicationId = ProjectProperty.APPLICATION_ID
    }
    script.StaticScript.baseExtension(this, true, project)
}

plugins {
    id("com.android.application")
    kotlin("android")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
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
            rootPath?.plus(ProjectModule.THIS_FILE_PATH)
                ?.run(script.ScaffoldExtension::updateProjectModuleType)
            script.ScaffoldExtension.updateSettingModule(settingsGradlePath, it)
        }
}


