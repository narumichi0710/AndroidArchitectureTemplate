# 必要な技術
- テキストファイルのbase64エンコードの概念 (テキストファイルを環境変数に文字列として登録する)
- grep (文字列の抜き出し)
- jq (JSONの値抜き出し)
- curl (APIを叩く処理)

# 環境変数一覧

- ENCODED_DEBUG_KEYSTORE => keystore.jksをbase64エンコードしたもの
- ENCODED_LOCAL_PROPERTIES => local.propertiesをbase64エンコードしたもの
- FIREBASE_AUTH_TOKEN => Firebase CLIから取得した認証トークン
- GITHUB_PERSONAL_ACCESS_TOKEN => Githubのユーザー名とパーソナルアクセストークンのセット
- SLACK_RELATION_MAILADRESS => Slackに自動転送

## ※ 付則
ENCODED_LOCAL_PROPERTIESに登録するlocal.propertiesのsdk.dirはローカルとCI上で値が変わるので環境変数に登録する情報からは削除しておく

環境変数に登録するlocal.propertiesの見本
```
ANDROID_STORE_PASSWORD=(ストアパスワード)
ANDROID_KEY_ALIAS=(キーエイリアス)
ANDROID_KEY_PASSWORD=(キーパスワード)
```

GITHUB_PERSONAL_ACCESS_TOKENのフォーマットは`ユーザー名:パーソナルアクセストークン`とする[=>公式ドキュメント](https://docs.github.com/ja/rest/guides/getting-started-with-the-rest-api#authentication)

GITHUB_PERSONAL_ACCESS_TOKENに登録するアカウントは社員の退職などに影響されないように、Bot用のアカウント等が望ましい

# 運用方法

## CIコマンドの修正方法
1. config.ymlを直接イジるのではなく、各部品のファイル内のコマンドを修正する
2. CircleCI CLIがインストールされたコマンドラインで `circleci config pack .circleci >| .circleci/config.yml`のコマンドを入力するとconfig.ymlに反映される[=>公式ドキュメント](https://circleci.com/docs/ja/2.0/local-cli/#packing-a-config)
3. 上記の手順を踏まずに部品の内容とconfig.ymlに差異がある場合はビルドが失敗するように設定してあります

## 各環境のデプロイ方法
- Prod => masterブランチにマージする
- Stg => stagingブランチにマージする
- Dev => developブランチにマージする

※ 上記で使うブランチはGITHUB_PERSONAL_ACCESS_TOKENに使用したアカウント以外はプッシュできないようにすること

## マージ方法

1. 実装したブランチからデプロイ用のブランチにプルリクエストを出す
2. CircleCI上でApproveする [=>公式ドキュメント](https://circleci.com/docs/ja/2.0/workflows/#holding-a-workflow-for-a-manual-approval)
3. ユニットテストで問題がなければマージされる
4. Firebase App DistributionにAPKがアップロードされる
