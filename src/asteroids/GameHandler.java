
/*
 * File added by Nathan MacLeod 2019
 */
package asteroids;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
/**
 *
 * @author macle
 */
public class GameHandler {
    /*
        Game handler is responsible for containing all the stuff and for handling the game logic,
    like if on a certain menu certain things should be drawn and cerain actions should have a certain effect.
    */
    private Enteties e; //non player objects
    private Player p; //the player
    private double pWidth; //screen width
    private double pHeight; //screen height
    private double lives; //amount of lives left
    private double ufoCounter = 0; //countdown to spawning the next ufo
    private int[] nameChars; //the charecters in the name being entered in the save add new high score screen
    private int charIndex; //what character is being edited in the namechars array
    private boolean letterFlicker; //toggling boolean that makes the letter selected flicker in the high score screen
    private final int flickerTime = 120; //how quickly the letter flickers
    private double timeToUFO = 0; //What ufoCounter resets to after a ufo spawns
    private int roundNum = 0; //what round it is
    private double waitTime = 0; //general purpose counter, makes the game wait before respawning, show the game over screen for a certain time, etc
    private int gameState = 0; //0 = normal, 1 = wait for next round, 2 = wait to respawn, 3 = intro screen, 4 = instructions, 5 = gameOverGraphic, 6 = addNewScore, 7 = displayHighScores
    private int score = 0;
    private int lifeScore = 0; //moves up with score, but when it reaches 10,000 it resets in order to give a life every 10,000
    
    public GameHandler(double pWidth, double pHeight) {
        this.pWidth = pWidth;
        this.pHeight = pHeight;
    }
    
    public void startGame() {
        /*
            resets needed stuff
        */
        roundNum = 0;
        lives = 4;
        score = 0;
        lifeScore = 0;
        gameState = 0;
        startNewRound(0);
    }
    
    public void gameUpdate(double timeFactor, Asteroids game) {
        /*
            just splits the different states in order to make easier to manage
        */
        if(gameState == 0 || gameState == 1 || gameState == 2 || gameState == 5) {
            doGameLogic(timeFactor, game);
        }
        else if(gameState == 3 || gameState == 4 || gameState == 7 || gameState == 6) {
            doMenuLogic(timeFactor, game);
        }
    }
    
    public void goToAddScore() {
        nameChars = new int[] {65, 65, 65};
        charIndex = 0;
        letterFlicker = false;
        gameState = 6;
        waitTime = flickerTime;
    }
    //Go to add score and go to intro make sure that the needed things are initalized, like the cosmetic asteroids in the menus
    public void goToIntro() {
        e = new Enteties(pWidth, pHeight);
        gameState = 3;
        createNAsteroids(e, new Player(100000, 100000, 0, 0, 0), 9, 6, 2);
    }
    
    private void doMenuLogic(double time, Asteroids game) {
        e.cosmeticUpdate(time);
        if(gameState == 3) {
            if(game.getSpace()) {
                gameState = 4;
            }
            else if(game.getEnter()) {
                startGame();
            }
            else if(game.getDown()) {
                gameState = 7;
            }
        }
        else if(gameState == 4) {
            if(game.getEnter()) {
                gameState = 3;
                game.setEnter(false);
            }
        }
        else if(gameState == 7) {
            if(game.getEnter()) {
                gameState = 3;
                game.setEnter(false);
            }
        } 
        else if(gameState == 6) {
            addNameToHighScore(game);
        }
        waitTime--;
        if(waitTime < 0) {
            waitTime = flickerTime;
            letterFlicker = !letterFlicker;
        }
    } 
    
    private void addNameToHighScore(Asteroids game) {
        if(game.getLeft()) {
            charIndex--;
            if(charIndex < 0) {
                charIndex = 0;
            }
            game.setLeft(false);
        }
        if(game.getRight()) {
            charIndex++;
            if(charIndex > 2) {
                charIndex = 2;
            }
            game.setRight(false);
        }
        if(game.getDown()) {
            nameChars[charIndex]++;
            if(nameChars[charIndex] > 90) {
                nameChars[charIndex] = 65;
            }
            game.setDown(false);
        }
        if(game.getUp()) {
            nameChars[charIndex]--;
            if(nameChars[charIndex] < 65) {
                nameChars[charIndex] = 90;
            }
            game.setUp(false);
        }
        if(game.getEnter()) {
            saveScore();
            goToIntro();
            game.setEnter(false);
        }
        else if(game.getSpace()) {
            goToIntro();
            game.setSpace(false);
        }
    }
    
