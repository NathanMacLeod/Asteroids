/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
/**
 *
 * @author macle
 */
public class Hitbox {
    private double[][] hitboxMemory; //Stores the locations of points relative to the center
    private double[][] hitboxPoints; //Stores the real location of the points
     //   private ArrayList<double[]> collPoints = new ArrayList<double[]>(); 
    
    public Hitbox(double[][] points) {
        hitboxMemory = points;
        hitboxPoints = points;
        
    }
    
    public boolean collidesWith(Hitbox box) {
        double[][] boxPoints = box.getBox();
        for(int i = 0; i < hitboxPoints.length; i++) {
            double[] point = hitboxPoints[i];
            if(pointInPolygon(boxPoints, point[0], point[1])) {
                return true;
            }
        }
        for(int i = 0; i < boxPoints.length; i++) {
            double[] point = boxPoints[i];
            if(pointInPolygon(hitboxPoints, point[0], point[1])) {
                return true;
            }
        }
        return false;
    }
    
    public double[][] getBox() {
        return hitboxPoints;
    }
    
    public double[][] getMemoryBox() {
        return hitboxMemory;
    }
    
    public void updateHitbox(double[][] newBox) {
        hitboxPoints = newBox;
    }
    
    public boolean pointInPolygon(double[][] pol, double x, double y) {
        /*
            This function describes if a point is inside a polygon by creating two lines that pass through the object, 
        one parralel to the x axis, the other parralel to the y. It then finds the number of intersections with the lines of the polygon to the left, right,
        and number above and below the points. If there is an odd number above and below, and an odd number to the right and the left, this means the point must be inside the polygon
        */
        int xCollsLeft = 0;
        int xCollsRight = 0;
        int yCollsUp = 0;
        int yCollsDown = 0;
        for(int i = 0; i < pol.length; i++) {
            double[] p1 = pol[i];
            double[] p2;
            if(i == pol.length - 1) {
                p2 = pol[0];
            }
            else {
                p2 = pol[i + 1];
            }
            if(inDomain(p1[0], p2[0], x)) {
                double yInt = findYIntersect(p1, p2, x);
                if(yInt > y) {
                    yCollsDown++;
                }
                else {
                    yCollsUp++;
                }
            }
            if(inDomain(p1[1], p2[1], y)) {
                double xInt = findXIntersect(p1, p2, y);
                if(xInt > x) {
                    xCollsRight++;
                }
                else {
                    xCollsLeft++;
                }
                
            }
        }
        if(xCollsLeft % 2 != 0 && xCollsRight % 2 != 0 && yCollsUp % 2 != 0 && yCollsDown % 2 != 0) {
            return true;
        }
        return false;
    }
    
    public boolean pointInside(double[] point) {
        return pointInPolygon(hitboxPoints, point[0], point[1]);
    }
    
    private boolean inDomain(double dom1, double dom2, double val) {
        return ((val > dom1 && val < dom2) || (val > dom2 && val < dom1));
    } 
    
    private double findYIntersect(double[] p1, double[] p2, double x) {
        double m = findSlope(p1, p2);
        double b = findB(p1, m);
        return (m * x) + (-m * p1[0]) + p1[1];
    }
    
    private double findXIntersect(double[] p1, double[] p2, double y) {
        double m = findSlope(p1, p2);
        double b = findB(p1, m);
        return ((y - p1[1])/m) + p1[0];
    }
    
    private double findSlope(double[] p1, double[] p2) {
        /*
            If you divide by 0 the number is INFINITY. it can be used in comparing numbers but I cant do math with it.
        I just make the slope some really big number instead so I can do the same math and logic as with a non verticle line.
        */
        double slope = (p1[1] - p2[1])/(p1[0] - p2[0]);
        if(slope > Integer.MAX_VALUE) {
            slope = Integer.MAX_VALUE;
        }
        else if(slope < -Integer.MAX_VALUE) {
            slope = -Integer.MAX_VALUE;
        }
        return slope;
    }
    
    private double findB(double[] p1, double m) {
        return (-m * p1[0]) + p1[1];
    }
    
    public void drawFrame(Graphics g) {
        /*
            draws the wireframe that nearly all the objects use to draw themselves with.
        */
        for(int i = 0; i < hitboxPoints.length; i++) {
            double[] p1 = hitboxPoints[i];
            double[] p2;
            if(i == hitboxPoints.length - 1) {
                p2 = hitboxPoints[0];
            }
            else {
                p2 = hitboxPoints[i + 1];
            }
            g.setColor(Color.white);
            g.drawLine((int) p1[0],(int) p1[1], (int) p2[0], (int) p2[1]);
        }
    }
    
}
