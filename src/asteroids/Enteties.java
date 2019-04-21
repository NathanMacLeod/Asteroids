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
public class Enteties {
    private ArrayList<Entity> enteties = new ArrayList<Entity>();
    private int IDIndex = 0;
    private double xBounds;
    private double yBounds;
    
    public Enteties(double xBounds, double yBounds) {
        this.xBounds = xBounds;
        this.yBounds = yBounds;
    }
    
    public Entity getEntity(int index) {
        return enteties.get(index);
    }
    
    public Entity getEntityWithID(int ID) {
        return getEntity(getIndexWithID(ID));
    }
    
    public void addEntity(Entity e) {
        enteties.add(e);
        e.setID(IDIndex);
        IDIndex++;
    }
    
    private void removeEntity(int index) {
        if(index != -1) {
            enteties.remove(index);
        }
    }
    
    public void removeEntityWithID(int ID) {
        removeEntity(getIndexWithID(ID));
    }
    
    public int getIndexWithID(int ID) {
        int i = enteties.size() - 1;
        boolean done = false;
        
        while(!done) {
            Entity e  = enteties.get(i);
            if (e.getID() == ID) {
                done = true;
            }
            else if(i == 0) {
                i = -1; //-1 means id wasnt found
                done = true;
            }
            else {
                i--;
            }
        }
        
        return i;
    }
    
    public ArrayList<Asteroid> getAsteroids() {
        ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
        for(int i = 0; i < enteties.size(); i++) {
            if(enteties.get(i) instanceof Asteroid) {
                asteroids.add((Asteroid) enteties.get(i));
            }
        }
        return asteroids;
    }
    
    public ArrayList<Bullet> getBullets() {
        ArrayList bullets = new ArrayList();
        for(int i = 0; i < enteties.size(); i++) {
            if(enteties.get(i) instanceof Bullet) {
                bullets.add(enteties.get(i));
            }
        }
        return bullets;
    }
    
    public ArrayList<UFO> getUFOs() {
        ArrayList bullets = new ArrayList();
        for(int i = 0; i < enteties.size(); i++) {
            if(enteties.get(i) instanceof UFO) {
                bullets.add(enteties.get(i));
            }
        }
        return bullets;
    }
    
    public void updateEnteties(Player player, double time, GameHandler h) {
        for(int i = 0; i < enteties.size(); i++) {
            enteties.get(i).update(this, player, time, xBounds, yBounds, h);
        }
    }
    
    public void cosmeticUpdate(double time) {
        for(int i = 0; i < enteties.size(); i++) {
            ((Asteroid)enteties.get(i)).cosmeticUpdate(xBounds, yBounds, time);
        }
    }
    
    public void drawEnteties(Graphics g) {
        for(int i = 0; i < enteties.size(); i++) {
            enteties.get(i).draw(g);
        }
    }
    
}
