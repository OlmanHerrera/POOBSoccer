package src.domain;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class GamePanel extends JPanel implements  Runnable{
    
    private static final int  PWIDTH = 500; //size of panel
    private static final int PHEIGHT = 400;
    
    
    Thread animator; //for the animation
    
    //
    private volatile boolean running = false; //stops the animation
    private volatile boolean gameOver = false; // for game termination
    
    // global variables for off-screen rendering
    private Graphics dbg;
    private Image dbImage = null;
    
    
    private MatchBall ball;
    
    public GamePanel(){
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
        //Adding user interaction
        
        setFocusable(true);
        requestFocus();
        readyForTermination();

        //Create game components
        ball = new MatchBall(PWIDTH,PHEIGHT);
    }//end of GamePanel()
    
    
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
        running = true;
        while(running) {
            gameUpdate( ); // game state is updated
            gameRender( ); // render to a buffer
            paintScreen(); // draw buffer to screen
            try {
                Thread.sleep(20); // sleep a bit
            }
            catch(InterruptedException ex){}
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