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
public class Player extends RotatingEntity{
    private double airResistance;
    private double shootCooldown;
    private double shootCounter;
    private boolean destroyed;
    private boolean shieldActive;
    private double shieldCounter;
    private double shieldTime;
    private double shieldChargeFactor;
    private double shieldRange;
    private int shieldFlutterOn;
    private double shieldPower;
    private double thrustPercent;
    private final double thrustRate = 0.05;
    
    public Player(double x, double y, double direction, double speed, double angle, Hitbox box, double airResistance, double shootCooldown,
            double shieldTime, double chargeFactor, double shieldRange, double shieldPower) {
        super(x, y, direction, speed, angle, box, 60);
        this.airResistance = airResistance;//there is no air is space, but in asteroids the player slows down like there is
        destroyed = false;
        this.shootCooldown = shootCooldown;
        shieldActive = false;
        this.shieldTime = shieldTime;
        shieldCounter = shieldTime;
        shieldChargeFactor = chargeFactor;
        this.shieldRange = shieldRange;
        shieldFlutterOn = 0;
        this.shieldPower = shieldPower;
        thrustPercent = 0;
    }
    
    public void destroy(Enteties e) {
        ExplosionEffect ex = new ExplosionEffect(getX(), getY(), getDiameter(), 250, 25, e);
        e.addEntity(ex);
        destroyed = true;
    }
    
    public boolean destroyed() {
        return destroyed;
    }
    
    public Player(double x, double y, double direction, double speed, double angle) {
        this(x, y, direction, speed, angle, HitboxTemplates.getPlayerTemplate(40), 0.001, 60, 280, 18, 140, 0.035);
    }
    
    public void turn(double turnVal, double time) {
        setAngle(getAngle() + (turnVal * time));
    }
    
    public void thrust(double thrustVal, double time) {
        accelerate(Math.cos(getAngle()) * time * thrustVal, Math.sin(getAngle()) * thrustVal * time);
        thrustPercent += time * thrustRate;
        if(thrustPercent > 1) {
            thrustPercent = 1;
        }
    }
    
    public void airResistance(double time) {
        setSpeed(getSpeed() - (Math.pow(getSpeed(), 2) * airResistance * time));
    }
    
    public void shoot(Enteties e, double yBound) {
        if(shootCounter >= shootCooldown && !destroyed) {
            shootCounter = 0;
            Bullet b = new Bullet(getX(), getY(), getAngle(), true, 750);
            //b.accelerate(Math.cos(getAngle()), Math.sin(getAngle()));
            e.addEntity(b);
        }
    }
    
    public boolean getShield() {
        return shieldActive;
    }
    
    public void activateShield() {
        //turns the shield on
        if(shieldCounter >= shieldTime/3) {
            shieldActive = true;
        }
    }
    
    public void deactivateShield() {
        shieldActive = false;
    }
    
    private void updateCounters(double time) {
        if(shootCounter < shootCooldown) {
            shootCounter += time;
        }
        if(shieldActive) {
            shieldCounter-= time;
            if(shieldCounter <= 0) {
                shieldActive = false;
            }
        }
        else if(shieldCounter < shieldTime){
            shieldCounter += time / shieldChargeFactor; //charge factor means the shield takes longer to charge then it does to drain it by use
            if(shieldCounter > shieldTime) {
                shieldCounter = shieldTime;
            }
        }
         if(thrustPercent == 1) {
            thrustPercent -= 6 * thrustRate;
        }
        if(thrustPercent > 0) {
            thrustPercent -= (time * thrustRate)/2;
            if(thrustPercent < 0) {
                thrustPercent = 0;
            }
        }
        
    }
    
    private boolean pointInShieldRange(double[] p) {
        return Math.pow(shieldRange/2, 2) > Math.pow(getX() - p[0], 2) + Math.pow(getY() - p[1], 2);
    }
    
    private boolean entityInShieldRange(Entity e) {
        double[][] points = e.getBox().getBox();
        for(int i = 0; i < points.length; i++) {
            if(pointInShieldRange(points[i])) {
                return true;
            }
        }
        return false;
    }
    
