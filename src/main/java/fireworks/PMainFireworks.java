package fireworks;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.List;

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

        // フォーカス解除判定
        if (inFocusMode && (millis() - focusStartMillis > FOCUS_DURATION)) {
            inFocusMode = false;
        }
        // フォーカス中でなければ自動花火
        if (!inFocusMode && random(1) < FIREWORK_SPAWN_RATE) {
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
        // クリック位置を目標高度とするハイライト花火を生成
        float targetX = mouseX;
        float targetY = mouseY;
        fireworks.add(new Firework(this, targetX, targetY, true));
        inFocusMode = true;
        focusStartMillis = millis();
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

    public static void main(String[] args) {
        PApplet.main(PMainFireworks.class);
    }
} 