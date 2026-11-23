package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.Side;
import src.domain.Action;
import src.domain.MatchObserver;
import src.domain.GoalEvent;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Panel del juego con el bucle de animación activa de KGPJ: actualiza, dibuja y duerme.
 * Solo conoce la fachada del dominio.
 */
public class POOBSoccerPanel extends JPanel implements  Runnable, MatchObserver{

    private static final int DEFAULT_WIDTH = 500; //size of panel before the first match
    private static final int DEFAULT_HEIGHT = 400;
    private volatile int fieldWidth = DEFAULT_WIDTH;
    private volatile int fieldHeight = DEFAULT_HEIGHT;


    Thread animator; //for the animation

    //
    private volatile boolean running = false; //stops the animation
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



    private DecimalFormat df = new DecimalFormat("0.####"); //4 dp
    private DecimalFormat timedf = new DecimalFormat("0.####"); //4 dp


    private POOBSoccer psTop;
    private POOBSoccerGame game;

    private Hud hud;
    private Font font;
    private static final int FPS_X = 8;
    private static final int FPS_BOTTOM_MARGIN = 6;
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HINT_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Color MESSAGE_COLOR = new Color(0, 0, 0, 170);
    private static final int TITLE_OFFSET = 35;
    private static final int TITLE_AREA = 62;
    private static final int LINE_STEP = 20;
    private static final String PAUSE_TITLE = "PAUSA";
    private static final String[] PAUSE_LINES = {"P para continuar - ESC para terminar el partido"};
    private static final String OVER_HINT = "ESC para volver al menú";
    private static final String PLAYER_1 = "Jugador 1";
    private static final String PLAYER_2 = "Jugador 2";
    private String endTitle = "";
    private String[] endLines = {OVER_HINT};
    private static final String QUIT_QUESTION = "¿Terminar el partido ahora? No habrá ganador ni puntos.";
    private static final String QUIT_TITLE = "Terminar partido";


    /**
     * Crea el panel del juego.
     * @param ps ventana principal
     * @param game fachada del dominio
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccerPanel(POOBSoccer ps, POOBSoccerGame game, long period){
        setBackground(Color.white);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        //Adding user interaction

        psTop = ps;
        this.game = game;
        this.period = period;


        setFocusable(true);
        requestFocus(); // so receives key events


        addKeyListener( new KeyAdapter(){
            public void keyPressed(KeyEvent  e)
            {processKey(e);} //handle key presses
        });
        game.addObserver(this);
        hud = new Hud();

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


    /**
     * Espera a que el panel esté dentro de la ventana para arrancar el bucle.
     */
    public void addNotify( )
    {
        super.addNotify( ); // creates the peer
        startGame( ); // start the thread
    }

