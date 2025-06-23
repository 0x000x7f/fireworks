# 【技術設計書】v1.5: ハイライト演出機能・物理モデル厳密化

**ドキュメントバージョン:** 1.0  
**対象プロジェクト:** 日本花火シミュレーション  
**担当AIエージェント:** ChatGPT × Gemini 統合設計  
**ステータス:** 設計完了 ・ 実装待ち

---

## 1. 概要と目的

本機能は、ユーザーのインタラクションをトリガーとして、 **「静忍」 (Focus Mode)** と **「特別な一発」 (Highlight Firework)** を組み合わせたエンターテインメント演出**を実現するための技術仕様を定義する。

この演出は、観客の視線を集中させ、背景の静けさと模様な爆発を対置することで、 感情的なカタルシスを作り出すことを犯猟とする。

---

## 2. 演出シーケンス

1. ユーザーがクリックする
2. 特別な「ハイライト花火」を打ち上げ
3. フォーカスモード開始：自動花火が停止、静寂な夜空を表現
4. ハイライト花火が爆発：大きさ、明るさ、長存在
5. フォーカスモード解除：指定時間後に自動花火再開

---

## 3. 技術仕様 (実装指示)

### 3.1. `PMainFireworks` の状態管理

```java
private boolean inFocusMode = false;
private int focusStartMillis = 0;
private static final int FOCUS_DURATION = 3000;
```

### 3.2. `mousePressed()` でのトリガー

```java
public void mousePressed() {
    fireworks.add(new Firework(this, true));
    inFocusMode = true;
    focusStartMillis = millis();
}
```

### 3.3. `draw()` での自動打ち上げの制御

```java
if (inFocusMode && (millis() - focusStartMillis > FOCUS_DURATION)) {
    inFocusMode = false;
}
if (!inFocusMode && random(1) < FIREWORK_SPAWN_RATE) {
    fireworks.add(new Firework(this, false));
}
```

### 3.4. `Firework` クラス

```java
private final boolean isHighlight;

public Firework(PApplet p, boolean isHighlight) {
    this.p = p;
    this.isHighlight = isHighlight;
    this.vel = isHighlight ? new PVector(p.random(-1, 1), p.random(-22, -18)) : new PVector(0, p.random(-17, -12));
}

private void explode() {
    int particleCount = isHighlight ? 250 : 100;
    float maxSpeed = isHighlight ? 12.0f : 8.0f;
    for (int i = 0; i < particleCount; i++) {
        float speed = maxSpeed * sqrt(p.random(1));
        // Particle p = new Particle(..., isHighlight)
    }
    this.exploded = true;
}
```

### 3.5. `Particle` クラス

```java
private final boolean isHighlight;
private float size;

Particle(..., boolean isHighlight) {
    this.isHighlight = isHighlight;
    this.lifespan = isHighlight ? 350 : 255;
    this.size = isHighlight ? 3.0f : 2.0f;
}

void display() {
    if (isHighlight) {
        fill(this.c, lifespan * 0.3f);
        ellipse(pos.x, pos.y, size * 2.5f, size * 2.5f);
    }
    fill(this.c, lifespan);
    ellipse(pos.x, pos.y, size, size);
}
```

---

## 4. タスク分解

| ID | タスク | 状態 |
|----|--------|--------|
| T1 | `PMainFireworks` に状態変数を追加 | 完了 |
| T2 | `mousePressed()` にトリガー実装 | 完了 |
| T3 | `draw()` に自動打ち上げ停止ロジック | 完了 |
| T4 | `Firework` に `isHighlight` 拡張 | 完了 |
| T5 | `Particle` に `isHighlight` 拡張 | 完了 |
| T6 | `docs/hanabi_design_v_1_5.md` に記述 | 完了 |

---

## 5. 開発の方針

- 設計は正式ドキュメント `hanabi_design_v_1_5.md` として保存
- バージョンログは `PROGRESS_LOG.md` に追記
- v1.5 は 「静けさと主役」の対置を用いた高度な演出モード

