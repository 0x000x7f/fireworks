### 📅 日付：2025-06-25 (AM Session)
#### 🔨 作業内容：
- WSL環境への移行に伴うJava設定問題の解決
- Cursor設定でjava.jdt.ls.java.homeパスをWSL用に修正（/usr/lib/jvm/java-21-openjdk-amd64）
- CLAUDE.mdファイル作成：AI作業ログ自動記録ルール定義
- 花火シミュレーションのデバッグ準備（Maven/Processing依存関係の確認）

#### ✅ 完了タスク：
- [x] CLAUDE.mdファイル作成（AI作業ログ自動記録ルール定義）
- [x] WSL環境でのJavaパス問題特定・解決策提示

#### 📝 未完タスク / 次回予定：
- [ ] Cursor IDE再起動後、Java Language Server動作確認
- [ ] Maven依存関係解決・Processing ライブラリ取得
- [ ] 現在のコードをデバッグ・動作確認する
- [ ] 風（vx）演算の実装
- [ ] 多段爆発・形状バリエーションの拡張
- [ ] v1.5リリースコミット＆push

#### ⚠️ 再起動前メモ：
- WSL環境でJavaパス問題発生中
- Cursor設定で java.jdt.ls.java.home を /usr/lib/jvm/java-21-openjdk-amd64 に設定要
- Maven未完全インストール状態、Processing依存関係未解決
- 現在のセッション終了後、設定変更＆IDE再起動が必要

---

### 📅 日付：2025-06-25 (PM Session)
#### 🔨 作業内容：
- 再起動後の環境確認・Java Language Server動作確認完了
- Processing 3.3.7 core JARダウンロード・依存関係解決
- javacとjavaコマンドでの直接コンパイル・実行環境構築
- 花火シミュレーション動作確認成功（v1.8 Pattern Control）
- デバッグ情報表示の最適化：
  - 背景透明度を50に設定（非常に薄い半透明）
  - テキスト色を明るい緑色（100, 255, 100）に統一
  - 境界線も薄い透明度（100）に調整

#### ✅ 完了タスク：
- [x] Java Language Server動作確認
- [x] Processing依存関係解決（Maven不要のJAR直接使用）
- [x] 花火シミュレーション動作確認・デバッグ実行
- [x] デバッグ情報の半透明化・視認性最適化完了

#### 📝 未完タスク / 次回予定：
- [x] v1.9リリースコミット＆wslブランチ＆タグ付け完了
- [ ] 風（vx）演算の設計書作成
- [ ] 風（vx）演算の実装
- [ ] 多段爆発・形状バリエーションの拡張

---

## v2.0 Wind System Design 開始

### 📋 v2.0の設計方針：
- 風演算システム（横風vx影響）の追加
- **矢印キーによる直感的風制御**（←→=方向、↑↓=強度、T=ON/OFF）
- 高度・パーティクルサイズによる風影響度の差異化（**物理的現実性重視**）
- デバッグ表示への風情報追加
- 美観と物理的現実性の両立

### 📄 設計ドキュメント：
- `docs/wind_system_design_v2.0.md`: 詳細技術設計書作成完了

#### 🔨 実装進捗：
- [x] **Phase 1**: WindSystemクラス作成完了
  - 基本風力計算ロジック実装
  - 高度による風変化（現実的物理モデル）
  - パーティクルサイズによる風抵抗
  - 風の揺らぎ（タービュランス）効果
  - 矢印キー用の制御メソッド（adjustWindDirection/Strength）

- [x] **Phase 2**: 既存システム統合完了
  - Particleクラスに風力適用メソッド追加
  - Fireworkクラスに風システム連携
  - PMainFireworksに風システム初期化・更新ループ統合

- [x] **Phase 3**: UI・制御機能完了
  - 矢印キーによる直感的風制御（←→=方向、↑↓=強度、T=ON/OFF）
  - デバッグ表示に風情報追加
  - v2.0バージョン文字列更新

- [x] **動作テスト**: 全機能正常動作確認
  - 風の影響でパーティクルが横に流れる効果確認
  - リアルタイム風制御の応答性良好
  - 既存機能（スターマイン・パターン切替）との併用可能

- [x] **v2.0.1**: 風速の現実化・物理モデル改良
  - 風速パラメータを現実的レベルに調整（台風→微風）
  - tanh関数による高度係数の緩和
  - 表面積÷質量による物理的根拠のあるサイズ抵抗
  - 制御調整幅の精密化（±0.1, ±0.05刻み）

- [x] **v2.0.2**: 風速表示機能追加
  - 具体的な風速をm/s単位で表示
  - 気象学的分類（静穏/軟風/軽風/微風/弱風/中風/強風）
  - 風向き表示（西風/東風）
  - デバッグ表示の色分け・詳細情報表示
  - **完成度**: プロフェッショナル花火師レベルの情報表示

---

## v1.9 Debug Display Optimization リリース準備

### 📋 v1.9の主な変更点：
- デバッグ情報の半透明化（背景透明度50）
- デバッグテキストを明るい緑色（100, 255, 100）に統一
- 境界線の透明度調整（100）
- 花火表示の視認性向上

#### 🛠️ 技術的メモ：
- Processing 3.3.7 core.jar: lib/processing-core.jar
- コンパイル: `javac -cp "lib/processing-core.jar" -d target/classes src/main/java/fireworks/*.java`
- 実行: `java -cp "lib/processing-core.jar:target/classes" fireworks.PMainFireworks`
- 現在のバージョン: v1.9 Debug Display Optimization（デバッグ表示最適化完了）

