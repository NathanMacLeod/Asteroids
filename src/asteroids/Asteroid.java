/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class Asteroid extends Entity {
    private int size;
    private boolean memes = false;
    
    public void testUpdate(double mouseX, double mouseY) {
        if(getBox().pointInside(new double[]{mouseX, mouseY})) {
            memes = true;
        }
        else {
            memes = false;
        }
    }
    
    public Asteroid(double x, double y, double direction, double speed, Hitbox box, double diameter, int size) {
        super(x, y, direction, speed, box, diameter);
        this.size = size;
    }
    
    public Asteroid(double x, double y, double direction, double speed, double diameter, int size) {
        //Hitbox box = Asteroid.getScaleCopyOfRandomTemplate(diameter);
        this(x, y, direction, speed, HitboxTemplates.getScaleCopyOfRandomAsteroidTemplate(diameter), diameter, size);
    }
    
    public void explode(Enteties e, GameHandler h) {
        if(size > 1) {
            createNewAsteroids(e, 2);
        }
        h.addScore(75);
        ExplosionEffect ex = new ExplosionEffect(getX(), getY(), getDiameter(), 250, 45, e);
        e.addEntity(ex);
        destroySelf(e);
    }
    
    private void createNewAsteroids(Enteties e, int numOfAsteroids) {
        final double EXPLOSION_POWER =  0.35;
        for(int i = 0; i < numOfAsteroids; i++) {
            double explosionDirection = Math.random() * Math.PI * 2;
            Asteroid newRoid = new Asteroid(getX(), getY(), getDirection(), getSpeed(), getDiameter()/(numOfAsteroids), getSize() - 1);
            newRoid.accelerate(Math.cos(explosionDirection) * EXPLOSION_POWER, Math.sin(explosionDirection) * EXPLOSION_POWER);
            //System.out.println(newRoid.getDirection());
            e.addEntity(newRoid);
        }
    }
    
    public int getSize() {
        return size;
    }
    
    private void checkPlayerCollision(Player p, Enteties e, GameHandler h) {
        if(getBox().collidesWith(p.getBox()) && !p.destroyed()) {
            p.destroy(e);
            explode(e, h);
        }
    }
    
    private void checkCollisionWithUFOs(Enteties e, GameHandler h) {
        ArrayList<UFO> ufos = e.getUFOs();
        for(int i = 0; i < ufos.size(); i++) {
            if(hitboxCollision(ufos.get(i))) {
                ufos.get(i).explode(e, h);
                explode(e, h);
                //setSpeed(0);
                break;
            }
        }
    }
    
    public void update(Enteties e, Player p, double time, double xBounds, double yBounds, GameHandler h) {
        move(time);
        updateHitbox();
        checkPlayerCollision(p, e, h);
        checkCollisionWithUFOs(e, h);
        correctIfOutOfBounds(xBounds, yBounds);
    }
    
    public void cosmeticUpdate(double xBounds, double yBounds, double time) {
        move(time);
        correctIfOutOfBounds(xBounds, yBounds);
        updateHitbox();
    }
    
    
    public void draw(Graphics g) {
        getBox().drawFrame(g);
        if(memes) {
            g.setColor(Color.yellow); 
            g.fillOval((int) getX(), (int) getY(), 3, 3);
            
        }
      //  getBox().drawPoints(g);
    }
    
}
