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