/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;

import java.awt.Graphics;

/**
 *
 * @author macle
 */
public class UFO extends Entity {
    private double shootCoolDown;
    private double shootCounter;
    private boolean small;
    
    public static UFO getAroundCallToThisMustBeFirstStatementVeryAnnoying(double x, double y, double direction, boolean small) {
        //I wanted to be able to have one function to spawn either a small ufo or a big one, but the call to the constructor has to be the first line
        //This function gets around that by just returning a ufo
        if(!small) {
            return new UFO(x, y, direction, 0.7, HitboxTemplates.getUFOTemplate(50), 50, 480, small);
        }
        else {
            return new UFO(x, y, direction, 0.9, HitboxTemplates.getUFOTemplate(38), 38, 420, small);
        }
    }
    
    public UFO(double x, double y, double direction, double speed, Hitbox box, double diameter, double shootCoolDown, boolean small) {
        super(x, y, direction, speed, box, diameter);
        this.shootCoolDown = shootCoolDown;
        shootCounter = shootCoolDown;
        this.small = small;
    }
    
    public void explode(Enteties e, GameHandler h) {
        //kills the ufo in style
        ExplosionEffect ex = new ExplosionEffect(getX(), getY(), getDiameter(), 250, 25, e);
        e.addEntity(ex);
        destroySelf(e);
        h.addScore(250);
    }
    
    private void shoot(Player p, Enteties e, GameHandler g) {
        /*
            shoots towards the player, the higher the score the more accurate.
            big ufos just shoot randomly though
        */
        double angle = findPlayerAngle(p);
        int score = g.getScore();
        double fireArc;
        if(small) {
            fireArc = Math.PI/(2 * (1 + (score/4000)));           
        }
        else {
            fireArc = Math.PI * 2;
        }
        angle += (Math.random() * fireArc) - (fireArc/2);
        Bullet b = new Bullet(getX(), getY(), angle, false, 800);
        e.addEntity(b);
    }
    
    private void updateTimer() {
        if(shootCounter < shootCoolDown) {
            shootCounter++;
        }
    }
    
    
    public void update(Enteties e, Player p, double time, double xBounds, double yBounds, GameHandler h) {
        move(time);
        updateHitbox();
        destroyIfOutOfBounds(xBounds, yBounds, e);
        updateTimer();
        if(shootCounter >= shootCoolDown) {
            shoot(p, e, h);
            shootCounter = 0;
        }
    }
    
    private double findPlayerAngle(Player p) {
        /*
        get angle to the player
        */
        double xParts = p.getX() - getX();
        double yParts = p.getY() - getY();
        double angle = Math.atan(yParts/xParts);
        if(xParts < 0 && yParts < 0) {
            angle += Math.PI;
        }
        else if(xParts < 0 && yParts > 0) {
            angle += Math.PI;
        }
        return angle;
    } 
    
    public void draw(Graphics g) {
        getBox().drawFrame(g);
        double[][] points = getBox().getBox();
        drawPointLine(points[0], points[7], g);
        drawPointLine(points[1], points[6], g);
    }
    
}
