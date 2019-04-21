/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;
import java.util.ArrayList;
import java.awt.Graphics;
/**
 *
 * @author macle
 */
public class ExplosionEffect extends Entity {
    /*
        Explosion effect extends entity so that it can be updated in the same array as everything else
        In hindsight I should of had a interface or some higher class as effect inherits some unnececary stuff like hitbox
    */
    private double life;
    private ArrayList<ExplosionParticle> particles = new ArrayList<ExplosionParticle>();
    
    public ExplosionEffect(double x, double y, double diameter, double life, int numberParticles, Enteties e) {
        super(x, y, 0, 0, 0);
        this.life = life;
        createParticles(x, y, diameter, life, numberParticles, e);
    }
    
    private void createParticles(double x, double y, double diameter, double life, int numberParticles, Enteties e) {
        double maxVelocity = diameter/(life);
        for(int i = 0; i < numberParticles; i++) {
            double v = Math.random() * maxVelocity;
            double d = Math.random() * Math.PI * 2;
            ExplosionParticle p = new ExplosionParticle(x, y, d, v, 2);
            e.addEntity(p);
            particles.add(p);
        }
    }
    
    private void loseLife(double time, Enteties e) {
        life -= time;
        if(life <= 0) {
            destroyParticles(e);
            destroySelf(e);
        }
    }
    
    private void destroyParticles(Enteties e) {
        for(int i = 0; i < particles.size(); i++) {
            particles.get(i).destroySelf(e);
        }
    }
    
    public void update(Enteties e, Player p, double time, double dontUse, double dontCare, GameHandler h) {
        loseLife(time, e);
    }
    
    public void draw(Graphics g) {
        for(int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(g);
        }
    }
    
}
