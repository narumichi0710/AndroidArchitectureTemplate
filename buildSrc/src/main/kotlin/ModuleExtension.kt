import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

object ModuleExtension {

    internal fun findModuleType(project: Project): ModuleStructure.ModuleType? {
        val projectName = convertEnumName(project)
        return ModuleStructure.ModuleType.values()
            .find { projectName.startsWith(it.name) }
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