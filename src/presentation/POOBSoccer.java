package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Ventana principal con el menú Archivo y las pantallas del juego.
 */
public class POOBSoccer extends JFrame implements WindowListener{
    private static final int DEFAULT_FPS = 100;
    private static final String MENU = "MENU";
    private static final String CONFIG = "CONFIG";
    private static final String UPGRADE = "UPGRADE";
    private static final String GAME = "GAME";
    private static final String MATCH_FILES = "Partidos";
    private static final String PROGRESS_FILES = "Progreso";
    private static final String FILE_ERROR_TITLE = "Archivo";
    private static final String PROGRESS_ERROR = "No se pudo cargar el progreso: ";
    private static final int MILLIS_PER_SECOND = 1000;
    private static final long NANOS_PER_MILLI = 1000000L;

    private POOBSoccerGame game;
    private MainMenuPanel menu;
    private ConfigurationPanel configuration;
    private UpgradePanel upgrades;
    private POOBSoccerPanel gamePanel;
    private CardLayout cardLayout;
    private JPanel container;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem importItem;
    private JMenuItem exportItem;
    private JMenuItem exitItem;

    /**
     * Crea la ventana con sus pantallas y recupera el progreso de la ejecución anterior.
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccer(long period){
        game = new POOBSoccerGame(period);
        try{
            MenuStyle.applyColors();
        }
        catch (UnsupportedLookAndFeelException e){
            game.recordError(e);
        }
        loadProgress();
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        menu = new MainMenuPanel(this);
        configuration = new ConfigurationPanel(this, game);
        upgrades = new UpgradePanel(this, game);
        gamePanel = new POOBSoccerPanel(this, game, period);

        container.add(menu, MENU);
        container.add(configuration, CONFIG);
        container.add(upgrades, UPGRADE);
        container.add(gamePanel, GAME);
        add(container);
        cardLayout.show(container, MENU);
        prepareViewMenu();
        prepareControllerMenu();

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
        if (args.length != 0){
            fps = Integer.parseInt(args[0]);
        }
        long period = (long) (MILLIS_PER_SECOND / fps);
        new POOBSoccer(period * NANOS_PER_MILLI);
    }

    /** Arma el menú Archivo con sus separadores. */
    private void prepareViewMenu(){
        newItem = new JMenuItem("Nuevo");
        openItem = new JMenuItem("Abrir");
        saveItem = new JMenuItem("Guardar como");
        importItem = new JMenuItem("Importar");
        exportItem = new JMenuItem("Exportar como");
        exitItem = new JMenuItem("Salir");
        JMenu file = new JMenu("Archivo");
        file.add(newItem);
        file.addSeparator();
        file.add(openItem);
        file.add(saveItem);
        file.addSeparator();
        file.add(importItem);
        file.add(exportItem);
        file.addSeparator();
        file.add(exitItem);
        JMenuBar bar = new JMenuBar();
        bar.setBackground(MenuStyle.BACKGROUND);
        file.setForeground(MenuStyle.TEXT);
        bar.add(file);
        setJMenuBar(bar);
    }

