package fireworks;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class Firework {
    enum FireworkType {
        MARU // 日本の伝統的な球状花火
    }

    enum FireworkPattern {
        RANDOM,         // 1: 従来のランダム拡散（球状・丸割物）
        RING,          // 2: 環状配置（正円）
        LINE,          // 3: 直線状配置
        STAR,          // 4: 星型配置（五芒星）
        CHRYSANTHEMUM, // 5: 菊（長い放射状、日本伝統）
        WILLOW,        // 6: 柳（垂れ下がり効果）
        PALM,          // 7: 椰子（上向き弧状）
        MARUWARI_RED,  // 8: 赤色の丸割物（基本の球体）
        BOTAN,         // 9: 牡丹（太く明るい光）
        SENRIN_GIKU    // 0: 千輪菊（多数の小花が一斉に開く）
    }

    private static final int[][] PALETTE = {
        {255, 215, 0},    // 金
        {220, 20, 60},    // 紅
        {72, 61, 139},    // 青藍
        {0, 255, 127},    // 緑青
        {138, 43, 226}    // 紫
    };

    PVector pos, vel;
    boolean exploded = false;
    List<Particle> particles = new ArrayList<>();
    int c;
    PApplet p;
    FireworkType type;
    FireworkPattern pattern;
    Float targetY = null; // 目標爆発高度（nullなら従来通り最高点で爆発）
    float prevY; // 前フレームのy座標
    boolean fromClick = false;
    int fuseTime = 0;
    boolean isHighlight = false;
    // --- v1.6 Star-Mine Mode Fields ---
    private Float fuseTimeMillis = null; // 指定爆発時間（ミリ秒）
    private int launchTime = 0;         // 打ち上げ時刻

    public Firework(PApplet p, float width) {
        this.p = p;
        float x = p.random(width);
        float targetY = p.random(p.height * 0.2f, p.height * 0.7f); // ランダムな目標高度
        this.pos = new PVector(x, p.height);
        this.c = getRandomColor(p);
        this.type = FireworkType.MARU;
        this.pattern = FireworkPattern.RANDOM;
        this.targetY = targetY;
        this.prevY = this.pos.y;
        float g = 0.12f; // v2.1: PMainFireworks.GRAVITY_Yと統一
        float dy = p.height - targetY;
        float vy = -(float)Math.sqrt(2 * g * dy); // ランダム誤差なし
        this.vel = new PVector(0, vy);
    }

    // targetY指定用コンストラクタ
    public Firework(PApplet p, float x, float targetY, float vy, int c) {
        this.p = p;
        this.pos = new PVector(x, p.height);
        float vx = p.random(-0.5f, 0.5f);
        this.vel = new PVector(vx, vy);
        this.c = c;
        this.type = FireworkType.MARU;
        this.pattern = FireworkPattern.RANDOM;
        this.targetY = targetY;
        this.prevY = this.pos.y;
    }

    // クリック打ち上げ・ハイライト用コンストラクタ
    public Firework(PApplet p, float x, float targetY, boolean isHighlight) {
        this.p = p;
        this.pos = new PVector(x, p.height);
        this.c = getRandomColor(p);
        this.type = FireworkType.MARU;
        this.pattern = FireworkPattern.RANDOM;
        this.isHighlight = isHighlight;
        float g = 0.12f; // v2.1: PMainFireworks.GRAVITY_Yと統一
        float dy = p.height - targetY;
        float vy = -(float)Math.sqrt(2 * g * dy); // ランダム誤差なし
        this.vel = new PVector(0, vy);
        this.targetY = targetY;
        this.prevY = this.pos.y;
    }

    /**
     * ★ v1.6 スターマイン用コンストラクタ
     * @param fuseTime この時間（ミリ秒）が経過したら爆発する
     */
    public Firework(PApplet p, float x, float targetY, boolean isHighlight, float fuseTime) {
        this(p, x, targetY, isHighlight); // 既存のコンストラクタを呼び出す
        this.fuseTimeMillis = fuseTime + p.random(-50, 50); // ±50msのランダム性
        this.launchTime = p.millis();
    }

    private int getRandomColor(PApplet p) {
        int[] rgb = PALETTE[(int)p.random(PALETTE.length)];
        return p.color(rgb[0], rgb[1], rgb[2]);
    }

    public void update(float gravityY, float lifespanDecay, int particleCount) {
        update(gravityY, lifespanDecay, particleCount, null);
    }
    
    public void update(float gravityY, float lifespanDecay, int particleCount, WindSystem windSystem) {
        if (!exploded) {
            prevY = pos.y;
            vel.add(new PVector(0, gravityY));
            pos.add(vel);
            boolean shouldExplode = false;
            if (fuseTimeMillis != null) {
                if (p.millis() - launchTime >= fuseTimeMillis) {
                    shouldExplode = true;
                }
            } else if (targetY != null) {
                if ((vel.y > 0 && pos.y >= targetY) || (prevY > targetY && pos.y <= targetY)) {
                    shouldExplode = true;
                }
            } else {
                if (vel.y >= 0) {
                    shouldExplode = true;
                }
            }
            if (shouldExplode) {
                explode(particleCount, lifespanDecay, gravityY);
                exploded = true;
            }
        }
        // パーティクルの更新（風システム対応）
        for (int i = particles.size() - 1; i >= 0; i--) {
            if (windSystem != null) {
                particles.get(i).update(gravityY, lifespanDecay, windSystem);
            } else {
                particles.get(i).update(gravityY, lifespanDecay);
            }
            if (particles.get(i).isDead()) {
                particles.remove(i);
            }
        }
    }

    private void explode(int particleCount, float lifespanDecay, float gravityY) {
        int count = isHighlight ? 250 : particleCount;
        float maxSpeed = isHighlight ? 12.0f : 8.0f;
        generateParticles(count, maxSpeed, gravityY);
    }

    private void generateParticles(int count, float maxSpeed, float gravityY) {
        // パターンに応じて色を上書きする
        overrideColorForPattern();

        switch (this.pattern) {
            case RING:
                generateRingPattern(count, maxSpeed, gravityY);
                break;
            case LINE:
                generateLinePattern(count, maxSpeed, gravityY);
                break;
            case STAR:
                generateStarPattern(count, maxSpeed, gravityY);
                break;
            case CHRYSANTHEMUM:
                generateChrysanthemumPattern(count, maxSpeed, gravityY);
                break;
            case WILLOW:
                generateWillowPattern(count, maxSpeed, gravityY);
                break;
            case PALM:
                generatePalmPattern(count, maxSpeed, gravityY);
                break;
            case MARUWARI_RED:
                generateMaruwariRed(count, maxSpeed, gravityY);
                break;
            case BOTAN:
                generateBotan(count, maxSpeed, gravityY);
                break;
            case SENRIN_GIKU:
                generateSenrinGiku(count, maxSpeed, gravityY);
                return; // パーティクルを直接生成しないのでここで終了
            case RANDOM:
            default:
                generateRandomPattern(count, maxSpeed, gravityY);
                break;
        }
    }

    // パターンに応じて色を設定するヘルパー
    private void overrideColorForPattern() {
        switch (this.pattern) {
            case MARUWARI_RED:
                this.c = p.color(255, 60, 60); // 鮮やかな赤
                break;
            case BOTAN:
                this.c = p.color(255, 255, 255); // 牡丹は白や銀色が映える
                break;
            case SENRIN_GIKU:
                this.c = p.color(255, 215, 0); // 千輪菊は金色が美しい
                break;
            // 他のパターンは既存の色を使用
        }
    }

    /**
     * 【1. RANDOM / 丸割物】
     * 理想の球体を維持するため、重力を見越して全体を少し上に打ち上げる。
     */
    private void generateRandomPattern(int count, float maxSpeed, float gravityY) {
        // 重力が強いほど、より強く上向きに補正する
        // この係数は、花火が球形を保つ時間と関係します
        float verticalBias = gravityY * 2.5f;

        for (int i = 0; i < count; i++) {
            float angle = p.random(PApplet.TWO_PI);
            float speed = maxSpeed * (float)Math.sqrt(p.random(1));

            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            
            // 全てのパーティクルに、均等な上向きの力を加える
            vel.y -= verticalBias;

            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    /**
     * 【2. RING / 環】
     * 上下のパーティクルで初速を変え、空中で一瞬、完璧な円に見えるように調整。
     */
    private void generateRingPattern(int count, float maxSpeed, float gravityY) {
        // 重力が強いほど、上下の速度差を大きくする必要がある
        // この係数が、リングの「円形度」を決定します
        float compensationFactor = gravityY * 40.0f;

        for (int i = 0; i < count; i++) {
            float angle = PApplet.TWO_PI * i / count;
            
            // 基本速度
            float baseSpeed = maxSpeed * 0.8f;

            // 上向きのパーティクル (sin(angle) < 0) は遅く、
            // 下向きのパーティクル (sin(angle) > 0) は速くする
            float verticalSpeedAdjust = compensationFactor * p.sin(angle);
            
            // 初速を計算
            PVector vel = new PVector(p.cos(angle), p.sin(angle));
            vel.mult(baseSpeed + verticalSpeedAdjust);

            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    /**
     * 【3. LINE / 直線】
     * このパターンは意図が特殊なため、オリジナルのロジックを尊重しつつ、
     * 重力で曲がりにくくする安定化の調整のみ加えます。
     * 目標：空中で一瞬、垂直な光の線を描く。
     */
    private void generateLinePattern(int count, float maxSpeed, float gravityY) {
         for (int i = 0; i < count; i++) {
            // 直線上の位置 (-1.0 to 1.0)
            float t = p.map(i, 0, count - 1, -1, 1);
            
            // ほぼ真上を向く速度ベクトルを生成
            // 速度に差をつけることで、重力下でも線が維持されやすくなる
            float speed = maxSpeed * (1.5f + Math.abs(t));
            PVector vel = new PVector(t * 0.1f, -speed); // 非常に細いV字
            
            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    /**
     * 【4. STAR / 星型】
     * 五芒星の各頂点が、同じくらいの時間、同じ半径の円周上に留まるように調整。
     */
    private void generateStarPattern(int count, float maxSpeed, float gravityY) {
        int rays = 5; // 五芒星
        
        // 重力補正係数
        float compensationFactor = gravityY * 35.0f;

        for (int i = 0; i < count; i++) {
            // どの光線に属するか
            int rayIndex = i % rays;
            // 光線の中心からの距離 (0 to 1)
            float distFromCenter = p.random(0.2f, 1.0f);
            
            // 五芒星の頂点を結ぶ角度
            // 頂点は (2/rays), (4/rays), ...に配置される
            float angle = PApplet.TWO_PI * (rayIndex * 2.0f / rays) - PApplet.HALF_PI;

            float baseSpeed = maxSpeed * distFromCenter * 1.2f;
            
            // RINGと同じく、上下の速度差で補正
            float verticalSpeedAdjust = compensationFactor * p.sin(angle);

            PVector vel = new PVector(p.cos(angle), p.sin(angle));
            vel.mult(baseSpeed + verticalSpeedAdjust);

            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }
    
    // === 日本伝統花火形状 ===
    
    /**
     * 【5. CHRYSANTHEMUM / 菊】
     * 基本は丸割物と同じだが、より繊細で均一な線を描くため、速度のバラつきを抑える。
     * 菊らしさはパーティクルの寿命と描画（尾引き効果）で表現する。
     */
    private void generateChrysanthemumPattern(int count, float maxSpeed, float gravityY) {
        float verticalBias = gravityY * 2.5f;

        for (int i = 0; i < count; i++) {
            float angle = PApplet.TWO_PI * i / count; // 均等に配置
            // 速度のランダム性を抑え、均一な長さの線にする
            float speed = maxSpeed * p.random(0.9f, 1.1f);

            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            vel.y -= verticalBias;
            
            // 菊は寿命を長くするとそれらしくなる
            Particle particle = new Particle(p, pos.copy(), vel, c, isHighlight);
            particle.setLifespan(particle.getLifespan() * 1.5f); // 寿命を1.5倍に
            particles.add(particle);
        }
    }
    
    /**
     * 【6. WILLOW / 柳】
     * 最初に上へ打ち上げ、その勢いが完全に殺された後、重力で美しく垂れ下がる。
     * 爆発初速を抑え、重力に仕事をさせるのがポイント。
     */
    private void generateWillowPattern(int count, float maxSpeed, float gravityY) {
        for (int i = 0; i < count; i++) {
            // 上半分に集中して打ち上げる
            float angle = p.random(-PApplet.PI, 0); 
            
            // 柳は初速が弱い方が美しい。重力に任せる
            float speed = maxSpeed * p.random(0.3f, 0.8f);
            
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            
            // 柳は寿命が命
            Particle particle = new Particle(p, pos.copy(), vel, c, isHighlight, ParticleMaterial.IRON_SPARK);
            particle.setLifespan(particle.getLifespan() * 2.5f); // 寿命を大幅に延長
            particles.add(particle);
        }
    }
    
    /**
     * 【7. PALM / 椰子】
     * 太く力強い数本の「枝」が、重力に負けずに上へ伸びる。
     * 枝の方向に強い初速を与える。
     */
    private void generatePalmPattern(int count, float maxSpeed, float gravityY) {
        int branches = 6; // 6本の太い枝
        float verticalBias = gravityY * 5.0f; // 椰子は重力に逆らう力が強い

        for (int i = 0; i < count; i++) {
            // どの枝に属するか
            int branchIndex = i % branches;
            // 上半分に均等に枝を配置
            float branchAngle = p.map(branchIndex, 0, branches - 1, -PApplet.PI, 0);

            // 枝の根本からの広がり
            float spread = p.random(-0.1f, 0.1f);
            float angle = branchAngle + spread;
            
            // 枝の勢いを表現
            float speed = maxSpeed * p.random(1.2f, 1.5f);

            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            vel.y -= verticalBias;

            Particle particle = new Particle(p, pos.copy(), vel, c, isHighlight, ParticleMaterial.ALUMINUM);
            particle.setLifespan(particle.getLifespan() * 0.8f); // 椰子はキレ良く消える
            particles.add(particle);
        }
    }

    // === 日本伝統花火新ラインナップ（8,9,0キー） ===

    /**
     * 【8. MARUWARI_RED / 赤色の丸割物】
     * 日本の花火の基本形。鮮やかな赤色で完璧な球形を描く。
     */
    private void generateMaruwariRed(int count, float maxSpeed, float gravityY) {
        float verticalBias = gravityY * 2.5f;

        for (int i = 0; i < count; i++) {
            float angle = p.random(PApplet.TWO_PI);
            float speed = maxSpeed * (float)Math.sqrt(p.random(1));
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            vel.y -= verticalBias;
            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    /**
     * 【9. BOTAN / 牡丹】
     * 太く、明るい光の点。菊とは対照的に、パーティクル（星）そのものが大きく、明るく輝く。
     */
    private void generateBotan(int count, float maxSpeed, float gravityY) {
        float verticalBias = gravityY * 2.5f;

        for (int i = 0; i < count; i++) {
            float angle = p.random(PApplet.TWO_PI);
            float speed = maxSpeed * (float)Math.sqrt(p.random(1)) * 0.9f; // 少しゆっくり広がる
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            vel.y -= verticalBias;
            
            Particle particle = new Particle(p, pos.copy(), vel, c, isHighlight);
            // 牡丹はキレが良いので寿命は普通だが、サイズを大きく
            particle.setLifespan(particle.getLifespan() * 1.2f);
            particles.add(particle);
        }
    }

    /**
     * 【0. SENRIN_GIKU / 千輪菊】
     * 無数の小菊が一斉に開く豪華な花火。
     * このメソッドはパーティクルではなく、新しい「ミニ花火」を生成する。
     */
    private void generateSenrinGiku(int count, float maxSpeed, float gravityY) {
        int miniFireworkCount = 12; // 12個の小菊
        
        // PMainFireworksのインスタンスを取得する（要キャスト）
        if (p instanceof PMainFireworks) {
            PMainFireworks mainApp = (PMainFireworks) p;

            for (int i = 0; i < miniFireworkCount; i++) {
                // 親花火の爆発点から、ミニ花火を少しだけ打ち出す
                float angle = p.random(PApplet.TWO_PI);
                float speed = maxSpeed * p.random(0.5f, 2.0f); // 散らばる速度
                PVector miniVel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);

                // ミニ花火を生成
                Firework miniFw = new Firework(p, pos.x, pos.y, false); // ハイライトではない
                miniFw.pos = this.pos.copy(); // 親の爆発位置から開始
                miniFw.vel = miniVel; // 短く移動するための初速
                miniFw.pattern = FireworkPattern.CHRYSANTHEMUM; // 小さな菊が開く
                
                // ミニ花火はすぐに爆発するよう、短い寿命を設定
                miniFw.fuseTimeMillis = p.random(100, 300); // 0.1~0.3秒後に爆発
                miniFw.launchTime = p.millis();

                // メインの花火リストにミニ花火を追加
                mainApp.addFirework(miniFw);
            }
        }
    }

    public void display() {
        if (!exploded) {
            p.stroke(c);
            p.strokeWeight(4);
            p.point(pos.x, pos.y);
        }
        for (Particle particle : particles) {
            particle.display();
        }
    }

    public boolean isDone() {
        return exploded && particles.isEmpty();
    }

    public void setPattern(FireworkPattern pattern) {
        this.pattern = pattern;
    }

    public FireworkPattern getPattern() {
        return this.pattern;
    }
} 