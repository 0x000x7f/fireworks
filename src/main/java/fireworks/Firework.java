package fireworks;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class Firework {
    enum FireworkType {
        MARU // 日本の伝統的な球状花火
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

    public Firework(PApplet p, float width) {
        this.p = p;
        this.pos = new PVector(p.random(width), p.height);
        this.vel = new PVector(0, p.random(-13, -10));
        int[] rgb = PALETTE[(int)p.random(PALETTE.length)];
        this.c = p.color(rgb[0], rgb[1], rgb[2]);
        this.type = FireworkType.MARU;
    }

    public void update(float gravityY, float lifespanDecay, int particleCount) {
        if (!exploded) {
            vel.add(new PVector(0, gravityY));
            pos.add(vel);
            if (vel.y >= 0) {
                explode(particleCount, lifespanDecay);
                exploded = true;
            }
        }
        for (int i = particles.size() - 1; i >= 0; i--) {
            particles.get(i).update(gravityY, lifespanDecay);
            if (particles.get(i).isDead()) {
                particles.remove(i);
            }
        }
    }

    private void explode(int particleCount, float lifespanDecay) {
        if (type == FireworkType.MARU) {
            for (int i = 0; i < particleCount; i++) {
                float angle = p.random(PApplet.TWO_PI);
                float speed = p.random(2, 8);
                PVector vel = new PVector(p.cos(angle) * speed, p.sin(angle) * speed);
                particles.add(new Particle(p, pos.copy(), vel, c));
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
} 