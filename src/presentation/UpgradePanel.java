package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import src.domain.Side;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Pantalla para mejorar las estadísticas de cada jugador con sus puntos.
 */
public class UpgradePanel extends JPanel{
    private static final int GAP = 10;
    private static final String TITLE = "Mejoras";
    private static final String ERROR_TITLE = "Mejoras";
    private static final String MAX_TEXT = "Nivel máximo";

    private POOBSoccer frame;
    private POOBSoccerGame game;
    private JPanel columns;

    /**
     * Crea la pantalla de mejoras.
     * @param frame ventana principal
     * @param game juego
     */
    public UpgradePanel(POOBSoccer frame, POOBSoccerGame game){
        this.frame = frame;
        this.game = game;
        setLayout(new BorderLayout(GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        setBackground(MenuStyle.BACKGROUND);
        add(MenuStyle.createTitle(TITLE, MenuStyle.SUBTITLE_FONT), BorderLayout.NORTH);
        columns = new JPanel(new GridLayout(1, 2, GAP, GAP));
        columns.setBackground(MenuStyle.BACKGROUND);
        add(columns, BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    /** @return el botón de volver */
    private JPanel createBottom(){
        JButton back = MenuStyle.createButton("Volver");
        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.backToConfiguration();
            }
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(MenuStyle.BACKGROUND);
        bottom.add(back);
        return bottom;
    }

    /** Vuelve a armar las dos columnas con los puntos, niveles y precios actuales. */
    public void refresh(){
        columns.removeAll();
        columns.add(createColumn(Side.LEFT, "Jugador 1"));
        columns.add(createColumn(Side.RIGHT, "Jugador 2"));
        columns.revalidate();
        columns.repaint();
    }

    /**
     * @param s lado del jugador
     * @param title nombre del jugador
     * @return una columna con sus puntos y una fila por estadística
     */
    private JPanel createColumn(Side s, String title){
        JPanel column = new JPanel(new GridLayout(0, 1, GAP, GAP));
        column.setBackground(MenuStyle.BACKGROUND);
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(MenuStyle.TITLE);
        border.setTitleFont(MenuStyle.TEXT_FONT);
        column.setBorder(border);
        column.add(MenuStyle.createLabel("Puntos: " + game.getPoints(s)));
        ArrayList<String> names = game.getStatNames();
        for (int i = 0; i < names.size(); i++){
            column.add(createRow(s, names.get(i)));
        }
        return column;
    }

    /**
     * @param s lado del jugador
     * @param statName estadística
     * @return una fila con el nivel, el precio y el botón para mejorar
     */
    private JPanel createRow(final Side s, final String statName){
        JPanel row = new JPanel(new BorderLayout(GAP, GAP));
        row.setBackground(MenuStyle.BACKGROUND);
        row.add(MenuStyle.createLabel(rowText(s, statName)), BorderLayout.CENTER);
        JButton button = MenuStyle.createButton("Mejorar");
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                upgrade(s, statName);
            }
        });
        row.add(button, BorderLayout.EAST);
        return row;
    }

    /** @return el nivel y el precio de una estadística en dos líneas */
    private String rowText(Side s, String statName){
        String text = "<html>" + statName + ": nivel " + game.getStatLevel(s, statName) + "<br>";
        if (game.isMaxLevel(s, statName)){
            return text + MAX_TEXT + "</html>";
        }
        return text + "Precio: " + game.getUpgradePrice(s, statName) + "</html>";
    }

    /**
     * Compra un punto de mejora; si no se puede, muestra el motivo.
     * @param s lado del jugador
     * @param statName estadística
     */
    private void upgrade(Side s, String statName){
        try{
            game.upgrade(s, statName);
            refresh();
        }
        catch (POOBSoccerException e){
            game.recordError(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
}
