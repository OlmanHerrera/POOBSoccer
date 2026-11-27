package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.Side;
import src.domain.Action;
import src.domain.MatchObserver;
import src.domain.GoalEvent;
import src.domain.POOBSoccerException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Panel del juego: en cada ciclo actualiza, dibuja y duerme lo que falte del período.
 */
public class POOBSoccerPanel extends JPanel implements Runnable, MatchObserver{
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 400;
    private static final long NANOS_PER_SECOND = 1000000000L;
    private static final long NANOS_PER_MILLI = 1000000L;
    private static final long MAX_STATS_INTERVAL = NANOS_PER_SECOND;
    private static final int MAX_FRAME_SKIPS = 5;
    private static final int NO_DELAYS_PER_YIELD = 10;
    private static final int NUM_FPS = 10;
    private static final Font STATS_FONT = new Font("SansSerif", Font.BOLD, 10);
    private static final int FPS_X = 8;
    private static final int FPS_BOTTOM_MARGIN = 6;
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 22);
    private static final Font HINT_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Color MESSAGE_COLOR = new Color(0, 0, 0, 170);
    private static final int TITLE_OFFSET = 35;
    private static final int TITLE_AREA = 62;
    private static final int LINE_STEP = 20;
    private static final String PAUSE_TITLE = "PAUSA";
    private static final String[] PAUSE_LINES = {"P: continuar   ESC: terminar"};
    private static final String OVER_HINT = "ESC: menú";
    private static final String PLAYER_1 = "Jugador 1";
    private static final String PLAYER_2 = "Jugador 2";
    private static final String QUIT_MESSAGE = "El partido termina sin ganador ni puntos.";
    private static final String QUIT_TITLE = "Terminar partido";
    private static final String[] QUIT_OPTIONS = {"Terminar", "Seguir"};
    private static final int QUIT_CHOICE = 0;

    private POOBSoccer frame;
    private POOBSoccerGame game;
    private Hud hud;
    private Controls controls;
    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile int fieldWidth = DEFAULT_WIDTH;
    private volatile int fieldHeight = DEFAULT_HEIGHT;
    private Graphics dbg;
    private Image dbImage = null;
    private long period;
    private long statsInterval = 0L;
    private long prevStatsTime;
    private long totalElapsedTime = 0L;
    private int frameCount = 0;
    private int framesSkipped = 0;
    private long totalFramesSkipped = 0L;
    private int statsCount = 0;
    private double[] fpsStore;
    private double[] upsStore;
    private double averageFPS = 0.0;
    private double averageUPS = 0.0;
    private DecimalFormat df = new DecimalFormat("0.####");
    private String endTitle = "";
    private String[] endLines = {OVER_HINT};

    /**
     * Crea el panel del juego.
     * @param frame ventana principal
     * @param game juego
     * @param period período del ciclo en nanosegundos
     * @throws POOBSoccerException si controles.txt no existe o tiene una línea mal escrita
     */
    public POOBSoccerPanel(POOBSoccer frame, POOBSoccerGame game, long period) throws POOBSoccerException{
        try{
            controls = new Controls();
        }
        catch (POOBSoccerException e){
            game.recordError(e);
            throw e;
        }
        setBackground(Color.white);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.frame = frame;
        this.game = game;
        this.period = period;
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                processKey(e);
            }
        });
        game.addObserver(this);
        hud = new Hud();
        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++){
            fpsStore[i] = 0.0;
            upsStore[i] = 0.0;
        }
    }

    /** Arranca el ciclo cuando el panel ya está dentro de la ventana. */
    public void addNotify(){
        super.addNotify();
        startGame();
    }

    /** Arranca el hilo del ciclo de animación. */
    public void startGame(){
        if (animator == null || !running){
            animator = new Thread(this);
            animator.start();
        }
    }

    /** Detiene el ciclo; al salir de él se cierra la aplicación. */
    public void stopGame(){
        running = false;
    }

    /** Repite: actualiza, dibuja y duerme lo que falte del período. */
    public void run(){
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;
        beforeTime = System.nanoTime();
        prevStatsTime = beforeTime;
        running = true;
        while (running){
            gameUpdate();
            gameRender();
            paintScreen();
            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;
            if (sleepTime > 0){
                try{
                    Thread.sleep(sleepTime / NANOS_PER_MILLI);
                }
                catch (InterruptedException ex){
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            }
            else{
                excess -= sleepTime;
                overSleepTime = 0L;
                if (++noDelays >= NO_DELAYS_PER_YIELD){
                    Thread.yield();
                    noDelays = 0;
                }
            }
            beforeTime = System.nanoTime();
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)){
                excess -= period;
                gameUpdate();
                skips++;
            }
            framesSkipped += skips;
            storeStats();
        }
        printStats();
        System.exit(0);
    }

    private void storeStats(){
        frameCount++;
        statsInterval += period;
        if (statsInterval >= MAX_STATS_INTERVAL){
            long timeNow = System.nanoTime();
            long realElapsedTime = timeNow - prevStatsTime;
            totalElapsedTime += realElapsedTime;
            double timingError = (double) ((period - statsInterval) / statsInterval) * 100.0;
            totalFramesSkipped += framesSkipped;
            double actualFPS = 0;
            double actualUPS = 0;
            if (totalElapsedTime > 0){
                actualFPS = ((double) frameCount / totalElapsedTime) * NANOS_PER_SECOND;
                actualUPS = ((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * NANOS_PER_SECOND;
            }
            fpsStore[statsCount % NUM_FPS] = actualFPS;
            upsStore[statsCount % NUM_FPS] = actualUPS;
            statsCount += 1;
            double totalFPS = 0;
            double totalUPS = 0;
            for (int i = 0; i < NUM_FPS; i++){
                totalFPS += fpsStore[i];
                totalUPS += upsStore[i];
            }
            if (statsCount < NUM_FPS){
                averageFPS = totalFPS / statsCount;
                averageUPS = totalUPS / statsCount;
            }
            else{
                averageFPS = totalFPS / NUM_FPS;
                averageUPS = totalUPS / NUM_FPS;
            }
            System.out.println(df.format((double) statsInterval / NANOS_PER_SECOND) + " "
                               + df.format((double) realElapsedTime / NANOS_PER_SECOND) + "s "
                               + df.format(timingError) + "% "
                               + frameCount + "c "
                               + framesSkipped + "/" + totalFramesSkipped + " skip; "
                               + df.format(actualFPS) + " " + df.format(averageFPS) + " afps; "
                               + df.format(actualUPS) + " " + df.format(averageUPS) + " aups");
            framesSkipped = 0;
            statsInterval = 0L;
            prevStatsTime = timeNow;
        }
    }

    private void printStats(){
        System.out.println("Frame Count/Loss: " + frameCount + " / " + totalFramesSkipped);
        System.out.println("Average FPS: " + df.format(averageFPS));
        System.out.println("Average UPS: " + df.format(averageUPS));
    }

    private void gameUpdate(){
        if (!paused){
            game.update();
        }
    }

    private void gameRender(){
        if (dbImage == null){
            dbImage = createImage(fieldWidth, fieldHeight);
            if (dbImage == null){
                System.out.println("dbImage is null");
                return;
            }
            dbg = dbImage.getGraphics();
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
        dbg.setFont(STATS_FONT);
        dbg.drawString("Average FPS/UPS: " + df.format(averageFPS) + ", " + df.format(averageUPS),
                       FPS_X, fieldHeight - FPS_BOTTOM_MARGIN);
    }

    /**
     * Dibuja un aviso en el centro de la cancha.
     * @param title texto grande
     * @param lines textos pequeños debajo del título
     */
    private void drawMessage(String title, String[] lines){
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
     * El marcador ya se ve en la barra superior.
     * @param e lado que marcó
     */
    public void goalScored(GoalEvent e){
    }

    /**
     * Prepara el aviso final con el ganador y los puntos de cada jugador.
     * @param e lado ganador
     */
    public void matchEnded(GoalEvent e){
        Side winner = e.getScorer();
        endTitle = "Ganó " + playerName(winner) + ": " + game.getCountryName(winner);
        endLines = new String[] {pointsText(Side.LEFT), pointsText(Side.RIGHT), OVER_HINT};
    }

    private String playerName(Side s){
        if (s == Side.LEFT){
            return PLAYER_1;
        }
        return PLAYER_2;
    }

    private String pointsText(Side s){
        return playerName(s) + ": +" + game.getPointsGained(s) + " puntos, total " + game.getPoints(s);
    }

    private void paintScreen(){
        if (!isShowing()){
            return;
        }
        Graphics g;
        try{
            g = this.getGraphics();
            if ((g != null) && (dbImage != null)){
                g.drawImage(dbImage, 0, 0, null);
                Toolkit.getDefaultToolkit().sync();
                g.dispose();
            }
        }
        catch (Exception e){
            System.out.println("Graphics context error: " + e);
            game.recordError(e);
        }
    }

    /**
     * Ajusta el panel al tamaño de la cancha del partido.
     * @param width ancho de la cancha
     * @param height alto de la cancha
     */
    public void setFieldSize(int width, int height){
        fieldWidth = width;
        fieldHeight = height;
        setPreferredSize(new Dimension(width, height));
        dbImage = null;
    }

    /** Detiene las actualizaciones mientras la ventana no está activa. */
    public void pauseGame(){
        paused = true;
    }

    /** Reanuda las actualizaciones. */
    public void resumeGame(){
        paused = false;
    }

    private void processKey(KeyEvent e){
        int keyCode = e.getKeyCode();
        if (keyCode == Controls.EXIT_KEY){
            escapePressed();
            return;
        }
        if (keyCode == Controls.PAUSE_KEY){
            togglePause();
            return;
        }
        if (paused || game.isPaused() || game.isMatchOver()){
            return;
        }
        Action left = controls.getAction(Side.LEFT, keyCode);
        if (left != null){
            game.perform(Side.LEFT, left);
        }
        Action right = controls.getAction(Side.RIGHT, keyCode);
        if (right != null){
            game.perform(Side.RIGHT, right);
        }
    }

    private void togglePause(){
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

    /** ESC pide confirmación para terminar el partido, o vuelve al menú si ya terminó. */
    private void escapePressed(){
        if (game.isMatchOver()){
            frame.showMenu();
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
     * Pregunta si se termina el partido; si es así, lo termina y vuelve al menú.
     * @return true si el partido se terminó
     */
    private boolean askQuit(){
        int answer = JOptionPane.showOptionDialog(this, QUIT_MESSAGE, QUIT_TITLE, JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.WARNING_MESSAGE, null, QUIT_OPTIONS, QUIT_OPTIONS[1]);
        if (answer != QUIT_CHOICE){
            return false;
        }
        game.endMatch();
        frame.showMenu();
        return true;
    }
}
