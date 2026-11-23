package src.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel(POOBSoccer frame) {
        setPreferredSize(new Dimension(500,400));
        setLayout(new GridBagLayout());

        JButton startButton = new JButton("INICIAR PARTIDA");

        startButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e){
                frame.startGame();
            }
        });

        add(startButton);
    }
}
