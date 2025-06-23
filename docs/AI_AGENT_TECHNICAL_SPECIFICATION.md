# AIエージェント向け Processing花火シミュレーション拡張設計ドキュメント

このドキュメントは、提供された設計ドキュメントを基に、AI開発エージェントがプロジェクトのセットアップ、実装、拡張をよりスムーズに行えるよう、技術的な詳細と具体的な指示を補強したものです。

## 1. はじめに：AIエージェントへのブリーフィング

あなたは、JavaとProcessingを用いた花火シミュレーションを開発するAIエージェントです。このドキュメントに記載された仕様とタスクリストに基づき、プロジェクトの環境構築から実装、機能拡張までを自律的に進めてください。不明点があれば、各セクションの詳細設計を参照してください。

**主なタスク:**
1.  `pom.xml` を使用してMavenプロジェクトをセットアップします。
2.  `Particle.java`, `Firework.java`, `PMainFireworks.java` の3つのクラスを実装します。
3.  ドキュメントに記載された物理モデルとアルゴリズムに従い、リアルな花火の動きを再現します。
4.  指定された拡張タスク（手動打ち上げ、音響効果など）を実装します。

---

## 2. 環境構築と依存関係

プロジェクトの再現性を確保するため、以下の環境設定を`pom.xml`に正確に記述してください。

### 2.1. `pom.xml` の完全な構成

Processingコアライブラリへの依存関係と、Javaのバージョンを指定します。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>fireworks</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Processing 4.x Core Library -->
        <dependency>
            <groupId>org.processing</groupId>
            <artifactId>core</artifactId>
            <version>4.3</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <!-- メインクラスをここに指定 -->
                    <mainClass>fireworks.PMainFireworks</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 3. 詳細設計とアルゴリズム

実装の核となるロジックを以下に示します。これを参考に各クラスを実装してください。

### 3.1. 物理モデル

-   **重力 (Gravity):** すべての`Particle`と、打ち上げ後の`Firework`ロケットには、常に下向きの力がかかります。この力は`draw()`ループごとに`vel`（速度ベクトル）に加算されます。
    -   `PVector gravity = new PVector(0, 0.2);` のような定数を`PMainFireworks`に定義し、各オブジェクトの`update()`メソッドで利用してください。

### 3.2. 花火の生成ロジック

-   **自動打ち上げ:** `PMainFireworks`の`draw()`メソッド内で、フレームごとにランダムな確率で新しい`Firework`を生成します。
    -   例: `if (random(1) < FIREWORK_SPAWN_RATE) { fireworks.add(new Firework(this)); }`

### 3.3. 爆発アルゴリズム (`Firework.explode()`)

-   ロケットが最高点に達した（`vel.y >= 0`）時点で一度だけ呼び出されます。
-   `PARTICLE_COUNT`（例: 100）個の`Particle`を生成します。
-   各パーティクルの初速は、極座標系を用いて放射状に設定します。
    -   `angle = random(TWO_PI);` // 0〜360度のランダムな角度
    -   `speed = random(1, 8);` // ランダムな速さ
    -   `vel.x = cos(angle) * speed;`
    -   `vel.y = sin(angle) * speed;`

### 3.4. 描画とエフェクト

-   **残像効果:** `PMainFireworks`の`draw()`メソッドの冒頭で、半透明の黒い四角形を画面全体に描画します。これにより、前のフレームの描画が薄く残り、光の軌跡が表現されます。
    -   `p.fill(0, 25);` // 最後のアルファ値(25)が残像の濃さを決める
    -   `p.rect(0, 0, width, height);`
-   **フェードアウト:** `Particle.display()`で、パーティクルの色を描画する際に、`lifespan`（寿命）をアルファ値として使用します。
    -   `p.fill(this.c, this.lifespan);`

---

## 4. 主要パラメータ一覧

シミュレーションの挙動を調整しやすくするため、これらの値を`PMainFireworks`クラスの定数として定義してください。

```java
// PMainFireworks.java 内
final float GRAVITY_Y = 0.2f;              // 重力加速度
final float FIREWORK_SPAWN_RATE = 0.03f;     // フレーム毎の花火生成確率
final int PARTICLE_COUNT = 100;            // 爆発時のパーティクル数
final float PARTICLE_LIFESPAN_DECAY = 2.0f; // パーティクルの寿命の減衰率
final int BACKGROUND_ALPHA = 25;           // 残像効果の濃さ (0-255)
```

---

## 5. コード実装のヒント

AIエージェントが実装をスムーズに進めるための、主要メソッドの実装例です。

### `Particle.java`
```java
// Particle.update()
void update() {
    vel.add(p.gravity); // PMainFireworksで定義した重力を適用
    pos.add(vel);
    lifespan -= PARTICLE_LIFESPAN_DECAY;
}
```

