package script

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler


object ModuleExtension {

    /**
     * モジュール一覧から特定のレイヤーのものだけをフィルタリングして返す関数
     */
    internal fun byLayerModuleList(layerType: ModuleStructure.LayerType): List<Module.Type> =
        Module.Type
            .values().toList()
            .filter { it.name.contains(layerType.name) }

    internal fun findModuleType(project: Project): Module.Type? {
        val projectName = convertEnumName(project)
        return Module.Type.values()
            .find { projectName == it.name }
    }

    internal fun implAllModule(
        rootModuleType: Module.Type,
        importAction: (Module.Type) -> Unit
    ) {
        Module.Type.values().toSet()
            .minus(rootModuleType).forEach {
                importAction(it)
            }
    }

    internal fun implDomainModule(
        dependencyHandler: DependencyHandler,
        moduleType: Module.Type
    ) {
        if (isUnnecessaryImplModule(moduleType)) return
        val needLayerModuleList = filteringNeedLayer(moduleType)
        Module.Type.values()
            .filter { it.domainType == moduleType.domainType && it.layerType in needLayerModuleList }
            .forEach {
                dependencyHandler.impl(it)
            }
        sameLayerCoreModule(moduleType)?.let {
            dependencyHandler.api(it)
        }
    }

    /**
     * appとドメインがcoreのモジュールはレイヤー構造が変わるので弾く
     */
    private fun isUnnecessaryImplModule(
        moduleType: Module.Type
    ): Boolean = moduleType.domainType in listOf(null, ModuleStructure.DomainType.core)

    /**
     * 渡されたモジュールのレイヤーに必要とされるレイヤーのリストをアーキテクチャ構成図にしたがって返す
     * appとgatewayはドメインごとの区切りがないので不要になる
     */
    private fun filteringNeedLayer(
        moduleType: Module.Type
    ): List<ModuleStructure.LayerType> = when (moduleType.layerType) {
        ModuleStructure.LayerType.view ->
            listOf(
                ModuleStructure.LayerType.viewModel,
                ModuleStructure.LayerType.useCase
            )
        ModuleStructure.LayerType.viewModel ->
            listOf(
                ModuleStructure.LayerType.repository,
                ModuleStructure.LayerType.useCase
            )
        ModuleStructure.LayerType.repository ->
            listOf(ModuleStructure.LayerType.useCase)
        else -> listOf()
    }

    private fun sameLayerCoreModule(
        moduleType: Module.Type
    ): Module.Type? = Module.Type.values()
        .find { it.domainType == ModuleStructure.DomainType.core && it.layerType == moduleType.layerType }

    private fun convertEnumName(project: Project): String = project.path
        .replace(":", "_")

    fun convertModulePath(moduleType: Module.Type): String = moduleType.name
        .replace("_", ":")

    internal fun DependencyHandler.api(moduleType: Module.Type) {
        convertModulePath(moduleType).let { modulePath ->
            println("structure:api => $modulePath")
            add("api", project(mapOf("path" to modulePath)))
        }
    }

    internal fun DependencyHandler.impl(moduleType: Module.Type) {
        convertModulePath(moduleType).let { modulePath ->
            println("structure:impl => $modulePath")
            add("implementation", project(mapOf("path" to modulePath)))
        }
    }
}
