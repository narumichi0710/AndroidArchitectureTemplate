# アーキテクチャ構成図

![Diagram](document/ArchitectureDiagram.png)

## 管理方法
- 新しいライブラリが出るとDependabotがプルリクエストを作ってくれる
- 社内Slackチャンネルの[android_template_notification]に通知が行くのでマージ処理を行う

## 使い方
1. Github上でこのレポジトリを複製する
2. プロジェクト設定の値を修正する => (https://github.com/arsaga-partners/AndroidArchitectureTemplate/blob/develop/buildSrc/src/main/kotlin/ProjectProperty.kt)
3. Gitサブモジュールを有効化する

## ドメイン一覧
- core => ドメイン間で共通で使うコードを書く
- auth => 認証に関するロジックを書く

## 関連ドキュメントへのリンク一覧

