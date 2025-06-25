package fireworks;

/**
 * 花火パーティクルの材質特性を定義する列挙型
 * v2.1 Air Resistance & Realistic Physics
 */
public enum ParticleMaterial {
    // 材質名(重力係数, 空気抵抗係数, 基本寿命, 最大落下速度, サイズ, 説明)
    PAPER(0.05f, 0.95f, 300, 3.0f, 1.5f, "紙片：軽くてヒラヒラ舞う"),
    IRON_SPARK(0.12f, 0.99f, 180, 8.0f, 2.0f, "鉄火花：重くて直線的に落下"),
    ALUMINUM(0.08f, 0.97f, 250, 5.0f, 2.5f, "アルミ片：明るく長持ち"),
    MAGNESIUM(0.10f, 0.98f, 200, 6.0f, 3.0f, "マグネシウム：明るい白光"),
    COPPER(0.09f, 0.975f, 220, 5.5f, 2.2f, "銅：緑色の美しい炎");
    
    private final float gravityMultiplier;    // 重力への影響係数
    private final float airResistance;        // 空気抵抗係数 (0.9=強い抵抗, 0.99=弱い抵抗)
    private final float baseLifespan;         // 基本寿命
    private final float maxFallSpeed;         // 最大落下速度（終端速度）
    private final float baseSize;             // 基本サイズ
    private final String description;         // 説明
    
    ParticleMaterial(float gravityMultiplier, float airResistance, float baseLifespan, 
                    float maxFallSpeed, float baseSize, String description) {
        this.gravityMultiplier = gravityMultiplier;
        this.airResistance = airResistance;
        this.baseLifespan = baseLifespan;
        this.maxFallSpeed = maxFallSpeed;
        this.baseSize = baseSize;
        this.description = description;
    }
    
    // ゲッター
    public float getGravityMultiplier() { return gravityMultiplier; }
    public float getAirResistance() { return airResistance; }
    public float getBaseLifespan() { return baseLifespan; }
    public float getMaxFallSpeed() { return maxFallSpeed; }
    public float getBaseSize() { return baseSize; }
    public String getDescription() { return description; }
    
    /**
     * 実際の重力値を計算
     * @param baseGravity 基準重力値
     * @return 材質を考慮した重力値
     */
    public float calculateGravity(float baseGravity) {
        return baseGravity * gravityMultiplier;
    }
    
    /**
     * 速度に空気抵抗を適用
     * @param velocity 現在の速度
     * @return 空気抵抗適用後の速度
     */
    public float applyAirResistance(float velocity) {
        return velocity * airResistance;
    }
    
    /**
     * 終端速度の制限を適用
     * @param velocity 現在の速度
     * @return 終端速度で制限された速度
     */
    public float limitTerminalVelocity(float velocity) {
        if (Math.abs(velocity) > maxFallSpeed) {
            return maxFallSpeed * Math.signum(velocity);
        }
        return velocity;
    }
    
    /**
     * ランダムな材質を選択
     * @return ランダムに選ばれた材質
     */
    public static ParticleMaterial randomMaterial() {
        ParticleMaterial[] materials = values();
        return materials[(int)(Math.random() * materials.length)];
    }
}