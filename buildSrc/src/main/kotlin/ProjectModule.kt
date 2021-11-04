/**
 * モジュール一覧
 * 命名規則として'_'を':'に置換してimplするので
 * モジュール名に合わせて頭と各単語間に'_'が必須
 */

object ProjectModule {

    val THIS_FILE_PATH = "/buildSrc/src/main/kotlin/".plus(javaClass.simpleName).plus(".kt")

    data class Entity(
        val name: String,
        val layerType: ModuleStructure.LayerType,
        val domainType: ModuleStructure.DomainType
    )

    enum class CreateType {
        MANUAL, AUTO
    }

    enum class Type(
        val layerType: ModuleStructure.LayerType?,
        val domainType: ModuleStructure.DomainType?,
        val createType: CreateType
    ) {
        _app(null, null, CreateType.MANUAL),
        _dataStore_repository(ModuleStructure.LayerType.repository, null, CreateType.MANUAL),
        _dataStore_gateway_server(ModuleStructure.LayerType.gateway, null, CreateType.MANUAL),
        _dataStore_gateway_sdk(ModuleStructure.LayerType.gateway, null, CreateType.MANUAL),
        _dataStore_gateway_local(ModuleStructure.LayerType.gateway, null, CreateType.MANUAL),
        _extension_view(ModuleStructure.LayerType.view, null, CreateType.MANUAL),
        _extension_compose(ModuleStructure.LayerType.layout, null, CreateType.MANUAL),
        _extension_viewModel(ModuleStructure.LayerType.viewModel, null, CreateType.MANUAL),
        _extension_repository(ModuleStructure.LayerType.repository, null, CreateType.MANUAL),
        _extension_repositoryFlow(ModuleStructure.LayerType.repository, null, CreateType.MANUAL),
        _extension_gateway(ModuleStructure.LayerType.gateway, null, CreateType.MANUAL),
        _presentation_view_core(ModuleStructure.LayerType.view, ModuleStructure.DomainType.core, CreateType.AUTO),
        _presentation_layout_core(ModuleStructure.LayerType.layout, ModuleStructure.DomainType.core, CreateType.AUTO),
        _presentation_viewModel_core(ModuleStructure.LayerType.viewModel, ModuleStructure.DomainType.core, CreateType.AUTO),
        _dataStore_repository_core(ModuleStructure.LayerType.repository, ModuleStructure.DomainType.core, CreateType.AUTO),
        _domain_useCase_core(ModuleStructure.LayerType.useCase, ModuleStructure.DomainType.core, CreateType.AUTO),
        _domain_entity_core(ModuleStructure.LayerType.entity, ModuleStructure.DomainType.core, CreateType.AUTO),
        _presentation_view_auth(ModuleStructure.LayerType.view, ModuleStructure.DomainType.auth, CreateType.AUTO),
        _presentation_layout_auth(ModuleStructure.LayerType.layout, ModuleStructure.DomainType.auth, CreateType.AUTO),
        _presentation_viewModel_auth(ModuleStructure.LayerType.viewModel, ModuleStructure.DomainType.auth, CreateType.AUTO),
        _dataStore_repository_auth(ModuleStructure.LayerType.repository, ModuleStructure.DomainType.auth, CreateType.AUTO),
        _domain_useCase_auth(ModuleStructure.LayerType.useCase, ModuleStructure.DomainType.auth, CreateType.AUTO),
        _domain_entity_auth(ModuleStructure.LayerType.entity, ModuleStructure.DomainType.auth, CreateType.AUTO),
        ;
    }
}