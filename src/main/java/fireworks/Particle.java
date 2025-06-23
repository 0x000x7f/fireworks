package fireworks;

import processing.core.PApplet;
import processing.core.PVector;

public class Particle {
    PVector pos, vel;
    float lifespan = 255;
    int c;
    PApplet p;

    public Particle(PApplet p, PVector pos, PVector vel, int c) {
        this.p = p;
        this.pos = pos.copy();
        this.vel = vel.copy();
        this.c = c;
    }

    public void update(float gravityY, float lifespanDecay) {
        pos.add(vel);
        vel.y += 0.03;
        lifespan -= 3;
    }

    public void display() {
        p.noStroke();
        p.fill(c, lifespan);
        p.ellipse(pos.x, pos.y, 6, 6);
    }

    public boolean isDead() {
        return lifespan < 0;
    }
} 