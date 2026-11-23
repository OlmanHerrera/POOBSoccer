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
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);

    /**
     * Crea el menú.
     * @param frame ventana principal
     */
    public MainMenuPanel(POOBSoccer frame) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new GridBagLayout());

        JPanel column = new JPanel(new GridLayout(ROWS, 1, GAP, GAP));
        JLabel title = new JLabel(TITLE, SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        JButton pvpButton = new JButton("Jugador vs Jugador");
        JButton pvmButton = new JButton("Jugador vs Máquina");
        JButton exitButton = new JButton("Salir");
        column.add(title);
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
