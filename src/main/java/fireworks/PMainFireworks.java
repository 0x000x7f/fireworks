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
    private static final int BACKGROUND_ALPHA = 25;

    private List<Firework> fireworks = new ArrayList<>();

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

        // 新しい花火を確率で生成
        if (random(1) < FIREWORK_SPAWN_RATE) {
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

    public static void main(String[] args) {
        PApplet.main(PMainFireworks.class);
    }
} 