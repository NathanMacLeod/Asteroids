/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;
import java.awt.Graphics;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class Bullet extends Entity {
    private double maxDist;
    private double lifeTime;
    private boolean friendly;
    
    public Bullet(double x, double y, double direction, double speed, Hitbox box, double maxDist, boolean friendly) {
        super(x, y, direction, speed, box, 6);
        this.maxDist = maxDist;
        this.friendly = friendly;
        calculateLife();
    }
    
    public Bullet(double x, double y, double direction, boolean friendly, double range) {
        this(x, y, direction, 2, new Hitbox(new double[][] { new double[] {0, 0}, new double[]{0, 0.1}}), range, friendly);
    }
    
    public void update(Enteties enteties, Player player, double time, double xBounds, double yBounds, GameHandler h) {
        reduceLife(enteties, time);
        move(time);
        checkCollisionWithAsteroids(enteties, h);
        if(friendly) {
            checkCollisionWithUFOs(enteties, h);
        }
        else {
            checkCollisionWithPlayer(player, enteties);
        }
        correctIfOutOfBounds(xBounds, yBounds);
        updateHitbox();
    }
    
    public void setLife(double newLife) {
        lifeTime = newLife;
    }
    
    private void calculateLife() {
        lifeTime = maxDist/getSpeed();
    }
    
    private void reduceLife(Enteties enteties, double time) {
        lifeTime -= time;
        if(lifeTime <= 0) {
            destroySelf(enteties);
        }
    }
    
    public void setFriendly(boolean b) {
        friendly = b;
    }
    
    private void checkCollisionWithAsteroids(Enteties e, GameHandler h) {
        ArrayList<Asteroid> asteroids = e.getAsteroids();
        for(int i = 0; i < asteroids.size(); i++) {
            if(hitboxCollision(asteroids.get(i))) {
                asteroids.get(i).explode(e, h);
                destroySelf(e);
                break;
            }
        }
    }
    
    private void checkCollisionWithUFOs(Enteties e, GameHandler h) {
        ArrayList<UFO> ufos = e.getUFOs();
        for(int i = 0; i < ufos.size(); i++) {
            if(hitboxCollision(ufos.get(i))) {
                ufos.get(i).explode(e, h);
                destroySelf(e);
                break;
            }
        }
    }
    
    private void checkCollisionWithPlayer(Player p, Enteties e) {
        
        if(hitboxCollision(p)) {
            p.destroy(e);
            destroySelf(e);
        }
        
    }
    
    public void draw(Graphics g) {
        g.fillOval((int) getX() - 3, (int) getY() - 3, 6, 6);
    }
}
