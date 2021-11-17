# 必要な技術
- テキストファイルのbase64エンコードの概念 (テキストファイルを環境変数に文字列として登録する)
- grep (文字列の抜き出し)
- jq (JSONの値抜き出し)
- curl (APIを叩く処理)

# 環境変数一覧

- ENCODED_LOCAL_PROPERTIES => local.propertiesをbase64エンコードしたもの
- FIREBASE_AUTH_TOKEN => Firebase CLIから取得した認証トークン
- GITHUB_PERSONAL_ACCESS_TOKEN => Githubのユーザー名とパーソナルアクセストークンのセット

## ※ 付則
ENCODED_LOCAL_PROPERTIESに登録するlocal.propertiesのsdk.dirはローカルとCI上で値が変わるので環境変数に登録する情報からは削除しておく

環境変数に登録するlocal.propertiesの見本
```
ANDROID_STORE_PASSWORD=(ストアパスワード)
ANDROID_KEY_ALIAS=(キーエイリアス)
ANDROID_KEY_PASSWORD=(キーパスワード)
ENCODED_DEBUG_KEYSTORE=(keystore.jksをbase64エンコードしたもの)
```

GITHUB_PERSONAL_ACCESS_TOKENのフォーマットは`ユーザー名:パーソナルアクセストークン`とする[=>公式ドキュメント](https://docs.github.com/ja/rest/guides/getting-started-with-the-rest-api#authentication)

GITHUB_PERSONAL_ACCESS_TOKENやFIREBASE_AUTH_TOKENに登録するアカウントは社員の退職などに影響されないように、Bot用のアカウント等が望ましい

# 運用方法

## CIコマンドの修正方法
1. config.ymlを直接イジるのではなく、各部品のファイル内のコマンドを修正する
2. CircleCI CLIがインストールされたコマンドラインで `circleci config pack .circleci >| .circleci/config.yml`のコマンドを入力するとconfig.ymlに反映される[=>公式ドキュメント](https://circleci.com/docs/ja/2.0/local-cli/#packing-a-config)
3. 上記の手順を踏まずに部品の内容とconfig.ymlに差異がある場合はビルドが失敗するように設定してあります

## 各環境のFirebaseへのデプロイ方法
- Prod => masterブランチにマージする
- Stg => stagingがマージ元ブランチのプルリクエストを、作成する or 存在中に追加コミットする
- Dev => developがマージ元ブランチのプルリクエストを、作成する or 存在中に追加コミットする

※ 上記で使うブランチはGITHUB_PERSONAL_ACCESS_TOKENに使用したアカウント以外はプッシュできないようにすること

## Firebase配信時のテスターの指定方法
[/.circleci/tester](./tester) ディレクトリの中にある各環境のテキストファイルにFirebaseに登録されているメールアドレスをカンマ区切りで入力

## AppBundleの作成方法
Githubに[数字.数字.数字]形式のタグを作ると、Github上の同名のリリースが作成されそこに配置される


## マージ方法

1. 実装したブランチからデプロイ用のブランチにプルリクエストを出す
2. CircleCI上でApproveする [=>公式ドキュメント](https://circleci.com/docs/ja/2.0/workflows/#holding-a-workflow-for-a-manual-approval)
3. ユニットテストで問題がなければマージされる
4. Firebase App DistributionにAPKがアップロードされる
