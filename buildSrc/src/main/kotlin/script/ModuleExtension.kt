package script

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler


object ModuleExtension {

    /**
     * モジュール一覧から特定のレイヤーのものだけをフィルタリングして返す関数
     */
    internal fun byLayerModuleList(layerType: ModuleStructure.LayerType): List<ModuleStructure.ModuleType> =
        ModuleStructure.ModuleType
            .values().toList()
            .filter { it.name.contains(layerType.name) }

    internal fun findModuleType(project: Project): ModuleStructure.ModuleType? {
        val projectName = convertEnumName(project)
        return ModuleStructure.ModuleType.values()
            .find { projectName == it.name }
    }

    internal fun implAllModule(
        rootModuleType: ModuleStructure.ModuleType,
        importAction: (ModuleStructure.ModuleType) -> Unit
    ) {
        ModuleStructure.ModuleType.values().toSet()
            .minus(rootModuleType).forEach {
                importAction(it)
            }
    }

    internal fun implDomainModule(
        dependencyHandler: DependencyHandler,
        moduleType: ModuleStructure.ModuleType
    ) {
        if (isUnnecessaryImplModule(moduleType)) return
        val needLayerModuleList = filteringNeedLayer(moduleType)
        ModuleStructure.ModuleType.values()
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
        moduleType: ModuleStructure.ModuleType
    ): Boolean = moduleType.domainType in listOf(null, ModuleStructure.DomainType.core)

    /**
     * 渡されたモジュールのレイヤーに必要とされるレイヤーのリストをアーキテクチャ構成図にしたがって返す
     * appとgatewayはドメインごとの区切りがないので不要になる
     */
    private fun filteringNeedLayer(
        moduleType: ModuleStructure.ModuleType
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
        moduleType: ModuleStructure.ModuleType
    ): ModuleStructure.ModuleType? = ModuleStructure.ModuleType.values()
        .find { it.domainType == ModuleStructure.DomainType.core && it.layerType == moduleType.layerType }

    private fun convertEnumName(project: Project): String = project.path
        .replace(":", "_")

    private fun convertModulePath(moduleType: ModuleStructure.ModuleType): String = moduleType.name
        .replace("_", ":")

    internal fun DependencyHandler.api(moduleType: ModuleStructure.ModuleType) {
        convertModulePath(moduleType).let { modulePath ->
            println("structure:api => $modulePath")
            add("api", project(mapOf("path" to modulePath)))
        }
    }

    internal fun DependencyHandler.impl(moduleType: ModuleStructure.ModuleType) {
        convertModulePath(moduleType).let { modulePath ->
            println("structure:impl => $modulePath")
            add("implementation", project(mapOf("path" to modulePath)))
        }
    }
}
