package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Configuración del partido antes de jugar: modalidad, personaje de cada jugador,
 * perfil de la máquina, tipo de partido con sus minutos o goles, y resolución.
 * Las opciones salen de la fachada, así que lo que se registre en el catálogo aparece solo.
 */
public class ConfigurationPanel extends JPanel
{
    private static final int GAP = 10;
    private static final int LIMIT_COLUMNS = 4;
    private static final String PVP_TEXT = "Jugador vs Jugador";
    private static final String PVM_TEXT = "Jugador vs Máquina";
    private static final String ERROR_TITLE = "No se puede iniciar el partido";
    private static final String NOT_A_NUMBER =
        "Los minutos o goles deben ser un número entero, por ejemplo 3. Escribiste: ";

    private POOBSoccer frame;
    private POOBSoccerGame game;
    private boolean vsMachine;
    private JLabel modeLabel;
    private JComboBox<String> leftCountry;
    private JComboBox<String> rightCountry;
    private JComboBox<String> profile;
    private JComboBox<String> rules;
    private JLabel limitLabel;
    private JTextField limit;
    private JComboBox<String> field;

    /**
     * Crea el panel de configuración.
     * @param frame ventana principal
     * @param game fachada del dominio
     */
    public ConfigurationPanel(POOBSoccer frame, POOBSoccerGame game){
        this.frame = frame;
        this.game = game;
        setLayout(new BorderLayout(GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        add(createOptions(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        updateLimit();
    }

    /** @return la grilla con una etiqueta y un selector por opción */
    private JPanel createOptions(){
        modeLabel = new JLabel();
        leftCountry = createCombo(game.getCountryIds());
        rightCountry = createCombo(game.getCountryIds());
        profile = createCombo(game.getProfileIds());
        rules = createCombo(game.getRuleIds());
        limitLabel = new JLabel();
        limit = new JTextField(LIMIT_COLUMNS);
        field = createCombo(game.getFieldIds());
        rules.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                updateLimit();
            }
        });
        JPanel options = new JPanel(new GridLayout(0, 2, GAP, GAP));
        options.add(new JLabel("Modalidad:"));
        options.add(modeLabel);
        options.add(new JLabel("Personaje del jugador 1 (izquierda):"));
        options.add(leftCountry);
        options.add(new JLabel("Personaje del jugador 2 (derecha):"));
        options.add(rightCountry);
        options.add(new JLabel("Perfil de la máquina (jugador 2):"));
        options.add(profile);
        options.add(new JLabel("Tipo de partido:"));
        options.add(rules);
        options.add(limitLabel);
        options.add(limit);
        options.add(new JLabel("Resolución de la cancha:"));
        options.add(field);
        return options;
    }

    /** @return los botones de volver y jugar */
    private JPanel createButtons(){
        JButton back = new JButton("Volver");
        JButton play = new JButton("Jugar");
        back.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.showMenu();
            }
        });
        play.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                play();
            }
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(back);
        buttons.add(play);
        return buttons;
    }

    /**
     * @param items opciones
     * @return un selector con esas opciones, en el mismo orden
     */
    private JComboBox<String> createCombo(ArrayList<String> items){
        JComboBox<String> box = new JComboBox<String>();
        for (int i = 0; i < items.size(); i++){
            box.addItem(items.get(i));
        }
        return box;
    }

    /**
     * Ajusta la pantalla a la modalidad: el perfil solo se escoge contra la máquina.
     * @param vsMachine true si el jugador 2 es la máquina
     */
    public void setMode(boolean vsMachine){
        this.vsMachine = vsMachine;
        profile.setEnabled(vsMachine);
        if (vsMachine){
            modeLabel.setText(PVM_TEXT);
        }
        else{
            modeLabel.setText(PVP_TEXT);
        }
    }

    /** Cambia la etiqueta y el valor propuesto del límite según el tipo de partido. */
    private void updateLimit(){
        String rulesId = (String) rules.getSelectedItem();
        try{
            limitLabel.setText("Número de " + game.getRuleLimitName(rulesId) + ":");
            limit.setText(String.valueOf(game.getRuleDefaultLimit(rulesId)));
        }
        catch (POOBSoccerException e){
            showError(e.getMessage());
        }
    }

    /** Lee lo escogido y pide iniciar el partido; si algo está mal, lo explica. */
    private void play(){
        int number;
        try{
            number = Integer.parseInt(limit.getText().trim());
        }
        catch (NumberFormatException e){
            showError(NOT_A_NUMBER + limit.getText());
            return;
        }
        String machine = null;
        if (vsMachine){
            machine = (String) profile.getSelectedItem();
        }
        try{
            frame.playMatch((String) leftCountry.getSelectedItem(), (String) rightCountry.getSelectedItem(),
                            machine, (String) rules.getSelectedItem(), number, (String) field.getSelectedItem());
        }
        catch (POOBSoccerException e){
            showError(e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error claro al usuario.
     * @param message explicación del error
     */
    private void showError(String message){
        JOptionPane.showMessageDialog(this, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
