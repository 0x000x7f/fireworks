package fireworks;

import processing.core.PVector;

/**
 * 風システムクラス - 花火パーティクルに風の影響を適用
 * v2.0 Wind System の中核コンポーネント
 */
public class WindSystem {
    // 風パラメータ
    private float windVelocityX;        // 風の横方向速度（負=左風、正=右風）
    private float windStrength;         // 風の強度（0.0～1.0）
    private float windTurbulence;       // 風の揺らぎ（ランダム性）
    private float altitudeWindFactor;   // 高度による風強度変化係数
    
    // 風の状態管理
    private boolean isWindEnabled;      // 風のON/OFF
    private float windTime;             // 風の時間変化用
    
    // 定数（現実的な風速に調整）
    private static final float MAX_WIND_VELOCITY = 1.0f;  // 風速を大幅に削減
    private static final float MAX_WIND_STRENGTH = 0.5f;  // 最大強度も削減
    private static final float DEFAULT_TURBULENCE = 0.05f; // 揺らぎも抑制
    private static final float DEFAULT_ALTITUDE_FACTOR = 0.8f; // 高度効果も緩和
    
    /**
     * コンストラクタ - デフォルト値で初期化
     */
    public WindSystem() {
        this.windVelocityX = 0.0f;
        this.windStrength = 0.1f;  // 初期強度10%（微風）
        this.windTurbulence = DEFAULT_TURBULENCE;
        this.altitudeWindFactor = DEFAULT_ALTITUDE_FACTOR;
        this.isWindEnabled = false;  // 初期状態は無風
        this.windTime = 0.0f;
    }
    
    /**
     * 風システムの更新（フレームごとに呼び出し）
     * @param deltaTime フレーム間の時間
     */
    public void update(float deltaTime) {
        windTime += deltaTime;
        // 将来的に風の時間変化をここで実装
    }
    
    /**
     * 指定位置・サイズのパーティクルに対する風力を計算
     * @param position パーティクルの位置
     * @param particleSize パーティクルのサイズ
     * @param screenHeight 画面の高さ（高度計算用）
     * @return 風力ベクトル
     */
    public PVector getWindForce(PVector position, float particleSize, float screenHeight) {
        if (!isWindEnabled || windStrength <= 0) {
            return new PVector(0, 0);  // 風無効時は無風
        }
        
        // 基本風力計算
        float baseWindForce = windVelocityX * windStrength;
        
        // 高度による風の変化（高いほど風が強い）
        float altitudeFactor = calculateAltitudeFactor(position.y, screenHeight);
        
        // パーティクルサイズによる風抵抗（小さいほど影響を受けやすい）
        float sizeResistance = calculateSizeResistance(particleSize);
        
        // 風の揺らぎ（自然なランダム性）
        float turbulenceEffect = calculateTurbulence();
        
        // 最終的な風力計算
        float finalWindForce = baseWindForce * altitudeFactor * sizeResistance * turbulenceEffect;
        
        return new PVector(finalWindForce, 0);
    }
    
    /**
     * 高度による風強度係数を計算（tanh関数で緩和）
     * @param y パーティクルのY座標
     * @param screenHeight 画面の高さ
     * @return 高度係数（0.8～1.2程度）
     */
    private float calculateAltitudeFactor(float y, float screenHeight) {
        // Y座標が小さい（画面上部）ほど高度が高い
        float normalizedAltitude = y / screenHeight;  // 0.0（上部）～1.0（下部）
        
        // tanh関数で緩やかな変化に（急激な変化を防ぐ）
        float factor = (float) Math.tanh((1.0 - normalizedAltitude) * 2.0);
        return 0.8f + factor * 0.4f * altitudeWindFactor;  // 0.8～1.2程度の範囲
    }
    
