package jp.arsaga.domain.service.core

import android.app.Activity

/**
 * Serviceクラス(ロジックを表現するクラス)を共通化して使うための最低限のinterface
 */
interface BaseService<Dependency> {

    val dependency: Dependency

    /**
     * Serviceクラス内での外部依存情報のinterface
     * 各プロパティにDTOをあてがって使用する
     */
    interface Dependency {

        /**
         * 画面遷移させるラムダが並んだDTO interface
         * 基本的な使われ方はcommandのCallback内で引数を受け取ってラムダを実行し、
         * Viewのレイヤーで継承させたものをViewModelのコンストラクタ経由で受け取る
         */
        val navigator: Any?

        /**
         * 外部データを操作するラムダが並んだDTO interface
         * CQRSアーキテクチャパターンの「C」の責務
         * 端末からサーバーやSaaSにデータを送る処理が並ぶ
         */
        val command: Any?

        /**
         * 外からデータを受け取る型が並んだDTO interface
         * CQRSアーキテクチャパターンの「Q」の責務
         * FlowやLiveDataやObservableなどのReactiveプログラミングを実現する型が並ぶ
         */
        val query: Any?
    }
}

/**
 * 画面遷移アクションの基本型
 * Serviceレイヤーでは使わず呼び出し側で使う
 */
typealias ActivityCallback = (Activity) -> Unit