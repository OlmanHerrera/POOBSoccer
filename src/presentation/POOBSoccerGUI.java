package src.presentation;
import src.domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class POOBSoccerGUI extends JFrame{
    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final Dimension frameSize = new Dimension(screenSize.width/2, screenSize.height/2);
    private POOBSoccerPanel gp;

    public static void main(String[] args){
        POOBSoccerGUI sketch = new POOBSoccerGUI();
    }
    public POOBSoccerGUI(){
        prepareView();
        prepareController();
        setVisible(true);
    }

    public void prepareView(){
        setTitle("POOBSoccer");
        gp = new POOBSoccerPanel();
        add(gp);
        pack();
        setLocation((screenSize.width-frameSize.width)/2,(screenSize.height-frameSize.height)/2);
        
    }

    public void prepareController(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}