    private void saveScore() {
        /*
            gets the current high scores from a text file, finds where the current score belongs and rewrites the file with the new score
        */
        try {
            String scores = getHighScores();
            String[] scoreList = getStringArrayScores(scores);
            int replaceLocation = getScorePlacement(scores, score);
            for(int j = scoreList.length - 1; j > replaceLocation; j--) {

                scoreList[j] = scoreList[j - 1];
        }
        String name = "" + (char) nameChars[0] + (char) nameChars[1] + (char) nameChars[2];
        scoreList[replaceLocation] = name + score;
        PrintWriter out = new PrintWriter(".\\src\\asteroids\\scores.txt");
        for(int k = 0; k < scoreList.length; k++) {
            out.println(scoreList[k]);
        }
        out.close();
        }
        catch(Exception e) {
            System.out.println("failed to save score");
            System.out.println(e);
        }
    }
    
    private void doGameLogic(double timeFactor, Asteroids game) {
        e.updateEnteties(p, timeFactor, this); //update the non player object
        p.update(e, p, timeFactor, pWidth, pHeight, this); //update the player
        if(game.getSpace()) {
          p.shoot(e, pHeight); 
          game.setSpace(false);
        }
        if(game.getUp()) {
           p.thrust(0.007, timeFactor);
        }
        if(game.getLeft()) {
           p.turn(-0.014, timeFactor);
        }
        if(game.getRight()) {
           p.turn(0.014, timeFactor);
        }
        if(game.getDown()) {
            p.activateShield();
        }
        else {
            p.deactivateShield();
        }
        if(gameState == 0) { //0 is normal game mode, when nothing like the player dieing or the round ending has happened
            if(p.destroyed()) {//When a player is killed rather than being removed a boolean called destroyed is made true
                gameState = 2; //if the player is destryoed, wait for a bit then respawn
                waitTime = 600;
            }
            if(noAsteroids(e) && noUFOs(e)) {
                if(gameState == 2) { //Avoids the player being able to not lose a life if the rounds end while he is dead
                    lives--;
                }
                gameState = 1;
                waitTime = 600;//wait a bit, then start next round
            }
            ufoCounter += timeFactor;
            if(ufoCounter >= timeToUFO && !noAsteroids(e)) {
                ufoCounter = 0;//!noAsteroids avoids swamping the player with an unkillable rate of ufos if he took to long and making the game unendable
                timeToUFO /= 1.15;//ufos spawn at an increasing rate over the round
                if(timeToUFO < 1000) {
                    timeToUFO = 1000;//minimum time ufos will take to spawn
                }
                spawnUFO();
            }
        }


        if(gameState == 1 || gameState == 2 || gameState == 5) {
            waitTime -= timeFactor; //These gamestates are just waiting to execute an action, like respawn
            if(waitTime <= 0) { 
                if(gameState == 2) {// 2 is respawn
                    lives--;
                    if(lives <= 0) {
                        //startGame();
                        waitTime = 800;
                        gameState = 5;
                    }
                    else {
                        respawnPlayer(e);
                        gameState = 0;
                    }
                }
                else if(gameState == 1) {//1 is start new round
                    roundNum++;
                    startNewRound(roundNum);
                }
                else if(gameState == 5) { //5 is game over
                    try {//if current score would fit into the top 5, go to add score
                        if(getScorePlacement(getHighScores(), score) != -1) {
                            goToAddScore();
                        }
                        else {
                            goToIntro();
                        }
                    }
                    catch(Exception e) {
                        System.out.println("failed to find scores for initial check");
                        goToIntro();
                    }
                }
            }
        }
    }
    
    public int getScore() {
        return score;
    }
    
    private void spawnUFO() {
        double random = Math.random();
        double height = Math.random() * pHeight;
        double smallOdds = Math.pow(score/3000, 2)/(Math.pow(score/3000, 2) + 1); //the higher score is the more likely a small ufo is spawned
        boolean smallUFO = Math.random() < smallOdds;
         UFO fo;
        if(random > 0.5) { //50/50 chance to come in from the right or left side of screen
            fo = UFO.getAroundCallToThisMustBeFirstStatementVeryAnnoying(pWidth, height, Math.PI, smallUFO);
        }
        else {
            fo = UFO.getAroundCallToThisMustBeFirstStatementVeryAnnoying(0, height, 0, smallUFO);
        }
        e.addEntity(fo);
    }
    