    /** Conecta cada opción del menú Archivo con su método. */
    private void prepareControllerMenu(){
        newItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionNew();
            }
        });
        openItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionOpen();
            }
        });
        saveItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionSaveAs();
            }
        });
        importItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionImport();
            }
        });
        exportItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionExportAs();
            }
        });
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                optionExit();
            }
        });
    }

    /** Nuevo: termina el partido actual sin puntos y vuelve al menú principal. */
    private void optionNew(){
        game.endMatch();
        showMenu();
    }

    /** Abrir: escoge un partido guardado y lo continúa donde quedó. */
    private void optionOpen(){
        game.pause();
        JFileChooser chooser = createChooser(MATCH_FILES, POOBSoccerGame.MATCH_EXTENSION);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            game.open(chooser.getSelectedFile());
            showGame();
        }
        catch (POOBSoccerException e){
            showFileError(e);
        }
    }

    /** Guardar como: guarda el partido en curso; queda en pausa. */
    private void optionSaveAs(){
        game.pause();
        JFileChooser chooser = createChooser(MATCH_FILES, POOBSoccerGame.MATCH_EXTENSION);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            game.saveAs(withExtension(chooser.getSelectedFile(), POOBSoccerGame.MATCH_EXTENSION));
        }
        catch (POOBSoccerException e){
            showFileError(e);
        }
    }

    /** Importar: lee el progreso de los jugadores de un archivo de texto. */
    private void optionImport(){
        game.pause();
        JFileChooser chooser = createChooser(PROGRESS_FILES, POOBSoccerGame.PROGRESS_EXTENSION);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            game.import_(chooser.getSelectedFile());
            upgrades.refresh();
        }
        catch (POOBSoccerException e){
            showFileError(e);
        }
    }

    /** Exportar como: escribe el progreso de los jugadores en un archivo de texto. */
    private void optionExportAs(){
        game.pause();
        JFileChooser chooser = createChooser(PROGRESS_FILES, POOBSoccerGame.PROGRESS_EXTENSION);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            game.exportAs(withExtension(chooser.getSelectedFile(), POOBSoccerGame.PROGRESS_EXTENSION));
        }
        catch (POOBSoccerException e){
            showFileError(e);
        }
    }

    /** Salir: guarda el progreso y cierra el juego. */
    private void optionExit(){
        exitGame();
    }

    /**
     * @param description nombre de los archivos que se muestran
     * @param extension extensión con punto, por ejemplo ".dat"
     * @return selector de archivos que muestra solo esa extensión
     */
    private JFileChooser createChooser(String description, String extension){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension.substring(1)));
        return chooser;
    }

    /**
     * @param file archivo escogido
     * @param extension extensión con punto
     * @return el mismo archivo, con la extensión agregada si no la tenía
     */
    private File withExtension(File file, String extension){
        if (file.getName().endsWith(extension)){
            return file;
        }
        return new File(file.getPath() + extension);
    }

    /**
     * Deja el error en el registro y muestra su mensaje.
     * @param e error de archivo
     */
    private void showFileError(POOBSoccerException e){
        game.recordError(e);
        JOptionPane.showMessageDialog(this, e.getMessage(), FILE_ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /** Recupera el progreso guardado; si está dañado, avisa y se empieza de cero. */
    private void loadProgress(){
        try{
            game.loadProgress();
        }
        catch (POOBSoccerException e){
            game.recordError(e);
            JOptionPane.showMessageDialog(null, PROGRESS_ERROR + e.getMessage(), FILE_ERROR_TITLE,
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Vuelve al menú principal. */
    public void showMenu(){
        cardLayout.show(container, MENU);
    }

    /**
     * Muestra la configuración del partido.
     * @param vsMachine true si el jugador 2 es la máquina
     */
    public void showConfiguration(boolean vsMachine){
        configuration.setMode(vsMachine);
        cardLayout.show(container, CONFIG);
    }

    /** Muestra la pantalla de mejora de estadísticas con los datos actuales. */
    public void showUpgrades(){
        upgrades.refresh();
        cardLayout.show(container, UPGRADE);
    }

    /** Vuelve a la configuración, con la modalidad que ya estaba escogida. */
    public void backToConfiguration(){
        cardLayout.show(container, CONFIG);
    }

    /**
     * Inicia un partido y lo muestra.
     * @param leftCountry personaje del jugador 1
     * @param rightCountry personaje del jugador 2
     * @param profile perfil de la máquina, o null si es jugador contra jugador
     * @param rules tipo de partido
     * @param limit minutos o goles
     * @param field resolución de la cancha
     * @throws POOBSoccerException si algo de lo escogido no es válido
     */
    public void playMatch(String leftCountry, String rightCountry, String profile,
                          String rules, int limit, String field) throws POOBSoccerException{
        game.startMatch(leftCountry, rightCountry, profile, rules, limit, field);
        showGame();
    }

    /** Ajusta la ventana a la resolución del partido actual y muestra el juego. */
    private void showGame(){
        gamePanel.setFieldSize(game.getFieldWidth(), game.getFieldHeight());
        pack();
        setLocationRelativeTo(null);
        cardLayout.show(container, GAME);
        gamePanel.requestFocusInWindow();
    }

    /** Guarda el progreso y cierra el juego. */
    public void exitGame(){
        try{
            game.saveProgress();
        }
        catch (POOBSoccerException e){
            showFileError(e);
        }
        gamePanel.stopGame();
    }

    /**
     * Reanuda el juego cuando la ventana se activa.
     * @param e evento de la ventana
     */
    public void windowActivated(WindowEvent e){
        gamePanel.resumeGame();
    }

    /**
     * Pausa el juego cuando la ventana se desactiva.
     * @param e evento de la ventana
     */
    public void windowDeactivated(WindowEvent e){
        gamePanel.pauseGame();
    }

    /**
     * Reanuda el juego cuando la ventana se restaura.
     * @param e evento de la ventana
     */
    public void windowDeiconified(WindowEvent e){
        gamePanel.resumeGame();
    }

    /**
     * Pausa el juego cuando la ventana se minimiza.
     * @param e evento de la ventana
     */
    public void windowIconified(WindowEvent e){
        gamePanel.pauseGame();
    }

    /**
     * Guarda el progreso y cierra el juego cuando se cierra la ventana.
     * @param e evento de la ventana
     */
    public void windowClosing(WindowEvent e){
        exitGame();
    }

    /**
     * No hace nada cuando la ventana ya se cerró.
     * @param e evento de la ventana
     */
    public void windowClosed(WindowEvent e){
    }

    /**
     * No hace nada cuando la ventana se abre.
     * @param e evento de la ventana
     */
    public void windowOpened(WindowEvent e){
    }
}