### `Firework.java`
```java
// Firework.update()
void update() {
    if (!exploded) {
        vel.add(p.gravity); // ロケットにも重力を適用
        pos.add(vel);
        if (vel.y >= 0) { // 最高点に到達
            explode();
        }
    }
    // 爆発後はパーティクルを更新
    for (int i = particles.size() - 1; i >= 0; i--) {
        particles.get(i).update();
        if (particles.get(i).isDead()) {
            particles.remove(i);
        }
    }
}
```

### `PMainFireworks.java`
```java
// PMainFireworks.draw()
void draw() {
    // 1. 残像エフェクト
    fill(0, BACKGROUND_ALPHA);
    rect(0, 0, width, height);

    // 2. 新しい花火を確率で生成
    if (random(1) < FIREWORK_SPAWN_RATE) {
        fireworks.add(new Firework(this));
    }

    // 3. 全ての花火を更新・描画
    for (int i = fireworks.size() - 1; i >= 0; i--) {
        Firework f = fireworks.get(i);
        f.update();
        f.display();
        if (f.isDone()) {
            fireworks.remove(i);
        }
    }
}
```

---

## 6. 更新されたタスクリスト

元のタスクリストを、より具体的で実行可能なステップに細分化しました。この順序で進めてください。

### フェーズ1: 基本環境とクラス実装
-   [ ] **1-1. プロジェクト設定:**
    -   [ ] `fireworks`ディレクトリとパッケージ構造を作成する。
    -   [ ] 上記「2.1」のコードを`pom.xml`として保存し、Mavenプロジェクトを初期化する。
-   [ ] **1-2. クラス雛形作成:**
    -   [ ] `Particle.java`, `Firework.java`, `PMainFireworks.java`の3つの空ファイルを作成する。
-   [ ] **1-3. `Particle`クラス実装:**
    -   [ ] コンストラクタ、フィールドを定義する。
    -   [ ] `update()`: 重力、速度、位置、寿命の計算を実装する。
    -   [ ] `display()`: `lifespan`をアルファ値として円を描画する実装を行う。
    -   [ ] `isDead()`: `lifespan < 0` でtrueを返す実装を行う。
-   [ ] **1-4. `Firework`クラス実装:**
    -   [ ] コンストラクタで、画面下部からランダムな初速で打ち上げるように初期化する。
    -   [ ] `update()`: 上昇ロジックと、爆発後のパーティクル更新ロジックを実装する。
    -   [ ] `explode()`: 上記「3.3」のアルゴリズムに基づき、`PARTICLE_COUNT`個のパーティクルを生成する。
    -   [ ] `display()`: 爆発前はロケットを、爆発後は全パーティクルを描画する。
    -   [ ] `isDone()`: 爆発済みかつ全パーティクルが消滅したらtrueを返す。
-   [ ] **1-5. `PMainFireworks`クラス実装:**
    -   [ ] `settings()`で`fullScreen()`を設定する。
    -   [ ] `setup()`で`colorMode(HSB)`や`frameRate(60)`を設定する。
    -   [ ] `draw()`: 上記「5」のヒントを参考に、残像効果、花火の自動生成、リスト管理を実装する。
    -   [ ] `static void main()` エントリポイントを記述する。

### フェーズ2: テストとパラメータ調整
-   [ ] **2-1. 動作確認:**
    -   [ ] `mvn compile exec:java` を実行し、花火が意図通りに打ち上がり、爆発することを確認する。
-   [ ] **2-2. パラメータ調整:**
    -   [ ] 「4. 主要パラメータ一覧」で定義した定数を調整し、最も見栄えの良いアニメーションを探求する。特に`GRAVITY_Y`と`BACKGROUND_ALPHA`が重要。

### フェーズ3: 機能拡張
-   [ ] **3-1. 手動打ち上げ:**
    -   [ ] `PMainFireworks`に`mousePressed()`メソッドを追加し、クリックした位置に新しい`Firework`を生成するロジックを実装する（注：この場合、初速は上向きに設定すること）。
-   [ ] **3-2. 音響効果 (発展):**
    -   [ ] ProcessingのSoundライブラリ（`processing.sound.*`）を`pom.xml`に追加する。
    -   [ ] `Firework`のコンストラクタで打ち上げ音を、`explode()`で爆発音を再生する。
-   [ ] **3-3. 爆発形状の多様化 (発展):**
    -   [ ] `Firework.explode()`メソッドを拡張し、引数で形状（"RANDOM", "HEART"など）を切り替えられるようにする。
    -   [ ] ハート型の場合は、数式（カーディオイド曲線など）を使ってパーティクルの初速を計算する。

### フェーズ4: ドキュメント化
-   [ ] **4-1. コードコメント:**
    -   [ ] 各クラス、メソッドにJavadoc形式で説明を追加する。
    -   [ ] 複雑なロジック部分にインラインコメントを追加する。
-   [ ] **4-2. README作成:**
    -   [ ] プロジェクトの概要、実行方法、デモGIFを含む`README.md`ファイルを作成する。