    private void respawnPlayer(Enteties e) {
        /*
            first checks if it can spawn the player in the center safley, 
        if not, it keeps finding random locations until it find ones that works
        */
        boolean foundValidLocation = true;
        double x = pWidth/2;
        double y = pHeight/2;
        do {
            foundValidLocation = true;
            ArrayList<Asteroid> asteroids = e.getAsteroids();
            for(int i = 0; i < asteroids.size(); i++) {
                if(Math.pow((asteroids.get(i).getDiameter()/2) + 100, 2) > (Math.pow(x - asteroids.get(i).getX(), 2) + Math.pow(y - asteroids.get(i).getY(), 2))) {
                    foundValidLocation = false;
                }
            }
            if(!foundValidLocation) {
                x = Math.random() * pWidth;
                y = Math.random() * pHeight;
            }
            
        } while(!foundValidLocation);
        p = new Player(x, y, 0, 0, Math.PI/2);
    }
    
    public void addScore(int score) {
        this.score += score;
        lifeScore += score;
        final int NEW_LIFE_SCORE = 10000;
        if(lifeScore > NEW_LIFE_SCORE) {
            lives++;
            lifeScore %= NEW_LIFE_SCORE;
        }
    }
    
    private void drawScore(Graphics g) {
        final int X_BORDER = 20;
        final int Y_BORDER = 100;
        Font font = new Font("Futura", Font.PLAIN, 30);
        g.setFont(font);
        g.drawString(String.format("%08d", score), X_BORDER, Y_BORDER);
    }
    
    private void drawGameOver(Graphics g) {            
        Font font = new Font("Futura", Font.PLAIN, 40);
        final int Y = ((int)pHeight/2) - 20;
        final int X = ((int)pWidth/2) - g.getFontMetrics(font).stringWidth("GAME OVER")/2;
        g.setFont(font);
        g.drawString("GAME OVER", X, Y);
    }
    
    private void drawIntroGraphic(Graphics g) {
        writeCenteredText("ASTEROIDS", 180, (int)pWidth/2, (int)pHeight * 2/5, g);
        writeCenteredText("Enter to play", 25, (int)pWidth/2, (int)pHeight * 3/4, g);
        writeCenteredText("Space for controls", 25, (int)pWidth/2, (int)pHeight * 4/5, g);
        writeCenteredText("Down to view High Scores", 25, (int)pWidth/2, (int)pHeight * 17/20, g);
    }
    
    private void drawControlsGraphic(Graphics g) {
        int textSize = 20;
        writeCenteredText("Welocme to Asteroids!", textSize, (int)pWidth/2, (int)pHeight * 1/20, g);
        writeCenteredText("The goal of the game is to destroy all the asteorids on the screen. Once this happens a new round will start", textSize, (int)pWidth/2, (int)pHeight * 3/20, g);
        writeCenteredText("At the start of every round a set number of asteroids are created. When you or the asteroids drift off screen they move to the other side", textSize, (int)pWidth/2, (int)pHeight * 5/20, g);
        
        writeCenteredText("Up, left, and right arrow keys are used for movement. Space is to shoot", textSize, (int)pWidth/2, (int)pHeight * 7/20, g);
        writeCenteredText("The down key activates your repulser shield. It can be used to deflect asteroids or bullets", textSize, (int)pWidth/2, (int)pHeight * 8/20, g);
        writeCenteredText("Be careful - it has limited battery life, displayed in the top right corner. If it is too low it cant be used. When not in use the shield will be recharged", textSize, (int)pWidth/2, (int)pHeight * 9/20, g);
        
        
        writeCenteredText("Avoid UFO shots or colliding with an asteroid. Doing such will lose a life, shown in the top left corner. If you lose all your lives its game over", textSize, (int)pWidth/2, (int)pHeight * 12/20, g);
        writeCenteredText("If the center is obstructed, you will instead be respawned in a safe, random location", textSize, (int)pWidth/2, (int)pHeight * 13/20, g);
        writeCenteredText("Every time you shoot an asteroid you earn points, displayed in the top left corner. Every 10,000 points you will be rewarded with an extra life", textSize, (int)pWidth/2, (int)pHeight * 15/20, g);
        writeCenteredText("If you lose all your lives the game is over. See how high of a score you can get!", textSize, (int)pWidth/2, (int)pHeight * 17/20, g);
        writeCenteredText("Press enter to return to the main menu", textSize  + 10, (int)pWidth/2, (int)pHeight *19/20, g);
    }
    
