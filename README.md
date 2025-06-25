# 日本花火シミュレーション（Processing × Java）

## 概要

日本の伝統的な打ち上げ花火（丸割物・和風色）をProcessing(Java)でリアルに再現するシミュレーションです。
- **和風カラーパレット**・**球状爆発**・**残像エフェクト**を実装
- Maven＋JitPackで依存解決、誰でもcloneして即ビルド＆実行可能
- 設計・進捗・技術仕様もすべてMarkdownでドキュメント化

## 実行方法

### 方法1: 直接実行（推奨）
1. 必要環境: Java 17以上
2. clone後、以下を実行

```bash
# コンパイル
javac -cp "lib/processing-core.jar" -d target/classes src/main/java/fireworks/*.java

# 実行
java -cp "lib/processing-core.jar:target/classes" fireworks.PMainFireworks
```

### 方法2: Maven（要Maven環境）
```bash
mvn compile exec:java
```

- フルスクリーンで日本花火が自動生成されます

## ディレクトリ構成

```
fireworks/
├── pom.xml
├── .gitignore
├── README.md
├── LICENSE
├── docs/         # 設計・仕様・進捗ドキュメント
├── src/main/java/fireworks/
│   ├── Particle.java
│   ├── Firework.java
│   └── PMainFireworks.java
└── ...
```

## 参考ドキュメント
- `docs/hanabi_design_v_1.md`：日本花火デザイン仕様
- `docs/AI_AGENT_TECHNICAL_SPECIFICATION.md`：AI向け技術設計書
- `docs/fireworks_simulation.md`：シミュレーション全体設計
- `docs/PROGRESS_LOG.md`：進捗履歴

## ライセンス
MIT License 

## バージョン履歴

### v1.1（2024-06-08）
- 火花の速度分布を平方根分布に変更（中心密度UP）
- Particleの寿命減衰を指数関数的減衰に変更（より自然な消え方）
- 点滅・光量ブレ（アルファ値やサイズの揺らぎ）を追加
- 残像効果のalpha値を定数化し、調整しやすく

### v1.0
- 丸割物（球状）花火＋和風カラーパレット
- 基本物理モデル（重力・残像）
- Maven/JitPackビルド
- ドキュメント・進捗管理 

## 機能・バージョン履歴

### v1.9 Debug Display Optimization（現在）
- デバッグ情報の半透明化・視認性最適化
- 明るい緑色テキスト・薄い背景で花火表示を邪魔しない設計

### v1.8 Pattern Control
- パターン制御機能（1-4キーで花火形状切替）
- RANDOM/RING/LINE/STAR形状対応

### v1.6 Star-Mine Mode
- スターマインモード（連続クリック→一斉打ち上げ）
- 闇の演出効果付き

## 物理モデル仕様（v1.5以降）

- クリック花火・自動花火ともに「目標高度（Y座標）」で爆発
- 打ち上げ初速vyは「目標高度に理論的に到達する値」を厳密に計算（ランダム誤差なし）
- 縦軸のランダム性は「目標高度の選択」のみ（自動花火はランダム、クリック花火はユーザー指定）
- 今後は風の演算で横軸（vx）に自然なズレを加える予定

### 操作方法
- **クリック**：スターマインモード（連続クリック→0.5秒待機→一斉打ち上げ）
- **スペースキー**：中央下部からランダム花火を1発打ち上げ
- **1-4キー**：花火パターン切替（RANDOM/RING/LINE/STAR）
- **Dキー**：デバッグ情報表示切替
- **Rキー**：全花火クリア

### 設計思想・拡張性
- 物理モデルを厳密化することで、今後「風」「多段爆発」「形状バリエーション」などの拡張が容易
- コード・設計の再現性と保守性を重視 