    private void shieldRepel(Enteties e, double t) {
        //pushes away asteroids and deflects shots when the shield is active
        ArrayList<Asteroid> asteroids = e.getAsteroids();
        for(int i = 0; i < asteroids.size(); i++) {
            if(entityInShieldRange(asteroids.get(i))) {
                pushEntity(asteroids.get(i), shieldPower * t * 35/(asteroids.get(i).getDiameter()));
            }
        }
        ArrayList<Bullet> bullets = e.getBullets();
        for(int i = 0; i < bullets.size(); i++) {
            if(entityInShieldRange(bullets.get(i))) {
                pushEntity(bullets.get(i), shieldPower * 25 * t);
                bullets.get(i).setLife(450);
                bullets.get(i).setFriendly(true);//makes reflected bullets able to kill ufos
                break;
            }
        }
    
    }
    
    private void pushEntity(Entity e, double pushPower) {
        double x = e.getX() - getX();
        double y = e.getY() - getY();
        double ratio = pushPower / (4 * Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
        e.accelerate(x * ratio, y * ratio);
        
    }
    
    public void update(Enteties enteties, Player player, double time, double xBounds, double yBounds, GameHandler h) {
        if(!destroyed) {
            if(shieldActive) {
                shieldRepel(enteties, time);
            }
            airResistance(time);
            move(time);      
            updateCounters(time);
            correctIfOutOfBounds(xBounds, yBounds);
            updateHitbox();
        }
    }
    
    public void drawBattery(Graphics g, double pWidth) {
        //draws the battery in the top right
        //The frame uses a double[][] from hitboxtemplates and the two rectangles are porportional to how fully charged the battery is
        //the first rectangle indicated if the battery is charged enough to use, second shows if its past that point
        
        int batterySize = 100;
        int borderSize = 30;
        double[][] frame = HitboxTemplates.getBatteryTemplate(batterySize);
        Hitbox peep = new Hitbox(frame);
        translateAnyBox(peep, pWidth - borderSize, borderSize);
        peep.drawFrame(g);
        int firstThirdWidth;
        int secondThirdWidth;
        if(shieldCounter > shieldTime/3) {
            firstThirdWidth = batterySize * 2/9;
            secondThirdWidth = (int) (((shieldCounter - shieldTime/3)/(shieldTime * 2/3)) * batterySize * 3/9);
        }
        else {
            secondThirdWidth = 0;
            firstThirdWidth = (int) ((3 * shieldCounter/shieldTime) * batterySize * 2/9);
        }
        
        g.fillRect((int) (pWidth - (borderSize + (batterySize * 8/9))), (int) (borderSize + batterySize/18), firstThirdWidth, (int) batterySize/3);
        g.fillRect((int) (pWidth - (borderSize + (batterySize * 5/9))), (int) (borderSize + batterySize/18), secondThirdWidth, (int) batterySize/3);
    }
    
    private void drawThrust(Graphics g) {
        final int maxThrust = 32;
        double thrustLength = (maxThrust * thrustPercent) + (getDiameter()/4);
        double[][] points = getBox().getBox();
        double[] thrustTip = new double[] {getX() + Math.cos(getAngle() + Math.PI) * thrustLength, getY() +  Math.sin(getAngle() + Math.PI) * thrustLength};
        drawPointLine(points[3], thrustTip, g);
        drawPointLine(points[4], thrustTip, g);
    }
    
    public void draw(Graphics g) {
        if(!destroyed) {
            getBox().drawFrame(g);
            if(thrustPercent > 0) {
                drawThrust(g);
            }
            if(shieldActive) {
                if(shieldFlutterOn >= 0) {
                    if(shieldFlutterOn >= 2) {
                        shieldFlutterOn = -3;
                    }
                    g.drawOval((int) (getX() - shieldRange/2), (int) (getY() - shieldRange/2), (int) shieldRange, (int) shieldRange);
                }
                shieldFlutterOn++;
            }
        }
    }
    
}