    /**
     * Arranca el hilo del bucle de animación (el único hilo del juego).
     */
    public void startGame( )
    {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start( );
        }
    } // end of startGame( )

    /** Detiene el bucle; al salir del bucle se cierra la aplicación. */
    public void stopGame( )
    { running = false; }

    /**
     * Bucle de animación: actualiza, dibuja, duerme lo que falte del período.
     */
    public void run( )
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


    /** Junta las estadísticas de FPS y UPS cada segundo (KGPJ). */
    public void storeStats(){
        frameCount++;
        statsInterval += period;

        if (statsInterval >= MAX_STATS_INTERVAL){
            long timeNow = System.nanoTime();
            timeSpentInGame = (int) ((timeNow-gameStartTime)/1000000000L);

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

    /** Imprime las estadísticas finales de FPS y UPS (KGPJ). */
    public void printStats(){
        System.out.println("Frame Count/Loss: " + frameCount + " / "+totalFramesSkipped);
        System.out.println("Average FPS: "+df.format(averageFPS));
        System.out.println("Average UPS: "+ df.format(averageUPS));
    }

    private void gameUpdate()
    {
        if(!isPaused){
            game.update();
        }
    }

    private void gameRender()
    //draw the current frame to an image buffer
    {
        if (dbImage == null){
            dbImage = createImage(fieldWidth, fieldHeight);
            if (dbImage == null){
                System.out.println("dbImage is null");
                return;
            }
            else{
                dbg = dbImage.getGraphics();
            }
        }

        dbg.setColor(Color.white);
        dbg.fillRect(0, 0, fieldWidth, fieldHeight);
        if (!game.hasMatch()){
            return;
        }
        game.draw(dbg);
        hud.draw(dbg, game, fieldWidth);
        if (game.isMatchOver()){
            drawMessage(endTitle, endLines);
        }
        else if (game.isPaused()){
            drawMessage(PAUSE_TITLE, PAUSE_LINES);
        }

        dbg.setColor(Color.black);
        dbg.setFont(font);
        // report average FPS and UPS at bottom left
        dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) +
        ", " + df.format(averageUPS), FPS_X, fieldHeight - FPS_BOTTOM_MARGIN);
    } // end of gameRender( )

    /**
     * Dibuja un aviso en el centro de la cancha.
     * @param title texto grande
     * @param lines textos pequeños, uno por línea, debajo del título
     */
    private void drawMessage(String title, String[] lines)
    {
        int height = TITLE_AREA + lines.length * LINE_STEP;
        int top = (fieldHeight - height) / 2;
        dbg.setColor(MESSAGE_COLOR);
        dbg.fillRect(0, top, fieldWidth, height);
        dbg.setColor(Color.white);
        dbg.setFont(MESSAGE_FONT);
        hud.drawCentered(dbg, title, fieldWidth / 2, top + TITLE_OFFSET);
        dbg.setFont(HINT_FONT);
        for (int i = 0; i < lines.length; i++){
            hud.drawCentered(dbg, lines[i], fieldWidth / 2, top + TITLE_AREA + i * LINE_STEP);
        }
    }


    /**
     * Aviso de gol: no hace falta hacer nada, el HUD ya muestra el marcador.
     * @param e datos del gol
     */
    public void goalScored(GoalEvent e)
    {
    }

    /**
     * Aviso de fin del partido: arma el mensaje con el ganador y los puntos
     * que ganó cada jugador.
     * @param e datos del final; su lado es el del ganador
     */
    public void matchEnded(GoalEvent e)
    {
        Side winner = e.getScorer();
        endTitle = "¡Ganó el " + playerName(winner) + " (" + game.getCountryName(winner) + ")!";
        endLines = new String[] {pointsText(Side.LEFT), pointsText(Side.RIGHT), OVER_HINT};
    }

    /**
     * @param s lado
     * @return "Jugador 1" o "Jugador 2"
     */
    private String playerName(Side s)
    {
        if (s == Side.LEFT){
            return PLAYER_1;
        }
        return PLAYER_2;
    }

    /**
     * @param s lado
     * @return por ejemplo "Jugador 1: +100 puntos (ahora tiene 225)"
     */
    private String pointsText(Side s)
    {
        return playerName(s) + ": +" + game.getPointsGained(s) + " puntos (ahora tiene " + game.getPoints(s) + ")";
    }

    private void paintScreen( )
    // actively render the buffer image to the screen
    {
        if (!isShowing()){
            return; // otra pantalla del CardLayout está encima: no se pinta sobre ella
        }
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
        {
            System.out.println("Graphics context error: " + e);
            game.recordError(e);
        }
    } // end of paintScreen()


    /**
     * Ajusta el panel al tamaño de la cancha del partido; la imagen del doble
     * buffer se vuelve a crear con el tamaño nuevo.
     * @param width ancho de la cancha
     * @param height alto de la cancha
     */
    public void setFieldSize(int width, int height)
    {
        fieldWidth = width;
        fieldHeight = height;
        setPreferredSize(new Dimension(width, height));
        dbImage = null;
    }

    /** Pausa el bucle cuando la ventana se desactiva o se minimiza. */
    public void pauseGame()
    {
        isPaused = true;
    }//end of pauseGame();

    /** Reanuda el bucle cuando la ventana se activa o se restaura. */
    public void resumeGame()
    {
        isPaused = false;
    }//end of resumeGame();


    private void processKey(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE){
            escapePressed();
            return;
        }
        if (keyCode == KeyEvent.VK_P){
            togglePause();
            return;
        }
        if (isPaused || game.isPaused() || game.isMatchOver()){
            return;
        }
        Action left = leftAction(keyCode);
        if (left != null){
            game.perform(Side.LEFT, left);
        }
        Action right = rightAction(keyCode);
        if (right != null){
            game.perform(Side.RIGHT, right);
        }
    } // end of processKey()

    /** P pausa o reanuda el partido. */
    private void togglePause()
    {
        if (game.isMatchOver()){
            return;
        }
        if (game.isPaused()){
            game.resume();
        }
        else{
            game.pause();
        }
    }

    /**
     * ESC termina el partido en cualquier momento (con confirmación),
     * o vuelve al menú si el partido ya terminó.
     */
    private void escapePressed()
    {
        if (game.isMatchOver()){
            psTop.showMenu();
            return;
        }
        if (game.isPaused()){
            askQuit();
            return;
        }
        game.pause();
        if (!askQuit()){
            game.resume();
        }
    }

    /**
     * Pregunta si se termina el partido y, si dice que sí, lo termina y vuelve al menú.
     * @return true si el partido se terminó
     */
    private boolean askQuit()
    {
        int answer = JOptionPane.showConfirmDialog(this, QUIT_QUESTION, QUIT_TITLE, JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION){
            return false;
        }
        game.endMatch();
        psTop.showMenu();
        return true;
    }

    /**
     * Teclas del jugador izquierdo: A, D, S, W, Z (patear) y X (poder).
     * @param keyCode tecla presionada
     * @return acción de esa tecla, o null si no es suya
     */
    private Action leftAction(int keyCode)
    {
        if (keyCode == KeyEvent.VK_A){
            return Action.MOVE_LEFT;
        }
        if (keyCode == KeyEvent.VK_D){
            return Action.MOVE_RIGHT;
        }
        if (keyCode == KeyEvent.VK_S){
            return Action.STAY_STILL;
        }
        if (keyCode == KeyEvent.VK_W){
            return Action.JUMP;
        }
        if (keyCode == KeyEvent.VK_Z){
            return Action.KICK;
        }
        if (keyCode == KeyEvent.VK_X){
            return Action.POWER;
        }
        return null;
    }

    /**
     * Teclas del jugador derecho: flechas, ESPACIO (patear) y ENTER (poder).
     * @param keyCode tecla presionada
     * @return acción de esa tecla, o null si no es suya
     */
    private Action rightAction(int keyCode)
    {
        if (keyCode == KeyEvent.VK_LEFT){
            return Action.MOVE_LEFT;
        }
        if (keyCode == KeyEvent.VK_RIGHT){
            return Action.MOVE_RIGHT;
        }
        if (keyCode == KeyEvent.VK_DOWN){
            return Action.STAY_STILL;
        }
        if (keyCode == KeyEvent.VK_UP){
            return Action.JUMP;
        }
        if (keyCode == KeyEvent.VK_SPACE){
            return Action.KICK;
        }
        if (keyCode == KeyEvent.VK_ENTER){
            return Action.POWER;
        }
        return null;
    }

}
