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
        RANDOM,  // 従来のランダム拡散（球状）
        RING,    // 環状配置
        LINE,    // 直線状配置
        STAR     // 星型配置
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
        float g = 0.2f;
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
        float g = 0.2f; // GRAVITY_Yと合わせる
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
                explode(particleCount, lifespanDecay);
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

    private void explode(int particleCount, float lifespanDecay) {
        int count = isHighlight ? 250 : particleCount;
        float maxSpeed = isHighlight ? 12.0f : 8.0f;
        generateParticles(count, maxSpeed);
    }

    private void generateParticles(int count, float maxSpeed) {
        switch (this.pattern) {
            case RING:
                generateRingPattern(count, maxSpeed);
                break;
            case LINE:
                generateLinePattern(count, maxSpeed);
                break;
            case STAR:
                generateStarPattern(count, maxSpeed);
                break;
            case RANDOM:
            default:
                generateRandomPattern(count, maxSpeed);
                break;
        }
    }

    private void generateRandomPattern(int count, float maxSpeed) {
        for (int i = 0; i < count; i++) {
            float angle = p.random(PApplet.TWO_PI);
            float speed = maxSpeed * (float)Math.sqrt(p.random(1));
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    private void generateRingPattern(int count, float maxSpeed) {
        // リングは完全に均等に配置、速度も一定
        for (int i = 0; i < count; i++) {
            float angle = PApplet.TWO_PI * i / count;
            float speed = maxSpeed * 1.5f; // かなり速く
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    private void generateLinePattern(int count, float maxSpeed) {
        // 固定方向（真上）でより明確な直線
        float baseAngle = -PApplet.PI / 2; // 真上方向に固定
        for (int i = 0; i < count; i++) {
            float angle = baseAngle + p.random(-0.05f, 0.05f); // 非常に狭い散らばり（±3度）
            float speed = maxSpeed * p.random(1.0f, 2.0f); // より長い直線
            PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
            particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
        }
    }

    private void generateStarPattern(int count, float maxSpeed) {
        int rays = 5; // 五芒星
        int particlesPerRay = count / rays;
        int remaining = count % rays; // 余りの粒子
        
        for (int ray = 0; ray < rays; ray++) {
            int particlesThisRay = particlesPerRay + (ray < remaining ? 1 : 0);
            float rayAngle = PApplet.TWO_PI * ray / rays;
            
            for (int i = 0; i < particlesThisRay; i++) {
                float angle = rayAngle + p.random(-0.1f, 0.1f); // より狭い散らばり（±6度）
                float speed = maxSpeed * p.random(1.2f, 1.8f); // より長い光線
                PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
                particles.add(new Particle(p, pos.copy(), vel, c, isHighlight));
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