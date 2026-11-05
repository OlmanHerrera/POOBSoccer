package src.presentation;
import src.domain.*;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.DecimalFormat;

public class POOBSoccerPanel extends JPanel implements  Runnable{
    
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
    
    

    private DecimalFormat df = new DecimalFormat("0.##"); //2 dp
    private DecimalFormat timedf = new DecimalFormat("0.####"); //4 dp

    
    private POOBSoccer psTop;
    
    private Font font;    
    //Game components
    
    private MatchBall ball;
    
    
    //Make up the force friction
    
    
    
    public POOBSoccerPanel(POOBSoccer ps, long period){
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
        //Adding user interaction
        
        psTop = ps;
        this.period = period;


        setFocusable(true);
        requestFocus(); // so receives key events
        readyForTermination();

        //Create game components
        ball = new MatchBall(PWIDTH,PHEIGHT);
        
        addMouseListener ( new MouseAdapter(){
            public void mousePressed(MouseEvent e )
            {
                testPress(e.getX(), e.getY());
            }
        });
        // define a font
        font = new Font("SansSerif", Font.BOLD, 24);    
        
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
            
            
            double timingError = (double) (period - statsInterval/statsInterval) * 100.0;
            
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
            /*
             * System.out.println(
             * timedf.format( (double) statsInterval/1000000000L) + " " +
             * timedf.format((double) realElapsedTime/1000000000L)+"s "+
             * df.format(timingError) + "% " +
             * frameCount + "c " +
             * framesSkipped + "/" + totalFramesSkipped + " skip; " +
             * df.format(actualFPS) + " " + df.format(averageFPS)+" afps; " +
             * df.format(actualUPS) + " " + df.format(averageUPS)+" aups" );
             */
                        
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
        if(!isPaused && !gameOver){
            //update game components
            float m = ball.getMass();
            PVector v = ball.getVelocity();
            PVector friction = setFriction(v);
            PVector gravity = new PVector(0,(float) 0.1*m);
            ball.applyForce(friction);
            ball.applyForce(gravity);
            ball.update();
            ball.checkEdges();
            
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
        
        // clear the background
        dbg.setColor(Color.white);
        dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
        
        
        dbg.setColor(Color.blue);
        dbg.setFont(font);
        
        // report average FPS and UPS at top left
        dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) +
        ", " + df.format(averageUPS), 20, 25);
        // draw game elements}
        dbg.setColor(Color.green);
        ball.display(dbg); 


    } // end of gameRender( )

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
    
    private void readyForTermination( )
    {
        addKeyListener( new KeyAdapter( ) {
            // listen for esc, q, end, ctrl-c
            public void keyPressed(KeyEvent e)
            { int keyCode = e.getKeyCode( );
                if ((keyCode == KeyEvent.VK_ESCAPE) ||
                (keyCode == KeyEvent.VK_Q) ||
                (keyCode == KeyEvent.VK_END) ||
                ((keyCode == KeyEvent.VK_C) && e.isControlDown( )) ) {
                    running = false;
                }
            }
       });
    } // end of readyForTermination( )
    
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
    
    
}