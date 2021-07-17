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