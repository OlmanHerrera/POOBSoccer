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
    
    
    
    // global variables for off-screen rendering
    private Graphics dbg;
    private Image dbImage = null;
    //FPS /UPS
    private static int MAX_FRAME_SKIPS = 5;
    //no. of frames that can be skipped in any one animation loop
    
    private static int NO_DELAYS_PER_YIELD = 10;
    
    //Used for gathering statistics

    private long prevStatsTime;
    private long frameCount = 0;
    private long totalElapsedTime = 0L;
    private int updateCount = 0;
    
    private DecimalFormat df = new DecimalFormat("0.##"); //2 dp
    
    private double fps;
    private double ups;
    
    
    //Time for a period
    private long period = 10000000L; 
    
    private static final long ONE_SECOND = 1000000000L;
    
    
    private MatchBall ball;
    
    public POOBSoccerPanel(){
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
        //Adding user interaction
        
        setFocusable(true);
        requestFocus();
        readyForTermination();

        //Create game components
        ball = new MatchBall(PWIDTH,PHEIGHT);
    }//end of GamePanel()
    
    
    private void reportStats(int extraUpdates)
    
    {
        frameCount++;
        updateCount += 1 + extraUpdates;
        
        long now = System.nanoTime();
        totalElapsedTime = now -  prevStatsTime; 
        
        if (totalElapsedTime > ONE_SECOND){
            fps = frameCount * (double) ONE_SECOND/totalElapsedTime;
            ups = updateCount * (double) ONE_SECOND/totalElapsedTime;  
            frameCount = 0;
            updateCount = 0;
            prevStatsTime = now;
        }
        
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
        
        prevStatsTime = System.nanoTime();
        beforeTime = System.nanoTime();
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
            
            reportStats(skips);
        }
        System.exit(0); // so enclosing JFrame/JApplet exits
    } // end of run( )
    
    private void gameUpdate()
    {
        if(!gameOver){
            //update game components
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
        // draw game elements
        ball.display(dbg); 
        dbg.drawString("FPS: "+ df.format(fps) + " UPS: "+ df.format(ups),10,20);

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
    
    
    
    
}