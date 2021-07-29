import com.android.build.gradle.internal.dsl.BuildType

object ProjectProperty {

    /**
     * アプリのリリースなどで使うID
     */
    const val APPLICATION_ID = "com.example.app"

    /**
     * このアプリの最低動作保証SDKバージョン
     */
    const val MIN_SDK_VERSION = 23

    /**
     * CIやgradle.propertiesからのビルド環境変数名のタイプ一覧
     */
    internal enum class BuildVariantType {
        ANDROID_KEY_PASSWORD, ANDROID_STORE_PASSWORD, ANDROID_KEYSTORE_FILE_PATH
    }

    /**
     * FlavorType一覧
     */
    internal enum class FlavorType {
        prod, stg, dev
    }

    /**
     * このプロジェクトにおけるBuildType一覧
     * ラムダにはビルドタイプ毎に実行したいコードが書ける
     */
    //FIXME:できればかっこいい名前にしたい
    internal enum class BuildTypeType(val action: (BuildType, FlavorType) -> Unit) {
        debug({ _, _ -> }),
        release({ _, _ -> })
    }

    /**
     * ManifestPlaceHolderに置く値の一覧
     */
    internal enum class ManifestPlaceHolderType(val value: (FlavorType, BuildTypeType) -> String) {
        appName({ flavorType, _ ->
            if (flavorType == FlavorType.prod) {
                ""
            } else {
                flavorType.name
            }.plus("アルサーガテンプレート")
        })
    }

    /**
     * 環境変数一覧
     * プロパティの型と、環境別で値の出し分けロジックを書く
     * ラムダの中の返り値のStringをパースしたものをそのままプロダクト実装コードで使える
     */
    internal enum class BuildConfig(
        val type: IBuildConfigType,
        val value: (FlavorType, BuildTypeType) -> String
    ) {
        IS_DEBUG_LOGGING(BuildConfigType.Boolean, { flavorType, _ ->
            (flavorType != FlavorType.prod).toString()
        }),
        BASE_URL(BuildConfigType.String, { flavorType, _ ->
            if (flavorType == FlavorType.prod) baseUrl(UrlType.API, "")
            else baseUrl(UrlType.API, flavorType.name)
        }),
        ArrayMock(CustomBuildConfigType.StringArray, {_, _ -> "new java.util.ArrayList<>()"})
    }

    /**
     * プリミティブ型の一覧
     */
    internal enum class BuildConfigType : IBuildConfigType {
        Boolean, String
    }

    /**
     * 環境毎に様々な型の値を出せるが、複雑なものは環境毎のパッケージを作ってKotlinで書いた方が保守性が高い
     */
    internal enum class CustomBuildConfigType(val fileFullPath: String) : IBuildConfigType {
        StringArray("java.util.ArrayList<String>")
    }

    /**
     * 型定義のクラスをマーキングするためのインターフェース
     */
    interface IBuildConfigType

    /**
     * URLのパスの向き先を定義する
     * WebViewやCDNやSaaS用の種類が追加される想定
     */
    private enum class UrlType {
        API
    }

    /**
     * APIのパスを生成する関数。
     * 第一引数で用途毎の出し分け、
     * 第二引数で「dev」や「stg」を埋め込む想定。
     */
    private fun baseUrl(urlType: UrlType, prefix: String?): String = when (urlType) {
        UrlType.API -> "https://${prefix ?: ""}.arsaga.jp/v1/api/"
    }.let { "\"$it\"" }
}