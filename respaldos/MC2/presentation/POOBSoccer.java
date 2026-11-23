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
    private POOBSoccerPanel psp;
    private MainMenuPanel menu;
    private long period;
    private CardLayout cardLayout;
    private JPanel container;
    
    public POOBSoccer(long period){
        this.period = period;
        setSize(500,400);
        
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        MainMenuPanel mainMenu = new MainMenuPanel(this);

        container.add(mainMenu, "MENU");

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

        if (psp == null) {
            psp = new POOBSoccerPanel(this,period);
            container.add(psp, "GAME");
            container.revalidate();
        }
        cardLayout.show(container, "GAME");
        psp.requestFocusInWindow();
    }
    
    
    
    public void addComponentToPane(Container content ){
   
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