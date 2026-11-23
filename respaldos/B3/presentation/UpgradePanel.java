package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import src.domain.Side;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Pantalla para mejorar las estadísticas de cada jugador con sus puntos, antes
 * del partido. Muestra puntos, niveles y precios; las estadísticas salen de la fachada.
 */
public class UpgradePanel extends JPanel
{
    private static final int GAP = 10;
    private static final String TITLE = "Mejorar estadísticas";
    private static final String NOTE_PRICES = "Cada punto cuesta el doble: 500, 1000, 2000...";
    private static final String NOTE_WHEN = "Las mejoras se notan desde el siguiente partido.";
    private static final String ERROR_TITLE = "No se puede mejorar";
    private static final String MAX_TEXT = "nivel máximo";

    private POOBSoccer frame;
    private POOBSoccerGame game;
    private JPanel columns;

    /**
     * Crea la pantalla de mejoras.
     * @param frame ventana principal
     * @param game fachada del dominio
     */
    public UpgradePanel(POOBSoccer frame, POOBSoccerGame game){
        this.frame = frame;
        this.game = game;
        setLayout(new BorderLayout(GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        add(new JLabel(TITLE, SwingConstants.CENTER), BorderLayout.NORTH);
        columns = new JPanel(new GridLayout(1, 2, GAP, GAP));
        add(columns, BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    /** @return la nota de precios y el botón de volver */
    private JPanel createBottom(){
        JButton back = new JButton("Volver a la configuración");
        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.backToConfiguration();
            }
        });
        JPanel notes = new JPanel(new GridLayout(2, 1));
        notes.add(new JLabel(NOTE_PRICES));
        notes.add(new JLabel(NOTE_WHEN));
        JPanel bottom = new JPanel(new BorderLayout(GAP, GAP));
        bottom.add(notes, BorderLayout.CENTER);
        bottom.add(back, BorderLayout.EAST);
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
        column.setBorder(BorderFactory.createTitledBorder(title));
        column.add(new JLabel("Puntos: " + game.getPoints(s)));
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
        row.add(new JLabel(rowText(s, statName)), BorderLayout.CENTER);
        JButton button = new JButton("Mejorar");
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                upgrade(s, statName);
            }
        });
        row.add(button, BorderLayout.EAST);
        return row;
    }

    /** @return dos líneas, por ejemplo "Kick: nivel 3" y "siguiente punto: 2000" (HTML de JLabel) */
    private String rowText(Side s, String statName){
        String text = "<html>" + statName + ": nivel " + game.getStatLevel(s, statName) + "<br>";
        if (game.isMaxLevel(s, statName)){
            return text + MAX_TEXT + "</html>";
        }
        return text + "siguiente punto: " + game.getUpgradePrice(s, statName) + "</html>";
    }

    /**
     * Compra un punto de mejora; si no se puede, explica por qué.
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