    private void drawDisplayHighScores(Graphics g) {
        int textSize = 60;
        writeCenteredText("High Scores:", textSize, (int)pWidth/2, (int)pHeight * 1/8, g);
        textSize = 40;
        try {
            String[] scores = makeScoresDisplayable(getHighScores());
            for(int i = 0; i < scores.length; i++) {
                writeCenteredText(scores[i], textSize, (int)pWidth/2, (int)pHeight * (2 + i)/8, g);
            }
        }
        catch(Exception e) {
            writeCenteredText("Couldnt find scores", textSize, (int)pWidth/2, (int)pHeight * 2/8, g);
            writeCenteredText("Make sure the scores text file is in the asteroids package", textSize, (int)pWidth/2, (int)pHeight * 4/8, g);
        }
        writeCenteredText("Enter to return to main menu", textSize, (int)pWidth/2, (int)pHeight * 7/8, g);
    }
    
    private void drawAddHighScore(Graphics g) {
        int textSize = 30;
        writeCenteredText("Congrats, your score of " + score + " is high enough to be on the top 5", textSize, (int)pWidth/2, (int)pHeight * 1/8, g);
        textSize = 20;
        writeCenteredText("Use arrow keys to enter the name for the score to be saved with", textSize, (int)pWidth/2, (int)pHeight * 2/8, g);
        writeCenteredText("Hit enter to save, or space to not save", textSize, (int)pWidth/2, (int)pHeight * 3/16, g);
        textSize = 160;
        char[] chars = new char[3];
        for(int i = 0; i < 3; i++) {
            char c;
            if(letterFlicker && charIndex == i) {
                c = ' ';
            }
            else {
                c = (char) nameChars[i];
            }
            chars[i] = c;
        }
        writeCenteredText(chars[0] + "  " + chars[1] + "  " + chars[2], textSize, (int)pWidth/2, (int)(pHeight * 5/8) - 10, g, true);
        writeCenteredText("_  _  _", textSize, (int)pWidth/2, (int)pHeight * 5/8, g, true);
        
    }
    
    private void writeCenteredText(String message, int size, int x, int y, Graphics g) {
        Font font = new Font("Futura", Font.PLAIN, size);
        int Y = (y);
        int X = (x) - g.getFontMetrics(font).stringWidth(message)/2;
        g.setFont(font);
        g.drawString(message, X, Y);
    }
    
    private void writeCenteredText(String message, int size, int x, int y, Graphics g, boolean b) {
        /*
        This function just basically exists for the add high score screen, when the letter flickers its just replace with a spacebar
        If the character arnt monospaced then other letters start sliding around
        */
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, size);
        int Y = (y);
        int X = (x) - g.getFontMetrics(font).stringWidth(message)/2;
        g.setFont(font);
        g.drawString(message, X, Y);
    }
    
    private void drawLives(Graphics g) {
        final int Y_BORDER = 20;
        final int X_BORDER = 30;
        final int FRAME_WIDTH = 30;
        final int GAP = 15;
        final int FRAME_HEIGHT = 40;
        int startX = X_BORDER;
        for(int i = 0; i < lives - 1; i++) {
            drawShip(startX + ((FRAME_WIDTH + GAP) * i), Y_BORDER + FRAME_HEIGHT, FRAME_HEIGHT, FRAME_WIDTH, g);
        }
    }
    
    private void drawShip(int x, int y, int height, int width, Graphics g) {
        g.setColor(Color.white);
        g.drawLine(x, y, x + width/2, y - height);
        g.drawLine(x, y, x + width, y);
        g.drawLine(x + width/2, y - height, x + width, y);
    }
    
    public void gameRender(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(gameState == 0 || gameState == 1 || gameState == 2 || gameState == 5) {
            p.draw(g); 
            p.drawBattery(g, pWidth);
            e.drawEnteties(g);
            if(gameState == 5) {
                drawGameOver(g);
            }
            drawLives(g);
            drawScore(g);
        }
        else {
            e.drawEnteties(g);
            if(gameState == 3) {
                drawIntroGraphic(g);
            }
            else if(gameState == 4) {
                drawControlsGraphic(g);
            }
            else if(gameState == 7) {
                drawDisplayHighScores(g);
            }
            else if(gameState == 6) {
                drawAddHighScore(g);
            }
        }
    }
    
    private String getHighScores() throws Exception{
        /*
        retrives the high scores from the text document
        */
        try {
            Scanner wanner = new Scanner(new FileReader(".//src//asteroids//scores.txt"));
            String scores = "";
            while(wanner.hasNext()) {
                scores += wanner.next() + " ";
            }
            return scores;
        }
        catch(Exception e) {
            throw e;
        }
    }
    
    private String[] getStringArrayScores(String scores) {
        /*
        sorts the scores into a string array
        */
        StringTokenizer tokens = new StringTokenizer(scores);
        String[] fancySchmancy = new String[tokens.countTokens()];
        int i = 0;
        while(tokens.hasMoreTokens()) {
            fancySchmancy[i] = tokens.nextToken();
            i++;
        }
        return fancySchmancy;
    }
    
    private String[] makeScoresDisplayable(String scores) {
        //makes the string into what the displayed version should be
        String[] ray = getStringArrayScores(scores);
        for(int i = 0; i < ray.length; i++) {
            ray[i] = (i + 1) + ".  " + ray[i].substring(0, 3) + "   " + ray[i].substring(3);
        }
        return ray;
    }
    
    private int getScorePlacement(String scores, int score) {
        //gets the scores of the high scores , and sees where score fits into it
        //If its within top 5 it return where, if not it returns -1
        StringTokenizer tokens = new StringTokenizer(scores);
        int i = -1;
        int j = 0;
        while(tokens.hasMoreTokens()) {
            int scoreAt = Integer.parseInt(tokens.nextToken().substring(3));
            if(score > scoreAt) {
               i = j;
               break;
            }
            j++;
        }
        return i;
    }
    
