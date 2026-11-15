package src.presentation;
import src.domain.*;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.DecimalFormat;
import java.awt.image.BufferedImage;

public class POOBSoccerPanel extends JPanel implements  Runnable, MatchObserver{
    
    private static final int  PWIDTH = 500; //size of panel
    private static final int PHEIGHT = 400;
    
    
    Thread animator; //for the animation
    
    //
    private volatile boolean running = false; //stops the animation
    private volatile boolean gameOver = false; // for game termination
    private volatile boolean isPaused = false; // for the game iconifcation
    
    
    
    // global variables for off-screen rendering
    private Graphics dbg;
    private Image dbImage = null;
    //FPS /UPS
    private static int MAX_FRAME_SKIPS = 5;
    private static final long  MAX_STATS_INTERVAL = 1000000000L;
    
    //no. of frames that can be skipped in any one animation loop
    
    private static int NO_DELAYS_PER_YIELD = 10;
    
    //Used for gathering statistics
    
    private long statsInterval = 0L;
    private long gameStartTime;
    private  int timeSpentInGame =0; //in seconds
    private long prevStatsTime;
    private int framesSkipped = 0;
    private long totalFramesSkipped = 0L;
    private long period;
    private int NUM_FPS = 10;
    private int statsCount = 0;
    private long totalElapsedTime = 0L;
    private double[] upsStore;
    private double[] fpsStore;
    private double averageFPS = 0.0;
    private double averageUPS = 0.0;
    
    private int frameCount = 0;
    
    

    private DecimalFormat df = new DecimalFormat("0.####"); //2 dp
    private DecimalFormat timedf = new DecimalFormat("0.####"); //4 dp

    
    private POOBSoccer psTop;
    
    private Font font;
    private static final String IMS_INFO = "imsInfo.txt";
    //Game components
    
    private BallSprite ball;
    private PlayerSprite  rightPlayer;
    private PlayerSprite leftPlayer;
    private BufferedImage bgImage;

    // animated parts of the stadium (crowd.png and flags.png strips)
    private ImagesPlayer crowd;
    private ImagesPlayer flags;
    private static final int CROWD_Y = 150;   // top of the stands in background.jpg
    private static final int FLAGS_Y = 40;    // top of the flag poles in background.jpg
    private static final double CROWD_DURATION = 0.6;  // secs for one crowd cycle
    private static final double FLAGS_DURATION = 0.9;  // secs for one flags cycle

    // fixed pieces drawn over the background
    private BufferedImage scoreboard;         // fits inside the jumbotron screen
    private BufferedImage goalLeft, goalRight;
    private static final int SCOREBOARD_X = 150, SCOREBOARD_Y = 43;
    private static final int GOAL_BASE_Y = 380;   // y where the goals stand on the pitch
    private static final int GOAL_FOOT = 136;     // y of the near panel's bottom inside goal_*.png
    private static final int GOAL_OUT = 6;        // pixels of each goal left outside the panel

    // the match (domain) and what the panel remembers from its events
    private Match match;
    private int leftScore = 0, rightScore = 0;
    private String message = null;      // "GOL" or the winner, drawn in the centre
    private int messageTicks = 0;       // updates left to show the message
    private static final int GOAL_MESSAGE_TICKS = 150;   // 1.5 secs at 100 UPS
    private Font scoreFont = new Font("SansSerif", Font.BOLD, 22);
    private Font messageFont = new Font("SansSerif", Font.BOLD, 40);
    //Make up the force friction
    
    
    