---

### 📅 日付：2025-06-24
#### 🔨 作業内容：
- v1.5物理モデル厳密化：クリック花火・自動花火ともに「目標高度（Y座標）」で爆発する物理ロジックに統一
- vyは理論値（ランダム誤差なし）で計算、縦軸ランダム性は目標高度の選択のみ
- クリック花火はユーザー指定高度、自動花火はランダム高度
- 最高点爆発・fuseTimeタイマー方式を廃止し、targetY通過判定のみで爆発
- ドキュメント（README.md, hanabi_design_v_1_5.md, fireworks_simulation.md）を最新仕様に合わせて大幅追記
- 今後の拡張（風による横軸ズレ、多段爆発、形状バリエーション等）を見据えた設計方針を明記

#### ✅ 完了タスク：
- [x] v1.5物理モデル厳密化の実装
- [x] クリック・自動ともに目標高度で爆発するロジックの統一
- [x] vy理論値計算の厳密化（ランダム誤差なし）
- [x] ドキュメントの充実・設計思想の明文化

#### 📝 未完タスク / 次回予定：
- [ ] 風（vx）演算の実装
- [ ] 多段爆発・形状バリエーションの拡張
- [ ] v1.5リリースコミット＆push

### 📅 日付：2024-06-23
#### 🔨 作業内容：
- fireworksディレクトリの現状調査
- pom.xml（Processing依存含むMavenプロジェクト）新規作成
- src/main/java/fireworks ディレクトリ作成
- Particle.java, Firework.java, PMainFireworks.java 雛形作成
- Particleクラスの実装（位置・速度・寿命・色・親PApplet、update/display/isDeadメソッド）
- Fireworkクラスを日本の花火仕様（和風カラーパレット・球状放射・FireworkType導入）に修正
- PMainFireworksクラスの実装（PApplet継承、花火リスト管理、自動生成・描画ループ、主要パラメータ定義、エントリポイント）
- mvn compile exec:java による動作確認（ビルド＆実行テスト）
- パラメータ調整案の検討・推奨値の記載
- v1.1 速度分布の平方根分布化（中心密度UP）をFirework.explode()に実装
- v1.1 Particleの寿命減衰を指数関数的減衰（lifespan *= LIFESPAN_DECAY_RATE）に変更
- v1.1 点滅・光量ブレ（アルファ値やサイズの揺らぎ）をParticle.display()に実装
- v1.1 残像効果のパラメータ化（定数化して調整しやすく）をPMainFireworksに実装
- v1.1動作確認・全仕様の実装完了
- v1.2 マウスによる手動打ち上げ機能（mousePressed）をPMainFireworksに実装
- v1.2 キーボードショートカット（リセット・追加打ち上げ）をPMainFireworksに実装

#### ✅ 完了タスク：
- [x] fireworksディレクトリの初期状態確認
- [x] pom.xml新規作成
- [x] src/main/java/fireworks ディレクトリ作成
- [x] Particle.java 雛形作成
- [x] Firework.java 雛形作成
- [x] PMainFireworks.java 雛形作成
- [x] Particleクラスの実装
- [x] Fireworkクラスの日本花火仕様対応
- [x] PMainFireworksクラスの実装
- [x] 動作確認・テスト（ビルド＆実行）
- [x] パラメータ調整案・推奨値の記載
- [x] 爆発時の火花の初速分布をsqrt(random(1))で中心密度UP
- [x] Particleの寿命減衰を指数関数的減衰に変更
- [x] Particle.display()でアルファ値・サイズにランダムな揺らぎを追加
- [x] 残像効果のalpha値をfinal定数BACKGROUND_ALPHAとして定義し、draw()で使用
- [x] v1.1全仕様の動作確認・リリース
- [x] mousePressed()でクリック位置に向かって花火を打ち上げる機能を追加
- [x] keyPressed()でr/Rで全花火クリア、スペースキーで中央下部からランダム花火を1発打ち上げる機能を追加

#### 📝 未完タスク / 次回予定：
- [ ] README.mdにバージョン履歴（v1.1の変更点）を追記
- [ ] v1.1リリースコミット＆push
- [ ] 実際のパラメータ調整・見た目の最適化
- [ ] 動作確認（色・形状・挙動の最終チェック）
- [ ] v1.2動作確認・README/ログ更新

#### 推奨パラメータ例：
- GRAVITY_Y = 0.2f（重力加速度。0.15～0.25で調整可）
- FIREWORK_SPAWN_RATE = 0.03f（花火発生確率。0.01～0.05で調整可）
- PARTICLE_COUNT = 100（爆発時のパーティクル数。50～200で調整可）
- PARTICLE_LIFESPAN_DECAY = 2.0f（寿命減衰。1.5～3.0で調整可）
- BACKGROUND_ALPHA = 25（残像濃度。10～40で調整可）

※見た目やパフォーマンスに応じて、上記範囲で微調整してください。

---

## v1.6 Star-Mine Mode（スターマイン・モード）

- クリック連打で位置を溜め、0.5秒間クリックが止まったら全ての位置に一斉に花火を打ち上げる「スターマイン・モード」を実装。
- 一斉打ち上げ前に「闇の時間（1秒）」を設け、劇的な静寂とクライマックスを演出。
- 全花火が同時に爆発するよう導火線タイミングを調整し、±50msのランダム性で自然なスターマインを実現。
- ドキュメント（hanabi_design_v_1_5.md）に仕様を追記。
- 動作テスト済み。 