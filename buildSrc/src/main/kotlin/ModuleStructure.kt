import Extension.api
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

object ModuleStructure {

    const val coreModulePathPostfix = "core"

    internal enum class DomainType {
        auth
    }

    internal enum class LayerType {
        _presentation_viewModel_,
        _domain_service_,
        _domain_entity_,
    }
}

fun Project.moduleStructure() {
    afterEvaluate {
    }
}

}
object Extension {

    internal fun findLayerType(project: Project): ModuleStructure.LayerType? {
        val projectName = convertEnumName(project)
        return ModuleStructure.LayerType.values()
            .find { projectName.startsWith(it.name) }
    }

    private fun convertEnumName(project: Project): String = project.path
        .replace(":", "_")

    internal fun convertModulePath(layerType: ModuleStructure.LayerType): String = layerType.name
        .replace("_", ":")


    internal fun DependencyHandler.api(modulePath: String) {
        println("structure:api => $modulePath")
        add("api", project(mapOf("path" to modulePath)))
    }

    internal fun DependencyHandler.impl(modulePath: String) {
        println("structure:impl => $modulePath")
        add("implementation", project(mapOf("path" to modulePath)))
    }
}