    public POOBSoccerPanel(POOBSoccer ps, long period){
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
        //Adding user interaction
        
        psTop = ps;
        this.period = period;


        setFocusable(true);
        requestFocus(); // so receives key events


        addKeyListener( new KeyAdapter(){
            public void keyPressed(KeyEvent  e)
            {processKey(e);} //handle key presses
        });
        //load the background image
        ImagesLoader imsLoader = new ImagesLoader(IMS_INFO);
        bgImage = imsLoader.getImage("background");
        scoreboard = imsLoader.getImage("scoreboard");
        goalLeft = imsLoader.getImage("goal_left");
        goalRight = imsLoader.getImage("goal_right");
        




        //Create game components
        rightPlayer = new PlayerSprite(375, PWIDTH, PHEIGHT, imsLoader, (int) (period/1000000L), "player2_run" );
        leftPlayer = new PlayerSprite( 125,PWIDTH, PHEIGHT, imsLoader, (int) (period/1000000L), "player1_run");
        ball = new BallSprite(PWIDTH, PHEIGHT, imsLoader, this, leftPlayer,rightPlayer);

        // the match owns the rules; the panel subscribes to hear about goals
        match = new Match(ball, leftPlayer, rightPlayer);
        match.addObserver(this);

        // 1. create the stadium animations: they repeat forever (true);
        // the period is passed in ms, like for the players
        crowd = new ImagesPlayer("crowd", (int) (period/1000000L), CROWD_DURATION, true, imsLoader);
        flags = new ImagesPlayer("flags", (int) (period/1000000L), FLAGS_DURATION, true, imsLoader);
        
        
        addMouseListener ( new MouseAdapter(){
            public void mousePressed(MouseEvent e )
            {
                testPress(e.getX(), e.getY());
            }
        });
        // define a font
        font = new Font("SansSerif", Font.BOLD, 10);    
        
        //initialise timing elements
        
        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++){
            fpsStore[i] = 0.0;
            upsStore[i] = 0.0;
        }
    }//end of GamePanel()
    
    public PVector setFriction(PVector v ){
        float c = (float) 0.01;
        float normal = 1;
        float frictionMag = c*normal;
        PVector friction = v;
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMag);    
        return friction;
    }
        
    public void addNotify( )
    /* Wait for the JPanel to be added to the JFrame/JApplet before starting. */
    {
        super.addNotify( ); // creates the peer
        startGame( ); // start the thread
    }
    
    public void startGame( )
    // initialise and start the thread
    {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start( );
        }
    } // end of startGame( )
    
    public void stopGame( )
    // called by the user to stop execution
    { running = false; }
    
    public void run( )
    /* Repeatedly update, render, sleep */
    {
        
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0;
        
        gameStartTime = System.nanoTime();
        prevStatsTime = gameStartTime;
        beforeTime = gameStartTime;
        running = true;
        while(running) {
            gameUpdate( ); // game state is updated
            gameRender( ); // render to a buffer
            paintScreen(); // draw buffer to screen
            
            afterTime  = System.nanoTime();
            timeDiff = (afterTime-beforeTime);
            sleepTime = (period-timeDiff)- overSleepTime;
            
            if (sleepTime > 0){ //some time left int this cycle
                try{
                    Thread.sleep(sleepTime/1000000L); //nano -> ms
                }
                catch(InterruptedException ex){}
                overSleepTime = (System.nanoTime()- afterTime) - sleepTime;
            }
            else{ //sleepTime <= 0; frame took longer than the period
                excess -= sleepTime; //store excess time value
                overSleepTime = 0L;
                if (++noDelays >= NO_DELAYS_PER_YIELD){
                    Thread.yield(); // give another thread the chance to run 
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();
            /*if frame animation is taking too long, update the game state
            without rendering it, to get the updates/sec nearer o the required FPS*/
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)){
                excess -= period;
                gameUpdate(); //update state but don'r render
                skips++;
            }
            
            framesSkipped += skips;
            storeStats();
        }
        
        printStats();
        System.exit(0); // so enclosing JFrame/JApplet exits
    } // end of run( )
    
    
    public void storeStats(){
        frameCount++;
        statsInterval += period;
        
        if (statsInterval >= MAX_STATS_INTERVAL){
            long timeNow = System.nanoTime();
            timeSpentInGame = (int) ((timeNow-gameStartTime)/1000000000L);
            //psTop.setTimeSpent(timeSpentInGame);
            
            long realElapsedTime = (timeNow- prevStatsTime); //since the last stats collection
            totalElapsedTime += realElapsedTime; 
            
            
            double timingError = (double) ((period - statsInterval)/statsInterval) * 100.0;
            
            totalFramesSkipped += framesSkipped;
            
            double actualUPS =0;
            double actualFPS = 0;
            
            if (totalElapsedTime > 0){
    
                actualFPS = ((double)frameCount / totalElapsedTime)*1000000000L;
                actualUPS = ((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L;
            }
            
            fpsStore[(int) (statsCount%NUM_FPS)] = actualFPS;
            upsStore[(int) (statsCount%NUM_FPS)] = actualUPS;
            statsCount += 1;
            double totalFPS = 0;
            double totalUPS = 0;
            
            for (int i = 0; i< NUM_FPS; i++){
                totalFPS += fpsStore[i];
                totalUPS += upsStore[i];
            }
            
            if (statsCount < NUM_FPS){
                 averageFPS = (double) (totalFPS/statsCount);
                 averageUPS = (double) (totalUPS/statsCount);
    
            }else{
                averageFPS = (double) (totalFPS/NUM_FPS);
                averageUPS = (double) (totalUPS/ NUM_FPS);
            }
            
            System.out.println(
            timedf.format( (double) statsInterval/1000000000L) + " " +
            timedf.format((double) realElapsedTime/1000000000L)+"s "+
            df.format(timingError) + "% " +
            frameCount + "c " +
            framesSkipped + "/" + totalFramesSkipped + " skip; " +
            df.format(actualFPS) + " " + df.format(averageFPS)+" afps; " +
            df.format(actualUPS) + " " + df.format(averageUPS)+" aups" );
        
                        
            framesSkipped = 0;
            statsInterval = 0L;
            prevStatsTime = timeNow;
        }    
    }//end of storeStats();
    
    public void printStats(){
        System.out.println("Frame Count/Loss: " + frameCount + " / "+totalFramesSkipped);
        System.out.println("Average FPS: "+df.format(averageFPS));
        System.out.println("Average UPS: "+ df.format(averageUPS));
    }
    
    private void gameUpdate()
    {
        if(!isPaused){
            if (!gameOver){
                match.update();   // moves ball and players, checks goals
            }

            // 2. advance the stadium animations (they stop while paused)
            crowd.updateTick();
            flags.updateTick();

            if (messageTicks > 0){
                messageTicks--;
            }
        }
    }
    
    private void gameRender()
    //draw the current frame to an image buffer
    {
        if (dbImage == null){
            dbImage = createImage(PWIDTH, PHEIGHT);
            if (dbImage == null){
                System.out.println("dbImage is null");
                return;
            }
            else{
                dbg = dbImage.getGraphics();
            }
        }
        
        if (bgImage == null){ // no background image
            dbg.setColor(Color.white);
            dbg.fillRect(0,0,PWIDTH, PHEIGHT);
        }
        else{
            dbg.drawImage(bgImage, 0,0,this);
        }

        // 3. draw the current crowd and flags images over the background,
        // before the players so they stay behind them
        dbg.drawImage(crowd.getCurrentImage(), 0, CROWD_Y, null);
        dbg.drawImage(flags.getCurrentImage(), 0, FLAGS_Y, null);
        dbg.drawImage(scoreboard, SCOREBOARD_X, SCOREBOARD_Y, null);
        drawScore();

        //draw game elements
        ball.drawSprite(dbg);
        

        leftPlayer.drawSprite(dbg);
        rightPlayer.drawSprite(dbg);
        drawGoals();
        drawMessage();

        dbg.setFont(font);
        dbg.setColor(Color.blue);
        // report average FPS and UPS at top left
        dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) +
        ", " + df.format(averageUPS), 20, 25);
        // draw game elements}

       
        


    } // end of gameRender( )

    // --------- MatchObserver: the Match calls these when the event happens ---------

    public void goalScored(GoalEvent e)
    // remember the new score and show "GOL" for a while
    {
        leftScore = e.getLeftScore();
        rightScore = e.getRightScore();
        message = "GOL!";
        messageTicks = GOAL_MESSAGE_TICKS;
        if (psTop != null){       // the JTextField belongs to Swing: update it on its thread
            final int l = leftScore, r = rightScore;
            SwingUtilities.invokeLater(() -> psTop.setScoreBoard(l, r));
        }
    }

    public void matchEnded(GoalEvent e)
    // stop the game and show the winner until the window is closed
    {
        gameOver = true;
        message = (e.getScorer() == Match.LEFT) ? "Gana jugador 1" : "Gana jugador 2";
        messageTicks = Integer.MAX_VALUE;
    }


    private void drawScore()
    // each score centred in its half of the scoreboard image
    {
        if (scoreboard == null){
            return;
        }
        dbg.setFont(scoreFont);
        dbg.setColor(Color.white);
        FontMetrics fm = dbg.getFontMetrics();
        int half = scoreboard.getWidth() / 2;
        int y = SCOREBOARD_Y + (scoreboard.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        String l = "" + leftScore, r = "" + rightScore;
        dbg.drawString(l, SCOREBOARD_X + (half - fm.stringWidth(l)) / 2, y);
        dbg.drawString(r, SCOREBOARD_X + half + (half - fm.stringWidth(r)) / 2, y);
    }

    private void drawMessage()
    // the GOL / winner message in the centre of the panel, with a shadow
    {
        if ((message == null) || (messageTicks == 0)){
            return;
        }
        dbg.setFont(messageFont);
        FontMetrics fm = dbg.getFontMetrics();
        int x = (PWIDTH - fm.stringWidth(message)) / 2;
        int y = PHEIGHT / 2;
        dbg.setColor(Color.black);
        dbg.drawString(message, x + 2, y + 2);
        dbg.setColor(Color.yellow);
        dbg.drawString(message, x, y);
    }

    private void drawGoals()
    // each goal stands on GOAL_BASE_Y and sticks out GOAL_OUT pixels past its side of the panel
    {
        if (goalLeft != null){
            dbg.drawImage(goalLeft, -GOAL_OUT, GOAL_BASE_Y - GOAL_FOOT, null);
        }
        if (goalRight != null){
            dbg.drawImage(goalRight, PWIDTH - goalRight.getWidth() + GOAL_OUT,
                          GOAL_BASE_Y - GOAL_FOOT, null);
        }
    } // end of drawGoals()

    private void paintScreen( )
    // actively render the buffer image to the screen
    {
        Graphics g;
        try {
            g = this.getGraphics( ); // get the panel's graphic context
            if ((g != null) && (dbImage != null)){
                g.drawImage(dbImage, 0, 0, null);
                Toolkit.getDefaultToolkit( ).sync( ); // sync the display on some systems
                g.dispose();
            }
        }
        catch (Exception e)
        { System.out.println("Graphics context error: " + e); }
    } // end of paintScreen()

    
    public void testPress(int  x, int y)
    // (x,y) 
    {
     if (!isPaused && !gameOver) ;
     
    }//end of testPress();
    
    
    public void pauseGame()
    //called when the JFrame is deactivated / iconified
    {
        isPaused = true;
    }//end of pauseGame();
    
    public void resumeGame()
    //called when the JFrame is activated / deiconified
    {
        isPaused = false;
    }//end of resumeGame();
    

    private void processKey(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        //termination keys
        if ((keyCode == KeyEvent.VK_ESCAPE) ||
            (keyCode == KeyEvent.VK_Q) ||
            (keyCode == KeyEvent.VK_END) ||
            ((keyCode == KeyEvent.VK_C) && e.isControlDown( )) ) {
                running = false;
        }
        //game-play keys

        if (!isPaused && !gameOver){
            //right Player
            if (keyCode == KeyEvent.VK_LEFT){
                rightPlayer.moveLeft();
            }
            else if(keyCode == KeyEvent.VK_RIGHT){
                rightPlayer.moveRight();
            }
            else if (keyCode == KeyEvent.VK_DOWN){
                rightPlayer.stayStill();
            }
            
            //left Player
            if (keyCode == KeyEvent.VK_A){
                leftPlayer.moveLeft();
            }
            else if (keyCode == KeyEvent.VK_D){
                leftPlayer.moveRight();
            }
            else if(keyCode == KeyEvent.VK_S){
                leftPlayer.stayStill();
            }
        }
    } // end of processKey()
    
}