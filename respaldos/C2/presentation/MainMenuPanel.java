package src.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Menú principal: escoger la modalidad del juego o salir.
 */
public class MainMenuPanel extends JPanel {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static final int ROWS = 4;
    private static final int GAP = 12;
    private static final String TITLE = "POOBSoccer";

    /**
     * Crea el menú.
     * @param frame ventana principal
     */
    public MainMenuPanel(POOBSoccer frame) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new GridBagLayout());
        setBackground(MenuStyle.BACKGROUND);

        JPanel column = new JPanel(new GridLayout(ROWS, 1, GAP, GAP));
        column.setBackground(MenuStyle.BACKGROUND);
        JButton pvpButton = MenuStyle.createButton("Jugador vs Jugador");
        JButton pvmButton = MenuStyle.createButton("Jugador vs Máquina");
        JButton exitButton = MenuStyle.createButton("Salir");
        column.add(MenuStyle.createTitle(TITLE, MenuStyle.TITLE_FONT));
        column.add(pvpButton);
        column.add(pvmButton);
        column.add(exitButton);
        add(column);

        pvpButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.showConfiguration(false);
            }
        });
        pvmButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.showConfiguration(true);
            }
        });
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.exitGame();
            }
        });
    }
}
