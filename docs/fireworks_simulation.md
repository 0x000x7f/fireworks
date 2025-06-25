# Processingによる花火シミュレーション設計ドキュメント

このドキュメントは、Processing（Javaベース）で構築された花火シミュレーションの構成、目的、各コンポーネントの動作、実行方法について日本語で整理したものです。Claude や Cursor などのエージェントが理解・拡張しやすいように構造的に記述されています。

---

## 📁 プロジェクト構成（fireworks ディレクトリ）

この花火シミュレーションは、`JavaDev/fireworks/` ディレクトリに専用構成として分離されています。Mavenプロジェクトとして独立して実行・拡張可能です。

```
JavaDev/
└── fireworks/
    ├── pom.xml
    └── src/
        └── main/
            └── java/
                └── fireworks/
                    ├── Particle.java
                    ├── Firework.java
                    └── PMainFireworks.java
```

パッケージ名も `fireworks` に統一されており、他の演習とは分離された自律構造となっています。

---

## 🎯 概要（目的）

- 全画面の夜空において、ロケットが下から打ち上がり、上空で爆発。
- 爆発により火花（パーティクル）が放射状に飛び、徐々にフェードアウト。
- 完全自動制御。一定確率で新たな花火が打ち上がる。

---

## 🧱 クラス構成

### 1. Particle.java（火花）

```java
class Particle {
    PVector pos, vel;
    float lifespan = 255;
    int c;
    PApplet p;

    Particle(PApplet p, PVector pos, PVector vel, int c)
    void update()  // 移動と寿命減衰
    void display() // 描画（円）
    boolean isDead() // 寿命切れ判定
}
```

### 2. Firework.java（1発の花火）

```java
class Firework {
    PVector pos, vel;
    boolean exploded = false;
    List<Particle> particles;
    int c;

    Firework(PApplet p)        // 底辺にランダム生成
    void update()              // 上昇→爆発→パーティクル更新
    void explode()             // パーティクルを放出
    void display()             // 点描 or パーティクル描画
    boolean isDone()           // 全パーティクル消滅済みか
}
```

### 3. PMainFireworks.java（メイン）

```java
public class PMainFireworks extends PApplet {
    List<Firework> fireworks;

    void settings()    // fullScreen() or size(800,600)
    void setup()       // frameRate, 背景初期化
    void draw()        // 花火の自動生成と描画
    static void main(String[] args) // エントリーポイント
}
```

---

## ⚙️ デザイン目標

- モジュール化：粒子と花火の責務を明確分離
- ランダム性：位置・色・爆発タイミングが全てランダム
- 視覚美：残像エフェクト、広がる放射表現、滑らかなフェード
- 安定性：同時に複数発が表示されても高FPSを維持

---

## 🚀 実行手順

1. `fireworks` パッケージ内に3ファイル配置：
   - `Particle.java`
   - `Firework.java`
   - `PMainFireworks.java`

2. `pom.xml` に以下を設定：

```xml
<mainClass>fireworks.PMainFireworks</mainClass>
```

3. 実行：

```bash
mvn compile exec:java
```

---

## 🔍 補足仕様

- 爆発条件：ロケットの `vel.y >= 0` で `explode()` 発動
- 火花数：100個（方向と速さは極座標でランダム）
- 残像表現：`fill(0, 0, 0, 25)` による透過黒塗り
- パーティクル描画：`fill(color, lifespan)` でフェード

---

## 💡 拡張アイデア（実装状況）

- ✅ `mousePressed()` で手動打ち上げ対応（スターマインモード）
- ✅ 風システム実装（v2.0で完了）
- ✅ UI追加（デバッグ表示・風制御）
- ✅ パターン制御（1-4キーでRANDOM/RING/LINE/STAR）
- 🔄 音響追加（WAVファイルとの連携）
- 🔄 爆発の形をハート型や文字に（座標制御）
- 🔄 多段爆発・連鎖花火システム

---

## 📝 実装・拡張タスクリスト

### 1. プロジェクト初期化・構成
- [ ] `fireworks` ディレクトリ・パッケージの作成
- [ ] `pom.xml` の作成と Processing 依存追加
- [ ] `Particle.java`・`Firework.java`・`PMainFireworks.java` の雛形作成

### 2. 基本クラス実装
- [ ] `Particle` クラスの実装（位置・速度・寿命・色・update/display/isDead）
- [ ] `Firework` クラスの実装（上昇・爆発・パーティクル管理・display/isDone）
- [ ] `PMainFireworks` の実装（花火リスト・自動生成・描画ループ）

### 3. 花火シミュレーションの基本動作
- [ ] ロケットの自動打ち上げ（ランダム位置・色・タイミング）
- [ ] 爆発時のパーティクル放射（100個・極座標ランダム）
- [ ] パーティクルのフェードアウト・消滅管理
- [ ] 残像エフェクト（夜空の残像）

### 4. 安定性・美観の調整
- [ ] FPS維持・同時多発時のパフォーマンス確認
- [ ] 色・速度・寿命パラメータの調整
- [ ] 爆発の広がり・放射角度の調整

### 5. 拡張・発展タスク
- [ ] `mousePressed()` で手動打ち上げ対応
- [ ] 爆発形状の多様化（ハート型・文字・星型など）
- [ ] 音響追加（WAVファイル連携）
- [ ] UI追加（打ち上げ間隔・色パレット調整など）
- [ ] 設定ファイルやコマンドライン引数によるカスタマイズ

### 6. ドキュメント・デバッグ
- [ ] コードコメント・Javadocの充実
- [ ] 動作例のGIFやスクリーンショット作成
- [ ] 既知の課題・今後のアイデア記載

---

## v2.0 Wind System実装完了（ubuntu-portブランチ）

### 風物理システムの特徴
- **現実的風速**: 気象学的分類に基づく風速表示（静穏〜強風）
- **高度効果**: tanh関数による自然な高度風変化
- **サイズ抵抗**: 表面積/質量比による物理的根拠ある風抵抗
- **直感制御**: 矢印キーによる風向き・強度のリアルタイム制御
- **プロ表示**: 具体的風速（m/s）・風向き・気象分類の詳細表示

### v1.5 物理モデル厳密化のポイント

- クリック花火・自動花火ともに「目標高度（Y座標）」で爆発
- vyは目標高度に理論的に到達する値を厳密に計算（ランダム誤差なし）
- 縦軸のランダム性は「目標高度の選択」のみ
- **完了**: 風の演算で横軸（vx）にズレを加える機能実装済み

### 拡張性
- 物理モデルの厳密化により風システムの実装が完了
- WindSystemクラスにより今後の多段爆発・形状バリエーション拡張が容易
- コードの再現性・保守性が大幅向上

---

