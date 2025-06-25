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
    
    // v2.1 Air Resistance & Realistic Physics
    private ParticleMaterial material;
    private float maxLifespan; // 最大寿命を記録

    public Particle(PApplet p, PVector pos, PVector vel, int c) {
        this(p, pos, vel, c, false, ParticleMaterial.randomMaterial());
    }
    
    public Particle(PApplet p, PVector pos, PVector vel, int c, boolean isHighlight) {
        this(p, pos, vel, c, isHighlight, ParticleMaterial.randomMaterial());
    }
    
    public Particle(PApplet p, PVector pos, PVector vel, int c, boolean isHighlight, ParticleMaterial material) {
        this.p = p;
        this.pos = pos.copy();
        this.vel = vel.copy();
        this.c = c;
        this.isHighlight = isHighlight;
        this.material = material;
        
        // 材質に基づいた初期化
        this.lifespan = material.getBaseLifespan();
        this.maxLifespan = this.lifespan;
        this.size = material.getBaseSize();
        
        if (isHighlight) {
            this.lifespan *= 1.5f; // ハイライトは1.5倍長持ち
            this.maxLifespan = this.lifespan;
            this.size *= 1.2f;
        }
    }

    public void update(float gravityY, float lifespanDecay) {
        // v2.1 物理モデルの適用
        updatePhysics(gravityY);
        
        // 位置を更新
        pos.add(vel);
        
        // 寿命を減衰
        lifespan *= LIFESPAN_DECAY_RATE;
    }
    
    /**
     * v2.1 現実的な物理モデルを適用
     */
    private void updatePhysics(float baseGravity) {
        // 1. 材質に基づいた重力を適用
        float materialGravity = material.calculateGravity(baseGravity);
        vel.y += materialGravity;
        
        // 2. 空気抵抗を適用（垂直方向重視）
        vel.x = material.applyAirResistance(vel.x);
        vel.y = material.applyAirResistance(vel.y);
        
        // 3. 終端速度の制限
        vel.y = material.limitTerminalVelocity(vel.y);
        
        // 4. 水平方向の空気抵抗を軽減（形状保持のため）
        vel.x *= 0.998f; // 0.995f → 0.998f 水平抵抗を軽減
    }
    
    public void update(float gravityY, float lifespanDecay, WindSystem windSystem) {
        // v2.1 物理モデルの適用
        updatePhysics(gravityY);
        
        // 風力を適用（風が有効な場合）
        if (windSystem != null && windSystem.isWindEnabled()) {
            PVector windForce = windSystem.getWindForce(pos, size, p.height);
            vel.add(windForce);
        }
        
        // 位置を更新
        pos.add(vel);
        
        // 寿命を減衰
        lifespan *= LIFESPAN_DECAY_RATE;
    }

    public void display() {
        p.noStroke();
        
        // v2.1 材質に基づいた視覚効果
        float lifespanRatio = lifespan / maxLifespan;
        float flicker = p.random(-20, 20);
        float sizeFlicker = p.random(0.8f, 1.2f);
        
        // 材質によるサイズ調整
        float displaySize = size * sizeFlicker;
        
        if (isHighlight) {
            p.fill(c, lifespan * 0.3f);
            p.ellipse(pos.x, pos.y, displaySize * 2.5f, displaySize * 2.5f);
        }
        
        // 材質による光効果調整
        float alpha = Math.max(0, Math.min(255, lifespan + flicker));
        
        // 材質特有の輝き効果
        if (material == ParticleMaterial.MAGNESIUM && lifespanRatio > 0.7f) {
            // マグネシウムは初期に特に明るい
            alpha *= 1.3f;
            displaySize *= 1.1f;
        } else if (material == ParticleMaterial.PAPER && vel.y > 2.0f) {
            // 紙は風で揺れるときにフリッカーが強い
            flicker *= 1.5f;
        }
        
        p.fill(c, Math.max(0, Math.min(255, alpha)));
        p.ellipse(pos.x, pos.y, displaySize, displaySize);
    }

    public boolean isDead() {
        return lifespan < 1.0f;
    }
    
    // v2.1 ゲッター
    public ParticleMaterial getMaterial() {
        return material;
    }
    
    public float getLifespanRatio() {
        return lifespan / maxLifespan;
    }
    
    public float getLifespan() {
        return this.lifespan;
    }
    
    public void setLifespan(float newLifespan) {
        this.lifespan = newLifespan;
        this.maxLifespan = newLifespan; // maxLifespanも更新
    }
} 