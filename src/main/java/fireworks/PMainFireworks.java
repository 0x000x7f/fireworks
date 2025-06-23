package fireworks;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;
import processing.core.PVector;

public class PMainFireworks extends PApplet {
    // 主要パラメータ
    private static final float GRAVITY_Y = 0.2f;
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

    public void settings() {
        fullScreen();
    }

    public void setup() {
        colorMode(HSB, 360, 255, 255, 255);
        frameRate(60);
        background(0);
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
                fireworks.add(new Firework(this, click.x, click.y, true, maxFuseTime));
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
            fireworks.add(new Firework(this, width));
        }

        // 全ての花火を更新・描画
        for (int i = fireworks.size() - 1; i >= 0; i--) {
            Firework f = fireworks.get(i);
            f.update(GRAVITY_Y, PARTICLE_LIFESPAN_DECAY, PARTICLE_COUNT);
            f.display();
            if (f.isDone()) {
                fireworks.remove(i);
            }
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
        if (key == 'r' || key == 'R') {
            fireworks.clear();
        } else if (key == ' ') {
            // 画面中央下部からランダムな花火を1発打ち上げ
            Firework fw = new Firework(this, width);
            fw.pos = new processing.core.PVector(width / 2f, height);
            // 初速はランダムな上向きベクトル
            float angle = random(-PI / 4, -3 * PI / 4); // 上方向
            float speed = random(10, 14);
            fw.vel = new processing.core.PVector(cos(angle) * speed, sin(angle) * speed);
            fireworks.add(fw);
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

    public static void main(String[] args) {
        PApplet.main(PMainFireworks.class);
    }
} 