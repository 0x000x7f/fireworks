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

---

# v1.6 スターマイン・モード（Star-Mine Mode）

## 概要

ユーザーが短時間に連続してクリックした位置情報をすべて溜め込み、クリックが止んだ瞬間に、溜め込んだ全ての位置に向けて花火を一斉に打ち上げる演出モード。さらに、打ち上げ前に「闇の時間（1秒）」を設けることで、劇的な静寂とクライマックスを演出する。

## 演出シーケンス

1. **溜め（Charge）:** ユーザーが短時間に連続してクリックした位置情報をすべてキュー（リスト）に溜め込む。
2. **発射待機:** 最後のクリックから0.5秒経過したら、画面上の花火が全て消えるのを待つ。
3. **闇（静寂）:** 全ての花火が消滅した瞬間から1秒間、画面に何も表示されない「闇の時間」を設ける。
4. **一斉打ち上げ:** 闇の時間が終わったら、溜め込んだ全ての位置に向けて花火を一斉に打ち上げる。
5. **同時爆発:** 打ち上げられた花火群が、異なる高度にも関わらず、ほぼ同時に爆発するようタイミングを調整する。

## 技術仕様

- `pendingClicks`リストでクリック位置を溜める。
- `lastClickTime`で最後のクリック時刻を記録。
- `LAUNCH_DELAY_MS`（0.5秒）で「クリックが止んだ」と判定。
- `starMinePending`と`darkStartMillis`で「闇の時間」状態を管理。
- `DARK_DURATION_MS`（1秒）で闇の時間を制御。
- `calculateFuseTime()`で全花火が同時に爆発するよう導火線タイミングを調整。
- 一斉打ち上げ後は`pendingClicks`をクリアし、通常モードに復帰。

## 実装ポイント

- クリック連打→0.5秒経過→全花火消滅→1秒闇→一斉打ち上げ、という厳密な時系列制御。
- 闇の時間中は自動打ち上げも停止。
- 一斉打ち上げ時、各花火の爆発タイミングに±50msのランダム性を加え、自然なスターマイン演出を実現。

