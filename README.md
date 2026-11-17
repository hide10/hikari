# 光 (hikari)

画面全体を、選んだ明るさで光らせるだけのアプリ。

- 明るさはスライダーで調整し、画面タップで操作パネルを表示/非表示する
- 明るさはウィンドウ属性 (screenBrightness) で適用し、端末のシステム設定は変更しない
- 最後に使った明るさを次回起動時に復元する
- 通信なし、権限なし

## 開発

- Kotlin + Jetpack Compose、単一モジュール・単一 Activity
- パッケージ: com.hide10.hikari / minSdk 26 / targetSdk 36
