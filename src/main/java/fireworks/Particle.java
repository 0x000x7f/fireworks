package fireworks;

import processing.core.PApplet;
import processing.core.PVector;

public class Particle {
    PVector pos, vel;
    float lifespan = 255;
    int c;
    PApplet p;
    private static final float LIFESPAN_DECAY_RATE = 0.985f; // 1フレームごとに1.5%減衰
    boolean isHighlight = false;
    float size = 2.0f;

    public Particle(PApplet p, PVector pos, PVector vel, int c) {
        this(p, pos, vel, c, false);
    }
    public Particle(PApplet p, PVector pos, PVector vel, int c, boolean isHighlight) {
        this.p = p;
        this.pos = pos.copy();
        this.vel = vel.copy();
        this.c = c;
        this.isHighlight = isHighlight;
        if (isHighlight) {
            this.lifespan = 350;
            this.size = 3.0f;
        }
    }

    public void update(float gravityY, float lifespanDecay) {
        // 位置を更新
        pos.add(vel);
        // 重力を適用
        vel.y += gravityY;
        // 寿命を減衰
        lifespan *= LIFESPAN_DECAY_RATE;
    }
    
    public void update(float gravityY, float lifespanDecay, WindSystem windSystem) {
        // 風力を適用（風が有効な場合）
        if (windSystem != null && windSystem.isWindEnabled()) {
            PVector windForce = windSystem.getWindForce(pos, size, p.height);
            vel.add(windForce);
        }
        
        // 既存の更新処理
        update(gravityY, lifespanDecay);
    }

    public void display() {
        p.noStroke();
        float flicker = p.random(-20, 20);
        float sizeFlicker = p.random(0.8f, 1.2f);
        if (isHighlight) {
            p.fill(c, lifespan * 0.3f);
            p.ellipse(pos.x, pos.y, size * 2.5f, size * 2.5f);
        }
        p.fill(c, Math.max(0, Math.min(255, lifespan + flicker)));
        p.ellipse(pos.x, pos.y, size * sizeFlicker, size * sizeFlicker);
    }

    public boolean isDead() {
        return lifespan < 1.0f;
    }
} 