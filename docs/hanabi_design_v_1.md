# 日本の花火シミュレーション（v1設計）

このドキュメントは、日本の伝統的な花火を模したシミュレーションをProcessing上で構築するための v1 設計方針を記述したものです。

## 🎯 目的

- 現実の日本花火（丸割物・紅・金など）を模したビジュアルを再現
- 初期バージョンでは最低限のランダム色と放射状の形状（丸割物）を実装

---

## 🔶 FireworkType（花火の形状タイプ）

```java
enum FireworkType {
    MARU  // 日本の伝統的な球状の丸割物花火
}
```

- v1では `MARU` のみ使用（将来的に CHIRIN, KIKU, KOWARI など追加可）

---

## 🎨 カラーパレット（和風花火色）

日本でよく見られる伝統色をベースに、ランダム選択で使用：

```java
color[] palette = {
    color(255, 215, 0),    // 金
    color(220, 20, 60),    // 紅
    color(72, 61, 139),    // 青藍
    color(0, 255, 127),    // 緑青
    color(138, 43, 226)    // 紫
};
```

---

## 🔧 Firework クラス修正案（v1）

```java
class Firework {
    FireworkType type;
    int c;

    Firework(PApplet p) {
        this.c = palette[(int)p.random(palette.length)];
        this.type = FireworkType.MARU;
    }

    void explode() {
        if (type == FireworkType.MARU) {
            for (int i = 0; i < 100; i++) {
                float angle = p.random(TWO_PI);
                float speed = p.random(1, 5);
                PVector dir = PVector.fromAngle(angle).mult(speed);
                particles.add(new Particle(p, pos, dir, c));
            }
        }
    }
}
```

---

## ✅ 実装目標（v1完了条件）

- [x] `FireworkType.MARU` の形状（球状）で爆発する
- [x] `palette[]` から色をランダムに決定
- [ ] 画面上で複数の花火がそれぞれ違う色で放射される

---

## 🚀 実行手順（テスト）

1. `Particle`・`Firework`・`PMainFireworks` クラスに上記内容を組み込む
2. `mainClass` を `fireworks.PMainFireworks` に設定した `pom.xml` で

```bash
mvn compile exec:java
```

3. 実行して以下を確認：
    - 放射状に広がる火花（円形）
    - 花火ごとにランダムな日本らしい色
    - フルスクリーン表示＋残像処理

---

## 💬 備考

- v1は最小限の「見た目」から始め、今後 菊型・尾を引く軌跡・再爆発 などへ発展可能
- 色名・形状は日本伝統花火に即して設計

