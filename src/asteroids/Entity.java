/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;
import java.awt.Graphics;
/**
 *
 * @author macle
 */
public abstract class Entity {
    /*
    Parent class for basically everything that moves during the game
    */
    private double x, y, direction, speed; //direction describes the angle that the object is moving in
    private double diameter;
    private int ID;
    private Hitbox hitbox; //hitbox is a class with a pointarray that describes the htibox of the object
    
    public Entity(double x, double y, double direction, double speed, double diameter) {
        this.x  = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.diameter = diameter;
    }
        
    public void setX(double newX) {
        x = newX;
    }
    
    public Entity(double x, double y, double direction, double speed, Hitbox hitbox, double diameter) {
        this(x, y, direction, speed, diameter);
        this.hitbox = hitbox;
        updateHitbox();
    }
    
    public double getDiameter() {
        return diameter;
    }
    
    protected void destroyIfOutOfBounds(double xBounds, double yBounds, Enteties e) {
        /*
        If not on screen destroy the object
        */
        if(x > xBounds + diameter || x + diameter < 0 || y > yBounds + diameter || y + diameter < 0) {
            destroySelf(e);
        }
                    
    }
    
    protected void correctIfOutOfBounds(double xBounds, double yBounds) {
        /*
        if off screen move to the other side
        */
        double overlap  = diameter * 3/4;
        if(x > xBounds + overlap) {
            x = 0;
            y = -(y - yBounds/2) + yBounds/2;
        }
        else if(x + overlap < 0) {
            x = xBounds + overlap;
            y = -(y - yBounds/2) + yBounds/2;
        }
        if(y > yBounds + overlap) {
            y = 0;
//            x = -(x - xBounds/2) + xBounds/2;
        }
        else if(y + overlap < 0) {
            y = yBounds + overlap;
//            x = -(x - xBounds/2) + xBounds/2;
        }
                    
    }
    
    public abstract void draw(Graphics g);
    public abstract void update(Enteties enteties, Player player, double time, double xBounds, double yBounds, GameHandler h);
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    
    public double getDirection() {
        return direction;
    }
    
    public void move(double time) {
        /*
            moves the object. times time to make the speed consistent despite different FPS
        */
        x += speed * time * Math.cos(direction);
        y += speed * time * Math.sin(direction);
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double newSpeed) {
        speed = newSpeed;
    }
    
    public void accelerate(double pushX, double pushY) {
        /*
            basically just combining vectors, first being the current speed and direction
        and the second being the one in the parameter
        */
        double xParts = pushX + (speed * Math.cos(direction));
        double yParts = pushY + (speed * Math.sin(direction));
        speed = Math.sqrt(Math.pow(xParts, 2) + Math.pow(yParts, 2));
        double angle = Math.atan(yParts/xParts);
        if(xParts < 0 && yParts < 0) {
            angle += Math.PI;
        }
        else if(xParts < 0 && yParts > 0) {
            angle += Math.PI;
        }
        direction = angle;
    }
    
    public Hitbox getBox() {
        return hitbox;
    }
    
    protected boolean hitboxCollision(Entity e) {
        return hitbox.collidesWith(e.getHitbox());
    }
    
    protected void updateHitbox() {
        /*
            Hitbox has two arrays, a list of memory points and a list of the real location of the points
            every time the object moves the real locations need to be updated
        */
        double[][] memoryBox = hitbox.getMemoryBox();
        translateHitbox(memoryBox, x, y);
    }
    
    protected void translateHitbox(double[][] memoryBox, double x, double y) {
        double[][] hitBox = new double[memoryBox.length][2];
        for(int i = 0; i < memoryBox.length; i++) {
            double[] mPoint = memoryBox[i];
            double[] point = new double[2];
            point[0] = mPoint[0] + x;
            point[1] = mPoint[1] + y;
            hitBox[i] = point;
        }
        hitbox.updateHitbox(hitBox);
    }
    
    public void translateAnyBox(Hitbox hitbox, double x, double y) {
        /*
        This just exists currently for drawing the battery in the player class, I wasnt sure if Id need it for something else
        so its in the entity class
        */
        double[][] memoryBox = hitbox.getMemoryBox();
        double[][] hitBox = new double[memoryBox.length][2];
        for(int i = 0; i < memoryBox.length; i++) {
            double[] mPoint = memoryBox[i];
            double[] point = new double[2];
            point[0] = mPoint[0] + x;
            point[1] = mPoint[1] + y;
            hitBox[i] = point;
        }
        hitbox.updateHitbox(hitBox);
    }
    
    protected void destroySelf(Enteties e) {
        e.removeEntityWithID(ID);
    }
    
    public double getX() {
        return x;
    }
    
    public int getID() {
        return ID;
    }
    
    public double getY() {
        return y;
    }
    
    protected void drawPointLine(double[] p1, double[] p2, Graphics g) {
        //for objects that need more lines than are on the hitbox frame
        g.drawLine((int) p1[0], (int) p1[1], (int) p2[0], (int) p2[1]);
    }
    
    public Hitbox getHitbox() {
        return hitbox;
    }    
    
}
