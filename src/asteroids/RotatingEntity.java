/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;
import java.awt.Graphics;
/**
 * 
 * 
 *
 * @author macle
 */
public abstract class RotatingEntity extends Entity {
    private double angle;
    //An entity that can rotate. Only player is a rotating entity right now
    
    public RotatingEntity(double x, double y, double direction, double speed, double angle, Hitbox box, double diameter) {
        super(x, y, direction, speed, box, diameter);
        angle = angle;
    }
    
    public abstract void update(Enteties enteties, Player player, double time, double xBounds, double yBounds, GameHandler h);
    
    public void setAngle(double newAngle) {
        angle = newAngle;
    }
    
    public double getAngle() {
        return angle;
    }
    
    protected void updateHitbox() {
        Hitbox box = getBox();
        double[][] memoryBox = box.getMemoryBox();
        double[][] rotatedBox = rotateHitbox(memoryBox);
        translateHitbox(rotatedBox, getX(), getY());
    }
    
    protected double[][] rotateHitbox(double[][] memoryBox) {
        /*
            htiboxes have a memory box and a real box, memory box has what the points are relative to origin before being translated
            This one just creates a rotated version of the box which is then translated to the position of the object
        */
        double[][] rotatedMemorybox = new double[memoryBox.length][2];
        for(int i = 0; i < memoryBox.length; i++) {
            double[] currentPoint = memoryBox[i];
            double[] newPoint = new double[2];
            double dist = Math.sqrt(Math.pow(currentPoint[0], 2) + Math.pow(currentPoint[1], 2));
            double currentAngle = Math.atan(currentPoint[1]/currentPoint[0]);
            if(currentPoint[0] < 0 && currentPoint[1] < 0) {
            currentAngle += Math.PI;
            }
            else if(currentPoint[0] < 0 && currentPoint[1] > 0) {
                currentAngle += Math.PI;
            }
            newPoint[0] = dist * Math.cos(currentAngle + angle);
            newPoint[1] = dist * Math.sin(currentAngle + angle);
            rotatedMemorybox[i] = newPoint;
        }
        return rotatedMemorybox;
    }
}