    /**
     * パーティクルサイズによる風抵抗係数を計算（慣性効果考慮）
     * @param particleSize パーティクルのサイズ
     * @return 抵抗係数（0.2～0.8程度）
     */
    private float calculateSizeResistance(float particleSize) {
        // 表面積と質量の関係を近似
        float area = particleSize * particleSize;  // 表面積 ∝ 半径²
        float mass = particleSize * 0.5f;         // 質量 ∝ 半径（簡易モデル）
        
        // 風感受性 = 表面積 / 質量（物理的根拠）
        float sensitivity = area / (mass + 1.0f);  // +1は分母が0になるのを防ぐ
        
        // 0.2～0.8の範囲に正規化（過度な影響を防ぐ）
        return Math.min(0.8f, Math.max(0.2f, sensitivity * 0.1f));
    }
    
    /**
     * 風の揺らぎ効果を計算
     * @return 揺らぎ係数（0.8～1.2程度）
     */
    private float calculateTurbulence() {
        if (windTurbulence <= 0) return 1.0f;
        
        // -1.0～1.0のランダム値に揺らぎ強度を適用
        float randomFactor = (float) (Math.random() * 2.0 - 1.0) * windTurbulence;
        return 1.0f + randomFactor;
    }
    
    // === 制御メソッド ===
    
    /**
     * 風向きを調整
     * @param deltaVelocity 速度変化量
     */
    public void adjustWindDirection(float deltaVelocity) {
        windVelocityX += deltaVelocity;
        // 範囲制限
        windVelocityX = Math.max(-MAX_WIND_VELOCITY, Math.min(MAX_WIND_VELOCITY, windVelocityX));
    }
    
    /**
     * 風強度を調整
     * @param deltaStrength 強度変化量
     */
    public void adjustWindStrength(float deltaStrength) {
        windStrength += deltaStrength;
        // 範囲制限
        windStrength = Math.max(0.0f, Math.min(MAX_WIND_STRENGTH, windStrength));
    }
    
    /**
     * 風のON/OFF切替
     */
    public void toggleWind() {
        isWindEnabled = !isWindEnabled;
    }
    
    // === ゲッター ===
    
    /**
     * 風の状態文字列を取得（具体的な風速表示）
     * @return 風の状態
     */
    public String getWindStatus() {
        if (!isWindEnabled) {
            return "OFF (無風)";
        }
        
        // 実際の風速を計算（画面単位 → m/s変換）
        float actualWindSpeed = Math.abs(windVelocityX * windStrength);
        float windSpeedMs = actualWindSpeed * 10.0f; // 仮想的にm/sに変換
        
        String direction = windVelocityX < 0 ? "西風" : windVelocityX > 0 ? "東風" : "無風";
        String speedCategory = getWindSpeedCategory(windSpeedMs);
        
        return String.format("ON | %s %.1fm/s (%s)", direction, windSpeedMs, speedCategory);
    }
    
    /**
     * 風速カテゴリを取得（気象学的分類）
     * @param windSpeedMs 風速（m/s）
     * @return 風速カテゴリ
     */
    private String getWindSpeedCategory(float windSpeedMs) {
        if (windSpeedMs < 0.3f) return "静穏";
        else if (windSpeedMs < 1.6f) return "軟風";
        else if (windSpeedMs < 3.4f) return "軽風";
        else if (windSpeedMs < 5.5f) return "微風";
        else if (windSpeedMs < 8.0f) return "弱風";
        else if (windSpeedMs < 10.8f) return "中風";
        else return "強風";
    }
    
    public boolean isWindEnabled() {
        return isWindEnabled;
    }
    
    public float getWindVelocityX() {
        return windVelocityX;
    }
    
    public float getWindStrength() {
        return windStrength;
    }
    
    // === セッター ===
    
    public void setWindVelocity(float velocity) {
        this.windVelocityX = Math.max(-MAX_WIND_VELOCITY, Math.min(MAX_WIND_VELOCITY, velocity));
    }
    
    public void setWindStrength(float strength) {
        this.windStrength = Math.max(0.0f, Math.min(MAX_WIND_STRENGTH, strength));
    }
    
    public void setWindEnabled(boolean enabled) {
        this.isWindEnabled = enabled;
    }
}