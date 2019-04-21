/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroids;
import java.awt.Graphics;
import java.awt.Color;
/**
 *
 * @author macle
 */
public class ExplosionParticle extends Entity{
    
    public ExplosionParticle(double x, double y, double direction, double speed, double diameter) {
        super(x, y, direction, speed, diameter);
    }
    
    public void update(Enteties e, Player p, double time, double x, double y, GameHandler h) {
        move(time);
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillOval((int) (getX() - getDiameter()/2), (int) (getY() - getDiameter()/2), (int) getDiameter(), (int) getDiameter());
    }
    
}
