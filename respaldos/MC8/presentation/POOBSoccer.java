package src.presentation;
import src.domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.Container;

public class POOBSoccer extends JFrame implements WindowListener{
    private static final int DEFAULT_FPS = 100;
    // partido por defecto: temporal, se corrige en MC8 con la configuración
    private static final String DEFAULT_LEFT_COUNTRY = "Corea del Sur";
    private static final String DEFAULT_RIGHT_COUNTRY = "Camerún";
    private static final String DEFAULT_PROFILE = "Ofensiva";
    private static final String DEFAULT_RULES = "Por goles";
    private static final int DEFAULT_LIMIT = 5;
    private static final String DEFAULT_FIELD = "500x400";
    private static final String ERROR_TITLE = "POOBSoccer";
    private POOBSoccerPanel psp;
    private MainMenuPanel menu;
    private POOBSoccerGame game;
    private long period;
    private CardLayout cardLayout;
    private JPanel container;

    public POOBSoccer(long period){
        this.period = period;
        game = new POOBSoccerGame(period);
        setSize(500,400);
        
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        menu = new MainMenuPanel(this);

        container.add(menu, "MENU");

        add(container);

        cardLayout.show(container, "MENU");
        
        addWindowListener(this);
        

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false); 
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
    
    public static void main(String[] args){
        int fps = DEFAULT_FPS;
        
        if (args.length !=0){
            fps = Integer.parseInt(args[0]);
        }
        long period = (long) (1000/fps);
        new POOBSoccer(period*1000000L);
    
        
    }
    
    public void startGame() {
        try{
            game.startMatch(DEFAULT_LEFT_COUNTRY, DEFAULT_RIGHT_COUNTRY, DEFAULT_PROFILE,
                            DEFAULT_RULES, DEFAULT_LIMIT, DEFAULT_FIELD);
        }
        catch (POOBSoccerException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (psp == null) {
            psp = new POOBSoccerPanel(this, game, period);
            container.add(psp, "GAME");
            container.revalidate();
        }
        cardLayout.show(container, "GAME");
        psp.requestFocusInWindow();
    }

    /** Vuelve al menú principal. */
    public void showMenu() {
        cardLayout.show(container, "MENU");
    }
    
    
    
    public void windowActivated(WindowEvent e){
        if (psp != null){
            psp.resumeGame();
        }
    }

    public void windowDeactivated(WindowEvent e){
        if (psp != null){
            psp.pauseGame();
        }
    }

    public void windowDeiconified(WindowEvent e){
        if (psp != null){
            psp.resumeGame();
        }
    }

    public void windowIconified(WindowEvent e ){
        if (psp != null){
            psp.pauseGame();
        }
    }

    public void windowClosing(WindowEvent e){
        if (psp != null){
            psp.stopGame();
        }
    }
    public void windowClosed(WindowEvent e ){
    }
    public void windowOpened(WindowEvent e ){
 
    }

    
    


}