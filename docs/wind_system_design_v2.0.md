# 【技術設計書】v2.0: 風演算システム実装

**ドキュメントバージョン:** 1.0  
**対象プロジェクト:** 日本花火シミュレーション  
**担当AIエージェント:** Claude AI  
**ステータス:** 設計中

---

## 1. 概要と目的

本機能は、花火シミュレーションに**風の影響**を追加し、より自然で動的な花火表現を実現することを目的とする。

### 1.1 実装目標
- 花火の軌道に横風（vx方向）の影響を加える
- 風速・風向きのリアルタイム制御
- パーティクルサイズによる風の影響度の差異化
- デバッグ情報での風の状態表示

---

## 2. 物理モデル設計

### 2.1 風力計算式

```java
// 基本風力計算
PVector windForce = new PVector(windVelocityX * windStrength, 0);

// パーティクルサイズによる影響度調整
float particleWindResistance = map(particleSize, 1, 10, 0.3f, 1.0f);
windForce.mult(particleWindResistance);

// パーティクルの速度に加算
velocity.add(windForce);
```

### 2.2 風パラメータ

| パラメータ | 型 | 範囲 | 説明 |
|------------|----|----- |------|
| `windVelocityX` | float | -5.0 ~ 5.0 | 風の横方向速度（負=左風、正=右風） |
| `windStrength` | float | 0.0 ~ 1.0 | 風の強度（0=無風、1=最大） |
| `windTurbulence` | float | 0.0 ~ 0.5 | 風の揺らぎ（ランダム性） |
| `altitudeWindFactor` | float | 0.5 ~ 2.0 | 高度による風強度変化 |

### 2.3 高度による風の変化

```java
// 高度が高いほど風が強くなる設定
float altitudeFactor = map(particle.pos.y, height, 0, 0.5f, 2.0f);
windForce.mult(altitudeFactor);
```

---

## 3. システム設計

### 3.1 WindSystemクラス設計

```java
public class WindSystem {
    // 風パラメータ
    private float windVelocityX;
    private float windStrength;
    private float windTurbulence;
    private float altitudeWindFactor;
    
    // 風の時間変化
    private float windTime;
    private boolean isWindEnabled;
    
    // コンストラクタ
    public WindSystem()
    
    // メソッド
    public void update(float deltaTime)
    public PVector getWindForce(PVector position, float particleSize)
    public void setWindVelocity(float vx)
    public void setWindStrength(float strength)
    public void toggleWind()
    
    // デバッグ用
    public String getWindStatus()
}
```

### 3.2 既存クラスへの統合

#### 3.2.1 PMainFireworksクラス
```java
// 新規フィールド
private WindSystem windSystem;

// setup()に追加
windSystem = new WindSystem();

// keyPressed()に風制御追加（矢印キー対応）
if (keyCode == LEFT) {
    windSystem.adjustWindDirection(-0.2f); // 左風
} else if (keyCode == RIGHT) {
    windSystem.adjustWindDirection(0.2f);  // 右風
} else if (keyCode == UP) {
    windSystem.adjustWindStrength(0.1f);   // 風強度UP
} else if (keyCode == DOWN) {
    windSystem.adjustWindStrength(-0.1f);  // 風強度DOWN
} else if (key == 't' || key == 'T') {
    windSystem.toggleWind();               // 風ON/OFF
}
```

#### 3.2.2 Particleクラス
```java
// update()メソッドに風力追加
public void update(WindSystem windSystem) {
    // 既存の重力計算
    vel.add(gravity);
    
    // 風力計算・適用
    if (windSystem.isWindEnabled()) {
        PVector windForce = windSystem.getWindForce(pos, size);
        vel.add(windForce);
    }
    
    // 位置更新
    pos.add(vel);
    // 寿命減衰
    lifespan -= lifespanDecay;
}
```

---

## 4. ユーザーインターフェース

### 4.1 キーボード制御（矢印キー採用）

| キー | 機能 | 詳細 |
|------|------|------|
| `←` | 左風増加 | windVelocityX -= 0.2 |
| `→` | 右風増加 | windVelocityX += 0.2 |
| `↑` | 風強度増加 | windStrength += 0.1 |
| `↓` | 風強度減少 | windStrength -= 0.1 |
| `T` | 風ON/OFF | toggleWind() |

**直感的な操作性:**
- 矢印の方向 = 風の方向（←左風、→右風）
- 矢印の上下 = 風の強弱（↑強く、↓弱く）
- ゲーム感覚での操作が可能

### 4.2 デバッグ表示追加

```java
// displayDebugInfo()に追加
text("Wind Status: " + windSystem.getWindStatus(), debugX + 10, debugY + 270);
text("Wind Controls: ←→=Direction ↑↓=Strength T=Toggle", debugX + 10, debugY + 290);
text("Wind: " + (windEnabled ? "ON" : "OFF") + " | Dir: " + windVelocityX + " | Str: " + windStrength, debugX + 10, debugY + 310);
```

### 4.3 風向き視覚表示（将来拡張）
- デバッグ画面に風向き矢印アイコン表示
- 風強度に応じた矢印の太さ・色変化
- 風の効果範囲の可視化

---

## 5. 実装フェーズ

### Phase 1: 基本風システム
1. WindSystemクラス作成
2. 基本的な風力計算実装
3. キーボード制御実装

### Phase 2: 物理モデル精緻化
1. 高度による風変化実装
2. パーティクルサイズによる影響度実装
3. 風の揺らぎ（タービュランス）実装

### Phase 3: UI・デバッグ強化
1. デバッグ表示に風情報追加
2. 風の視覚的表現（矢印等）追加
3. パラメータ調整・最適化

### Phase 4: 高度機能
1. 時間による風変化（風向き・強度の自動変化）
2. 風のプリセット機能
3. 風の影響を受けない特殊花火

---

## 6. テスト計画

### 6.1 単体テスト
- WindSystemクラスの各メソッド動作確認
- 風力計算の正確性検証
- パラメータ範囲チェック

### 6.2 統合テスト
- 既存花火システムとの統合確認
- パフォーマンス影響測定
- デバッグ表示の動作確認

### 6.3 ユーザビリティテスト
- キーボード制御の直感性
- 風の視覚的効果の自然さ
- デバッグ情報の見やすさ

---

## 7. 注意事項・制約

### 7.1 パフォーマンス考慮
- 風力計算は軽量に保つ（フレームレート60fps維持）
- 大量パーティクル時の処理負荷対策

### 7.2 物理的現実性
- 風の影響は適度に抑制（花火の美しさを損なわない）
- 極端な風速設定の制限

### 7.3 後方互換性
- 既存の花火動作への影響を最小限に
- 風OFF状態では従来通りの動作を保証

---

## 8. 拡張可能性

### 8.1 将来的な機能拡張
- 風のパーティクル表現（煙や霧）
- 3D風ベクトル（Y方向の風）
- 季節や天候による風パターン
- 音響効果（風の音）

### 8.2 他システムとの連携
- 多段爆発システムとの組み合わせ
- 形状バリエーションへの風影響
- AIによる風パターン学習

---

## 9. 実装予定スケジュール

- **Week 1**: WindSystemクラス設計・実装
- **Week 2**: 既存システム統合・基本テスト
- **Week 3**: UI実装・デバッグ機能追加
- **Week 4**: パフォーマンス最適化・最終テスト

---

**設計書作成日**: 2025-06-25  
**次回レビュー予定**: 実装開始前