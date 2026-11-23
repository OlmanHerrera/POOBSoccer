package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Ventana principal (KGPJ): con un CardLayout muestra el menú, la configuración
 * y el juego. Crea la fachada del dominio y se la pasa a los paneles.
 */
public class POOBSoccer extends JFrame implements WindowListener{
    private static final int DEFAULT_FPS = 100;
    private static final String MENU = "MENU";
    private static final String CONFIG = "CONFIG";
    private static final String GAME = "GAME";
    private POOBSoccerGame game;
    private MainMenuPanel menu;
    private ConfigurationPanel configuration;
    private POOBSoccerPanel psp;
    private CardLayout cardLayout;
    private JPanel container;

    /**
     * Crea la ventana con sus pantallas.
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccer(long period){
        game = new POOBSoccerGame(period);
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        menu = new MainMenuPanel(this);
        configuration = new ConfigurationPanel(this, game);
        psp = new POOBSoccerPanel(this, game, period);

        container.add(menu, MENU);
        container.add(configuration, CONFIG);
        container.add(psp, GAME);
        add(container);
        cardLayout.show(container, MENU);

        addWindowListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    /**
     * Arranca el juego.
     * @param args opcional: cuadros por segundo
     */
    public static void main(String[] args){
        int fps = DEFAULT_FPS;

        if (args.length !=0){
            fps = Integer.parseInt(args[0]);
        }
        long period = (long) (1000/fps);
        new POOBSoccer(period*1000000L);
    }

    /** Vuelve al menú principal. */
    public void showMenu() {
        cardLayout.show(container, MENU);
    }

    /**
     * Muestra la configuración del partido.
     * @param vsMachine true si el jugador 2 es la máquina
     */
    public void showConfiguration(boolean vsMachine) {
        configuration.setMode(vsMachine);
        cardLayout.show(container, CONFIG);
    }

    /**
     * Inicia un partido, ajusta la ventana a la resolución escogida y muestra el juego.
     * @param leftCountry personaje del jugador 1
     * @param rightCountry personaje del jugador 2
     * @param profile perfil de la máquina, o null si es jugador contra jugador
     * @param rules tipo de partido
     * @param limit minutos o goles
     * @param field resolución de la cancha
     * @throws POOBSoccerException si algo de lo escogido no es válido
     */
    public void playMatch(String leftCountry, String rightCountry, String profile,
                          String rules, int limit, String field) throws POOBSoccerException {
        game.startMatch(leftCountry, rightCountry, profile, rules, limit, field);
        psp.setFieldSize(game.getFieldWidth(), game.getFieldHeight());
        pack();
        setLocationRelativeTo(null);
        cardLayout.show(container, GAME);
        psp.requestFocusInWindow();
    }

    /** Cierra el juego: detiene el bucle, que al terminar cierra la aplicación. */
    public void exitGame() {
        psp.stopGame();
    }

    /** @param e la ventana se activó: el bucle sigue */
    public void windowActivated(WindowEvent e){
        psp.resumeGame();
    }

    /** @param e la ventana se desactivó: el bucle se pausa */
    public void windowDeactivated(WindowEvent e){
        psp.pauseGame();
    }

    /** @param e la ventana se restauró: el bucle sigue */
    public void windowDeiconified(WindowEvent e){
        psp.resumeGame();
    }

    /** @param e la ventana se minimizó: el bucle se pausa */
    public void windowIconified(WindowEvent e ){
        psp.pauseGame();
    }

    /** @param e la ventana se está cerrando: se detiene el bucle */
    public void windowClosing(WindowEvent e){
        psp.stopGame();
    }

    /** @param e la ventana se cerró */
    public void windowClosed(WindowEvent e ){
    }

    /** @param e la ventana se abrió */
    public void windowOpened(WindowEvent e ){
    }
}
