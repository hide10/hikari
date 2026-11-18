# 光

画面全体を白く光らせるだけの Android アプリ。

- 下のカードのスイッチで点灯と消灯を切り替え、スライダーで明るさを決める。
- 明るさはこのアプリの画面にだけ効く。端末の明るさ設定は変えない。
- 最後に選んだ明るさを次の起動でも使う。起動時は常に点灯する。
- 表示中は画面が消えない。
- インターネット権限を持たない。

## ビルド

JDK 17 と Android SDK（compileSdk 36）を使う。`local.properties` に `sdk.dir` を書いてから実行する。

```sh
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./gradlew testDebugUnitTest assembleDebug
```

APK は `app/build/outputs/apk/debug/app-debug.apk` にできる。
