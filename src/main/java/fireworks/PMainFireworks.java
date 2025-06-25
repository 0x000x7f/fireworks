package fireworks;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import processing.core.PVector;

public class PMainFireworks extends PApplet {
    // 主要パラメータ (v2.1 Air Resistance & Realistic Physics)
    private static final float GRAVITY_Y = 0.12f;  // 0.2f → 0.12f バランス調整
    private static final float FIREWORK_SPAWN_RATE = 0.03f;
    private static final int PARTICLE_COUNT = 100;
    private static final float PARTICLE_LIFESPAN_DECAY = 2.0f;
    private static final int BACKGROUND_ALPHA = 15;

    private List<Firework> fireworks = new ArrayList<>();
    private boolean inFocusMode = false;
    private int focusStartMillis = 0;
    private static final int FOCUS_DURATION = 3000;
    private boolean highlightPending = false; // ハイライト打ち上げ待機中
    private int pendingClickX, pendingClickY; // クリック位置保存
    // --- v1.6 Star-Mine Mode: State Management ---
    private List<PVector> pendingClicks = new ArrayList<>();
    private int lastClickTime = 0;
    private static final int LAUNCH_DELAY_MS = 500; // 0.5秒
    private boolean starMinePending = false; // 闇待機中フラグ
    private int darkStartMillis = 0;
    private static final int DARK_DURATION_MS = 1000; // 1秒闇
    // --- v2.1 Air Resistance & Realistic Physics ---
    private static final String VERSION = "v2.1 Air Resistance & Realistic Physics";
    private Firework.FireworkPattern currentPattern = Firework.FireworkPattern.RANDOM;
    private boolean showDebugInfo = false;
    private char lastPressedKey = ' '; // 最後に押されたキーを記録
    private WindSystem windSystem; // 風システム

    public void settings() {
        fullScreen();
    }

    public void setup() {
        colorMode(HSB, 360, 255, 255, 255);
        frameRate(60);
        background(0);
        // 風システム初期化
        windSystem = new WindSystem();
    }

    public void draw() {
        // 残像エフェクト
        fill(0, 0, 0, BACKGROUND_ALPHA);
        rect(0, 0, width, height);

        // --- v1.6 Star-Mine Mode: Launch Trigger Logic ---
        // クリック連打→0.5秒経過→全花火消滅→1秒闇→一斉打ち上げ
        if (!pendingClicks.isEmpty() && (millis() - lastClickTime > LAUNCH_DELAY_MS) && !starMinePending) {
            // 0.5秒経過後、全花火消滅待機モードへ
            if (fireworks.isEmpty()) {
                starMinePending = true;
                darkStartMillis = millis();
            }
        }
        // 闇の時間経過後に一斉打ち上げ
        if (starMinePending && (millis() - darkStartMillis > DARK_DURATION_MS)) {
            float highestY = height;
            for (PVector click : pendingClicks) {
                if (click.y < highestY) {
                    highestY = click.y;
                }
            }
            float maxFuseTime = calculateFuseTime(highestY);
            for (PVector click : pendingClicks) {
                Firework fw = new Firework(this, click.x, click.y, true, maxFuseTime);
                fw.setPattern(currentPattern);
                fireworks.add(fw);
            }
            pendingClicks.clear();
            starMinePending = false;
            inFocusMode = true;
            focusStartMillis = millis();
        }

        // フォーカス解除判定
        if (inFocusMode && (millis() - focusStartMillis > FOCUS_DURATION)) {
            inFocusMode = false;
        }
        // フォーカスモード中でもなく、ハイライト待機中でもない場合に自動打ち上げ
        if (pendingClicks.isEmpty() && !starMinePending && !inFocusMode && random(1) < FIREWORK_SPAWN_RATE) {
            Firework fw = new Firework(this, width);
            fw.setPattern(currentPattern);
            fireworks.add(fw);
        }

        // 風システム更新
        windSystem.update(1.0f/60.0f); // 60FPS想定
        
        // 全ての花火を更新・描画（風システム適用）
        for (int i = fireworks.size() - 1; i >= 0; i--) {
            Firework f = fireworks.get(i);
            f.update(GRAVITY_Y, PARTICLE_LIFESPAN_DECAY, PARTICLE_COUNT, windSystem);
            f.display();
            if (f.isDone()) {
                fireworks.remove(i);
            }
        }

        // デバッグ情報表示
        if (showDebugInfo) {
            displayDebugInfo();
        }
    }

    @Override
    public void mousePressed() {
        // 待機リストにクリック位置を追加
        pendingClicks.add(new PVector(mouseX, mouseY));
        // 最後のクリック時刻を更新
        lastClickTime = millis();
    }

