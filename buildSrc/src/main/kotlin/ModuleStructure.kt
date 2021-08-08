import script.ModuleExtension.api
import script.ModuleExtension.impl
import script.ModuleExtension
import org.gradle.api.Project

/**
 * 各build.gradle.ktsから呼び出す関数
 */
fun Project.moduleStructure() {
    ModuleExtension.findModuleType(this)
        ?.let { ModuleStructure.implModuleByLayerType(this, it) }
}

object ModuleStructure {

    /**
     * アプリ内で扱うドメイン(概念)一覧
     */
    enum class DomainType {
        core,
        auth
    }

    /**
     * モジュールの各レイヤーに特徴的な名前の種類
     */
    enum class LayerType {
        /**
         * レイアウト・アニメーション・ナビゲーション・端末依存解決について書くレイヤー
         * 主にActivity, Fragment,, View, Navigation, アニメーション関連のクラスを置く
         * 依存関係にまつわる主な処理はServiceにあるNavigatorを継承したクラスの作成、
         * ViewModelのコンストラクタに渡すことと、ViewModelに作ったViewEventのListenerとViewを紐づけること
         * 他の依存はデータバインディングで解決していく
         * ForegroundServiceもここで作成していく
         */
        view,

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
         * WorkManagerやServiceのクラスもここで作成する
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
        useCase,

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
        moduleType: Module.Type
    ) = project.dependencies.apply {
        when (moduleType) {
            // 特定のドメインに属するモジュール(主にここの種類が増えていく)
            Module.Type._presentation_view_auth,
            Module.Type._presentation_viewModel_auth,
            Module.Type._dataStore_repository_auth,
            Module.Type._domain_useCase_auth,
            Module.Type._domain_entity_auth -> {
                ModuleExtension.implDomainModule(this, moduleType)
            }
            // 親モジュール
            Module.Type._app -> ModuleExtension.implAllModule(Module.Type._app) {
                impl(it)
            }
            Module.Type._dataStore_repository -> {
                ModuleExtension.byLayerModuleList(LayerType.repository).forEach {
                    if (it != Module.Type._dataStore_repository) api(it)
                }
            }
            // coreモジュール
            Module.Type._presentation_view_core -> {
                api(Module.Type._presentation_viewModel_core)
                api(Module.Type._extension_view)
            }
            Module.Type._presentation_viewModel_core -> {
                api(Module.Type._domain_useCase_core)
                api(Module.Type._extension_viewModel)
            }
            Module.Type._dataStore_repository_core -> {
                api(Module.Type._dataStore_gateway_local)
                api(Module.Type._dataStore_gateway_sdk)
                api(Module.Type._dataStore_gateway_server)
                api(Module.Type._domain_useCase_core)
                api(Module.Type._extension_repository)
                api(Module.Type._extension_gateway)
            }
            Module.Type._domain_useCase_core -> {
                api(Module.Type._domain_entity_core)
            }
            // gatewayモジュール
            Module.Type._dataStore_gateway_sdk -> {
            }
            Module.Type._dataStore_gateway_local -> {
                impl(Module.Type._domain_entity_core)
            }
            Module.Type._dataStore_gateway_server -> {
                impl(Module.Type._extension_gateway)
                ModuleExtension.byLayerModuleList(LayerType.entity).forEach {
                    api(it)
                }
            }
            // 依存モジュールを持たないもの
            Module.Type._domain_entity_core,
            Module.Type._extension_view,
            Module.Type._extension_viewModel,
            Module.Type._extension_repository,
            Module.Type._extension_gateway -> {
            }
        }
    }
}