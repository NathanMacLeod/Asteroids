package asteroids;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

/**
 *
 * @author macle
 */
public class Asteroids extends JFrame implements Runnable {
    private GraphicsDevice gd;
    private int pWidth;
    private int pHeight;
    private boolean running;
    private Thread runCycle;
    private Graphics gScr;
    private BufferStrategy bufferStrategy;
    private long currentTime;
    private long previousTime;
    private boolean up = false, right = false, left = false, space = false, enter = false, down = false;
    GameHandler game; 
    
    public Asteroids() {
        super("Fighter Combat");
        initializeGraphics();
	setKeyEvents();
        game = new GameHandler(pWidth, pHeight);
        game.goToIntro();
        gameStart();
    }
    
    
    private void gameStart() { 
        runCycle = new Thread(this);
        runCycle.start();
    }
    
    private void initializeGraphics() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();

        setUndecorated(true);  
        setIgnoreRepaint(true);  
        setResizable(false);

        if (!gd.isFullScreenSupported()) {
          System.exit(0);
        }
        gd.setFullScreenWindow(this);
        pWidth = getBounds().width;
        pHeight = getBounds().height;
        setVisible(true);
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();        
    }
    
    private void setKeyEvents() {
	addKeyListener( new KeyAdapter() {
	// listen for esc, q, end, ctrl-c on/ the canvas to
	// allow a convenient exit from the full screen configuration
       public void keyPressed(KeyEvent e) { 
           int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE) {
                   running = false;
            }
            switch(keyCode) {
                case KeyEvent.VK_SPACE:
                   space = true;
                   break;
                case KeyEvent.VK_UP:
                   up = true;
                   break;
                case KeyEvent.VK_LEFT:
                   left = true;
                   break;
                case KeyEvent.VK_RIGHT:
                   right = true;
                   break;
                case KeyEvent.VK_ENTER:
                   enter = true;
                   break;
                case KeyEvent.VK_DOWN:
                    down = true;
                    break;
               
                    
           }
       }
       public void keyReleased(KeyEvent e) {
           int keyCode = e.getKeyCode();
           
            switch(keyCode) {
                case KeyEvent.VK_SPACE:
                   space = false;
                   break;
                case KeyEvent.VK_UP:
                   up = false;
                   break;
                case KeyEvent.VK_LEFT:
                   left = false;
                   break;
                case KeyEvent.VK_RIGHT:
                   right = false;
                   break;
                case KeyEvent.VK_ENTER:
                   enter = false;
                   break;
                case KeyEvent.VK_DOWN:
                    down = false;
                    break;
        }
       } 

     });
  } 
    
    private void gameRender() {
        do {
            do {
            Graphics g = bufferStrategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect (0, 0, pWidth, pHeight);     
            game.gameRender(g);
            g.dispose();
        } while(bufferStrategy.contentsLost());
        
        bufferStrategy.show();
        } while(bufferStrategy.contentsRestored());
    }
    
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        Asteroids game = new Asteroids();
    }
    
    private void gameUpdate(long time) {
        double timeFactor = 80 * time/300000000.00;
        game.gameUpdate(timeFactor, this);
    }
    
    public boolean getUp() {
        return up;
    }
    
    public void setUp(Boolean b) {
        up = b;
    }
    
    public boolean getRight() {
        return right;
    }
    
    public void setRight(Boolean b) {
        right = b;
    }
    
    public boolean getLeft() {
        return left;
    }   
    
    public void setLeft(Boolean b) {
        left = b;
    }
    
    public boolean getEnter() {
        return enter;
    }
    
    public void setEnter(boolean p) {
        enter = p;
    }
    
    public boolean getDown() {
        return down;
    }
    
    public void setDown(Boolean b) {
        down = b;
    }
    
    public boolean getSpace() {
        return space;
    }
    
    public  void setSpace(boolean s) {
        space = s;
    }
    
    
    public void run() {
        running = true;
        previousTime = System.nanoTime();
        currentTime = previousTime;
        while(running) {
            previousTime = currentTime;
            currentTime = System.nanoTime();
            long timeElapsed = currentTime - previousTime;
            gameUpdate(timeElapsed);
            gameRender();
        }
        System.exit(0);
    }
    
}
