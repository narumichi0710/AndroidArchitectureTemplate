import ModuleExtension.api
import org.gradle.api.Project

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
        ModuleExtension.findLayerType(this)
            ?.let { implModuleByLayerType(this, it) }
    }
}

private fun implModuleByLayerType(
    project: Project,
    layerType: ModuleStructure.LayerType
) {
    when (layerType) {
        ModuleStructure.LayerType._presentation_viewModel_ -> null
        ModuleStructure.LayerType._domain_service_ -> Impl.coreService(project)
        ModuleStructure.LayerType._domain_entity_ -> Impl.coreEntity(project)
    }
}

object Impl {
    internal fun coreService(project: Project, fromUpperLayer: Boolean = false) {
        coreEntity(project, true)
        if (!ModuleExtension.isCoreModule(project) || fromUpperLayer)
            ModuleExtension.convertModulePath(ModuleStructure.LayerType._domain_service_)
                .plus(ModuleStructure.coreModulePathPostfix)
                .run { project.dependencies.api(this) }
    }

    internal fun coreEntity(project: Project, fromUpperLayer: Boolean = false) {
        if (!ModuleExtension.isCoreModule(project) || fromUpperLayer)
            ModuleExtension.convertModulePath(ModuleStructure.LayerType._domain_entity_)
                .plus(ModuleStructure.coreModulePathPostfix)
                .run { project.dependencies.api(this) }
    }
}