import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * High-performance 2D particle system for sci-fi visual effects:
 * sparks, portal vortex rings, door unlock waves, and pickup bursts.
 */
public class ParticleSystem {
    private static class Particle {
        float x, y;
        float vx, vy;
        Color color;
        float size;
        float life;
        float maxLife;

        Particle(float x, float y, float vx, float vy, Color color, float size, float maxLife) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            this.maxLife = maxLife;
            this.life = maxLife;
        }

        boolean update(float dt) {
            x += vx * dt;
            y += vy * dt;
            life -= dt;
            return life > 0;
        }

        void render(Graphics2D g) {
            float alpha = Math.max(0.0f, Math.min(1.0f, life / maxLife));
            Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 220));
            g.setColor(c);
            int s = (int) Math.max(1.0f, size * alpha);
            g.fillRect((int) (x - s / 2.0f), (int) (y - s / 2.0f), s, s);
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random();

    public void emitBurst(float x, float y, Color color, int count, float speed) {
        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            float spd = speed * (0.4f + 0.8f * rand.nextFloat());
            float vx = (float) Math.cos(angle) * spd;
            float vy = (float) Math.sin(angle) * spd;
            float size = 3.0f + rand.nextFloat() * 4.0f;
            float life = 0.4f + rand.nextFloat() * 0.5f;
            particles.add(new Particle(x, y, vx, vy, color, size, life));
        }
    }

    public void emitSparks(float x, float y, Color color, int count) {
        emitBurst(x, y, color, count, 80.0f);
    }

    public void emitPortalRings(float cx, float cy, Color color) {
        for (int i = 0; i < 2; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            float r = 16.0f + rand.nextFloat() * 8.0f;
            float px = cx + (float) Math.cos(angle) * r;
            float py = cy + (float) Math.sin(angle) * r;
            // swirl velocity
            float vx = -(float) Math.sin(angle) * 40.0f;
            float vy = (float) Math.cos(angle) * 40.0f;
            particles.add(new Particle(px, py, vx, vy, color, 3.5f, 0.45f));
        }
    }

    public void update(float dt) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            if (!it.next().update(dt)) {
                it.remove();
            }
        }
    }

    public void render(Graphics2D g) {
        for (Particle p : particles) {
            p.render(g);
        }
    }

    public void clear() {
        particles.clear();
    }
}
