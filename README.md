# 日本花火シミュレーション（Processing × Java）

## 概要

日本の伝統的な打ち上げ花火（丸割物・和風色）をProcessing(Java)でリアルに再現するシミュレーションです。
- **和風カラーパレット**・**球状爆発**・**残像エフェクト**を実装
- Maven＋JitPackで依存解決、誰でもcloneして即ビルド＆実行可能
- 設計・進捗・技術仕様もすべてMarkdownでドキュメント化

## 実行方法

1. 必要環境: Java 17以上, Maven
2. clone後、以下を実行

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