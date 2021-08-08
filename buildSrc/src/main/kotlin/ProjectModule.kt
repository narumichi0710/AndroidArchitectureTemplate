/**
 * モジュール一覧
 * 命名規則として'_'を':'に置換してimplするので
 * モジュール名に合わせて頭と各単語間に'_'が必須
 */

object ProjectModule {
    enum class Type(val layerType: ModuleStructure.LayerType?, val domainType: ModuleStructure.DomainType?) {
        _presentation_view_auth(ModuleStructure.LayerType.view, ModuleStructure.DomainType.auth),
        _presentation_viewModel_auth(ModuleStructure.LayerType.viewModel, ModuleStructure.DomainType.auth),
        _dataStore_repository_auth(ModuleStructure.LayerType.repository, ModuleStructure.DomainType.auth),
        _domain_useCase_auth(ModuleStructure.LayerType.useCase, ModuleStructure.DomainType.auth),
        _domain_entity_auth(ModuleStructure.LayerType.entity, ModuleStructure.DomainType.auth),
        _presentation_view_core(ModuleStructure.LayerType.view, ModuleStructure.DomainType.core),
        _presentation_viewModel_core(ModuleStructure.LayerType.viewModel, ModuleStructure.DomainType.core),
        _domain_useCase_core(ModuleStructure.LayerType.useCase, ModuleStructure.DomainType.core),
        _dataStore_repository_core(ModuleStructure.LayerType.repository, ModuleStructure.DomainType.core),
        _domain_entity_core(ModuleStructure.LayerType.entity, ModuleStructure.DomainType.core),
        _app(null, null),
        _dataStore_repository(ModuleStructure.LayerType.repository, null),
        _dataStore_gateway_server(ModuleStructure.LayerType.gateway, null),
        _dataStore_gateway_sdk(ModuleStructure.LayerType.gateway, null),
        _dataStore_gateway_local(ModuleStructure.LayerType.gateway, null),
        _extension_view(ModuleStructure.LayerType.view, null),
        _extension_viewModel(ModuleStructure.LayerType.viewModel, null),
        _extension_repository(ModuleStructure.LayerType.repository, null),
        _extension_gateway(ModuleStructure.LayerType.gateway, null),
    }
    data class Entity(
        val name: String,
        val layerType: ModuleStructure.LayerType,
        val domainType: ModuleStructure.DomainType
    )
}