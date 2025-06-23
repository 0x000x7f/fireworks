### 📅 日付：2024-06-08
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

#### 📝 未完タスク / 次回予定：
- [ ] 実際のパラメータ調整・見た目の最適化
- [ ] 動作確認（色・形状・挙動の最終チェック）

#### 推奨パラメータ例：
- GRAVITY_Y = 0.2f（重力加速度。0.15～0.25で調整可）
- FIREWORK_SPAWN_RATE = 0.03f（花火発生確率。0.01～0.05で調整可）
- PARTICLE_COUNT = 100（爆発時のパーティクル数。50～200で調整可）
- PARTICLE_LIFESPAN_DECAY = 2.0f（寿命減衰。1.5～3.0で調整可）
- BACKGROUND_ALPHA = 25（残像濃度。10～40で調整可）

※見た目やパフォーマンスに応じて、上記範囲で微調整してください。 