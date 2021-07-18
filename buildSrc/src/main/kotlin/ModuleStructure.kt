import ModuleExtension.api
import ModuleExtension.impl
import org.gradle.api.Project

object ModuleStructure {

    /**
     * モジュール一覧
     * モジュール追加時に手動で追加する
     * 命名規則として'_'を':'に置換してimplするので
     * モジュール名に合わせて頭と各単語間に'_'が必須
     */
    internal enum class ModuleType {
        _app,
        _presentation_viewModel_auth,
        _presentation_viewModel_core,
        _dataStore_gateway,
        _dataStore_gateway_server,
        _dataStore_gateway_local,
        _dataStore_gateway_sdk,
        _domain_service_auth,
        _domain_service_core,
        _domain_entity_auth,
        _domain_entity_core,
    }

    /**
     * モジュールの各レイヤーに特徴的な名前の種類
     */
    internal enum class LayerType {
        viewModel,
        gateway,
        service,
        entity
    }

    /**
     * 各モジュールがどのモジュールをインポートするのかを定義するスイッチ文
     * appモジュールは全モジュールをimplするようにしている
     */
    internal fun implModuleByLayerType(
        project: Project,
        moduleType: ModuleType
    ) = project.dependencies.apply {
        when (moduleType) {
            ModuleType._app -> ModuleExtension.implAllModule(ModuleType._app) {
                impl(it)
            }
            ModuleType._presentation_viewModel_auth -> {
                api(ModuleType._presentation_viewModel_core)
                api(ModuleType._domain_service_auth)
            }
            ModuleType._presentation_viewModel_core -> {
                api(ModuleType._domain_service_core)
            }
            ModuleType._domain_service_auth -> {
                api(ModuleType._domain_service_core)
                api(ModuleType._domain_entity_auth)
            }
            ModuleType._domain_service_core -> {
                api(ModuleType._domain_entity_core)
            }
            ModuleType._domain_entity_auth -> {
                api(ModuleType._domain_entity_core)
            }
            ModuleType._domain_entity_core -> {
            }
            ModuleType._dataStore_gateway -> {
                api(ModuleType._dataStore_gateway_server)
                api(ModuleType._dataStore_gateway_local)
                api(ModuleType._dataStore_gateway_sdk)
            }
            ModuleType._dataStore_gateway_sdk -> {
            }
            ModuleType._dataStore_gateway_local -> {
                impl(ModuleType._domain_entity_core)
            }
            ModuleType._dataStore_gateway_server -> {
                ModuleExtension.byLayerModuleList(LayerType.entity).forEach {
                    api(it)
                }
            }
        }
    }
}