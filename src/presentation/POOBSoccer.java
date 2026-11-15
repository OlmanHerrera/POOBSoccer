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
    private JTextField jtfScoreBox;
    
    public POOBSoccer(long period){
        jtfScoreBox = new JTextField();
        jtfScoreBox.setEditable(false);
        psp = new POOBSoccerPanel(this,period);
        
        Container c = getContentPane();
        c.add(psp, BorderLayout.CENTER);
        c.add(jtfScoreBox,BorderLayout.SOUTH);
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
    
    
    
    public void addComponentToPane(Container content ){
   
    }
    public void windowActivated(WindowEvent e){
        psp.resumeGame();
        
    }
    
    public void windowDeactivated(WindowEvent e){
        psp.pauseGame();
        
    }
    
    public void windowDeiconified(WindowEvent e){
        psp.resumeGame();
    }
    
    public void windowIconified(WindowEvent e ){
        psp.pauseGame();
    }
    
    public void windowClosing(WindowEvent e){
        psp.stopGame();
        
    }
    public void windowClosed(WindowEvent e ){
    }
    public void windowOpened(WindowEvent e ){
 
    }

    
    public void setScoreBoard(int s1, int s2){
        jtfScoreBox.setText("Player1 : "+ s1+ " : " + " Player 2 : " +s2  );
    }
    


}