    @Override
    public void keyPressed() {
        lastPressedKey = key; // キーを記録
        
        // 矢印キーによる風制御（現実的な調整幅）
        if (keyCode == LEFT) {
            windSystem.adjustWindDirection(-0.1f); // 左風（調整量削減）
            println("Wind direction: LEFT");
        } else if (keyCode == RIGHT) {
            windSystem.adjustWindDirection(0.1f);  // 右風（調整量削減）
            println("Wind direction: RIGHT");
        } else if (keyCode == UP) {
            windSystem.adjustWindStrength(0.05f);  // 風強度UP（調整量削減）
            println("Wind strength UP");
        } else if (keyCode == DOWN) {
            windSystem.adjustWindStrength(-0.05f); // 風強度DOWN（調整量削減）
            println("Wind strength DOWN");
        } else if (key == 't' || key == 'T') {
            windSystem.toggleWind();               // 風ON/OFF
            println("Wind: " + (windSystem.isWindEnabled() ? "ON" : "OFF"));
        } else if (key == 'r' || key == 'R') {
            fireworks.clear();
        } else if (key == ' ') {
            // 画面中央下部からランダムな花火を1発打ち上げ
            Firework fw = new Firework(this, width);
            fw.setPattern(currentPattern);
            fw.pos = new processing.core.PVector(width / 2f, height);
            // 初速はランダムな上向きベクトル
            float angle = random(-PI / 4, -3 * PI / 4); // 上方向
            float speed = random(10, 14);
            fw.vel = new processing.core.PVector(cos(angle) * speed, sin(angle) * speed);
            fireworks.add(fw);
        } else if (key >= '1' && key <= '9' || key == '0') {
            // 数字キーでパターン切替（1-9,0対応）
            Firework.FireworkPattern oldPattern = currentPattern;
            switch (key) {
                case '1':
                    currentPattern = Firework.FireworkPattern.RANDOM;
                    break;
                case '2':
                    currentPattern = Firework.FireworkPattern.RING;
                    break;
                case '3':
                    currentPattern = Firework.FireworkPattern.LINE;
                    break;
                case '4':
                    currentPattern = Firework.FireworkPattern.STAR;
                    break;
                case '5':
                    currentPattern = Firework.FireworkPattern.CHRYSANTHEMUM;
                    break;
                case '6':
                    currentPattern = Firework.FireworkPattern.WILLOW;
                    break;
                case '7':
                    currentPattern = Firework.FireworkPattern.PALM;
                    break;
                case '8':
                    currentPattern = Firework.FireworkPattern.MARUWARI_RED;
                    break;
                case '9':
                    currentPattern = Firework.FireworkPattern.BOTAN;
                    break;
                case '0':
                    currentPattern = Firework.FireworkPattern.SENRIN_GIKU;
                    break;
            }
            println("Key '" + key + "' pressed: " + oldPattern + " -> " + currentPattern);
            
            // テスト用花火を即座に打ち上げ
            Firework testFw = new Firework(this, width/2, height*0.3f, true);
            testFw.setPattern(currentPattern);
            fireworks.add(testFw);
            println("Test firework launched with pattern: " + currentPattern);
        } else if (key == 'd' || key == 'D') {
            showDebugInfo = !showDebugInfo;
            println("Debug info: " + (showDebugInfo ? "ON" : "OFF"));
        }
    }

    private float calculateFuseTime(float targetY) {
        if (targetY < 0) targetY = 0;
        if (targetY >= height) targetY = height - 1;
        float dy = height - targetY;
        float vy = -(float)Math.sqrt(2 * GRAVITY_Y * dy);
        float timeInFrames = -vy / GRAVITY_Y;
        return timeInFrames * (1000.0f / 60.0f);
    }

    private void displayDebugInfo() {
        // 非常に薄い半透明な背景（元の位置、上部）
        fill(0, 0, 0, 50); // 非常に薄い透明度
        rect(5, 5, width - 10, 300);
        
        // 境界線を追加（薄く）
        stroke(255, 255, 0, 100); // 境界線も薄く
        strokeWeight(1);
        noFill();
        rect(5, 5, width - 10, 300);
        noStroke();

        // デバッグテキスト（全て明るい緑色に統一）
        fill(100, 255, 100); // 明るい緑色
        textSize(24);
        text("=== DEBUG INFO ===", 15, 35);
        
        // バージョン情報
        textSize(20);
        text("Version: " + VERSION, 15, 65);
        
        // 詳細情報
        textSize(18);
        text("Current Pattern: " + currentPattern, 15, 95);
        text("Last Pressed Key: '" + lastPressedKey + "'", 15, 125);
        text("Active Fireworks: " + fireworks.size(), 15, 155);
        text("Star-Mine Pending: " + starMinePending, 15, 185);
        text("Focus Mode: " + inFocusMode, 15, 215);
        text("Pending Clicks: " + pendingClicks.size(), 15, 245);
        
        // 風情報表示（強調）
        textSize(16);
        fill(120, 255, 255); // 水色で風情報を強調
        text("Wind: " + windSystem.getWindStatus(), 15, 275);
        
        // 風の詳細情報
        fill(100, 255, 100); // 元の明るい緑に戻す
        textSize(14);
        if (windSystem.isWindEnabled()) {
            text("Wind Details: Vel=" + String.format("%.2f", windSystem.getWindVelocityX()) + 
                 " | Str=" + String.format("%.2f", windSystem.getWindStrength()), 15, 295);
        }
        
        // パターン説明
        textSize(12);
        text("Controls: 1-9,0=Pattern  D=Debug  R=Reset  Space=Launch", 15, 315);
        text("Patterns: 1=丸割物 2=正円 3=直線 4=星 5=菊 6=柳 7=椰子", 15, 330);
        text("Japanese: 8=赤丸割物 9=牡丹 0=千輪菊", 15, 345);
        text("Wind: ←→=Direction  ↑↓=Strength  T=Toggle", 15, 360);
    }

    /**
     * 花火リストに新しい花火を追加するための公開メソッド
     * (千輪菊から呼び出される)
     */
    public void addFirework(Firework fw) {
        this.fireworks.add(fw);
    }

    public static void main(String[] args) {
        PApplet.main(PMainFireworks.class);
    }
}