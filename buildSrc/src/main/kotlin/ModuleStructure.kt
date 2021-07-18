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
        _dataStore_repository,
        _dataStore_repository_auth,
        _dataStore_repository_core,
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
        /**
         * AACのViewModelを継承するクラスを置くモジュール
         * 1.repositoryクラスとserviceクラス紐付け
         * 2.複数画面で共有されるインスタンスとしてライフタイムへの対応
         * 3.AndroidViewのリスナーからserviceの各メソッドを叩く繋ぎ込み処理
         * 4.serviceから直接アクセスさせないxmlリソースへのアクセス
         * などを行う
         */
        viewModel,

        /**
         * データの実体インスタンスを保持・管理するクラスを置くモジュール
         * gatewayモジュールの関数を順番/並列に叩く処理の管理も行う
         * 基本的にクラス内にデータを保持するがドメイン(モジュール)をまたぐデータ型の場合は
         * coreモジュールのシングルトン内にプロパティを作りその値をカスタムgetterで渡すようにする
         * 共有するデータが多い(Like問題)ことが見込まれる場合はシングルトンではなくDBの採用を検討する
         */
        repository,

        /**
         * IO処理を記述するモジュール
         * API、DB、Preference、SaaSなどへのデータ取得処理を記述する
         * 上位のgatewayでは各IOを順番に同期的に呼び出す必要がある時のメソッドを記述する
         * 並列化やキャッシュコントロールはここでは書かない
         */
        gateway,

        //これより下はAndroidSDKに依存しないロジックを記述するためユニットテストを書きやすい
        /**
         * ユーザー視点での機能を表現するクラスを置くモジュール
         * コルーチン(Flowなどの拡張も含む)とentityモジュールだけで実現できるロジックを書く
         * viewModelと分けている理由はテスタビリティのため
         */
        service,

        /**
         * データの型とenumとAndroidに依存しないUtilロジックのクラスを置くモジュール
         * 1つのデータだけで完結するシンプルなクエリメソッドはこのモジュールのクラスにメンバメソッドか拡張関数として書く
         */
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
            ModuleType._dataStore_repository -> {
                ModuleExtension.byLayerModuleList(LayerType.repository).forEach {
                    if (it != ModuleType._dataStore_repository) api(it)
                }
            }
            ModuleType._dataStore_repository_auth -> {
                api(ModuleType._dataStore_repository_core)
                impl(ModuleType._domain_service_auth)
            }
            ModuleType._dataStore_repository_core -> {
                impl(ModuleType._dataStore_gateway_sdk)
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