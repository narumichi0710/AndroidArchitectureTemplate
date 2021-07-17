import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

object ModuleExtension {

    internal fun findLayerType(project: Project): ModuleStructure.LayerType? {
        val projectName = convertEnumName(project)
        return ModuleStructure.LayerType.values()
            .find { projectName.startsWith(it.name) }
    }

    private fun convertEnumName(project: Project): String = project.path
        .replace(":", "_")

    internal fun convertModulePath(layerType: ModuleStructure.LayerType): String = layerType.name
        .replace("_", ":")

    internal fun isCoreModule(project: Project): Boolean = project.path
        .endsWith(ModuleStructure.coreModulePathPostfix)

    internal fun implCoreModule(
        project: Project,
        fromUpperLayer: Boolean,
        layerType: ModuleStructure.LayerType
    ) {
        if (!isCoreModule(project) || fromUpperLayer)
            convertModulePath(layerType)
                .plus(ModuleStructure.coreModulePathPostfix)
                .run { project.dependencies.api(this) }
    }

    internal fun DependencyHandler.api(modulePath: String) {
        println("structure:api => $modulePath")
        add("api", project(mapOf("path" to modulePath)))
    }

    internal fun DependencyHandler.impl(modulePath: String) {
        println("structure:impl => $modulePath")
        add("implementation", project(mapOf("path" to modulePath)))
    }
}