//    public static void main(String[] args) {
//        try {
//            System.out.println(makeScoresDisplayable(getScores()));
//        }
//        catch(Exception e) {
//            System.out.println("MAMA MIA");
//        }
//    }
    
    private void startNewRound(int round) {
        if(p != null && !p.destroyed()) {
            //keeps the player in the same location if hes still alive
                //p = new Player(p.getX(), p.getY(), 0, 0, p.getAngle());
        }
        else {
            p = new Player(pWidth/2, pHeight/2, 0, 0, Math.PI/2);
        }
        e = new Enteties(pWidth, pHeight);
        /*int numSmall = 7 + (round * 3); 
        int numLarge = ((int)round/2) * 2;
        numSmall -= (numLarge * 2);
        int numSuperLarge = ((int)round/5);
        numSmall -= numSuperLarge * 4;*/
        ufoCounter = 0;
        timeToUFO = 6000;
        int numAsteroids = 3 + (round * 2); //increases the number of asteroids each round
        if(numAsteroids > 26) {
            numAsteroids = 26;
        }
        createNAsteroids(e, p, 0, numAsteroids, 0);
        gameState = 0;
    }
    
    private boolean noAsteroids(Enteties e) {
        //are there no more asteroids
        return e.getAsteroids().size() == 0;
    }
    
    private boolean noUFOs(Enteties e) {
        //are there no more ufos
        return e.getUFOs().size() == 0;
    }
    
    private void createNAsteroids(Enteties e, Player p, int nSmall, int nLarge, int nSuperLarge) {
        //I experimented with having differnt sizes of asteroids at first, went against it but the code is still there
        for(int i = 0; i < nSmall; i++) {
            createAsteroidRandomLocation(e, p, 0);
        }
        for(int i = 0; i < nLarge; i++) {
            createAsteroidRandomLocation(e, p, 1);
        }
        for(int i = 0; i < nSuperLarge; i++) {
            createAsteroidRandomLocation(e, p, 2);
        }
    }
    
    private void createAsteroidRandomLocation(Enteties e, Player p, int size) {
        //Spawns the asteroids in a random location that is not ontop of the player
        double ASTEROID_DIAMETER = 120;
        int ASTEROID_SIZE = 3;
        if(size == 0) {
            ASTEROID_DIAMETER = 100;
            ASTEROID_SIZE = 3;
        }
        else if(size == 2) {
            ASTEROID_DIAMETER = 400;
            ASTEROID_SIZE = 5;
        }
        final double MAX_SPEED = 0.4;
        
        boolean foundValidLocation = false;
        double x = 0;
        double y = 0;
        while(!foundValidLocation) {
            x = Math.random() * pWidth;
            y = Math.random() * pHeight;
            if(Math.pow((ASTEROID_DIAMETER/2) + 100, 2) < Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2)) {
                foundValidLocation = true;
            }
        }
        double speed = Math.random() * MAX_SPEED; 
        double direction = Math.random() * Math.PI * 2;
        
        Asteroid roid = new Asteroid(x, y, direction, speed, ASTEROID_DIAMETER, ASTEROID_SIZE);
        e.addEntity(roid);
    }
    
    
}
