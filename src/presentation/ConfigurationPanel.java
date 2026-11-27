package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.POOBSoccerException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Pantalla para configurar el partido antes de jugar.
 */
public class ConfigurationPanel extends JPanel{
    private static final int GAP = 10;
    private static final int LIMIT_COLUMNS = 4;
    private static final String TITLE = "Configuración";
    private static final String HUMAN = "Humano";
    private static final String HUMAN_TEXT = "Jugador";
    private static final String MACHINE_TEXT = "Máquina";
    private static final String VERSUS = " vs ";
    private static final int HUMAN_INDEX = 0;
    private static final int FIRST_PROFILE_INDEX = 1;
    private static final String ERROR_TITLE = "Configuración";
    private static final String NOT_A_NUMBER = "Los minutos o goles deben ser un número entero: ";

    private POOBSoccer frame;
    private POOBSoccerGame game;
    private JLabel modeLabel;
    private JComboBox<String> leftCountry;
    private JComboBox<String> rightCountry;
    private JComboBox<String> leftControl;
    private JComboBox<String> rightControl;
    private JComboBox<String> rules;
    private JLabel limitLabel;
    private JTextField limit;
    private JComboBox<String> field;

    /**
     * Crea el panel de configuración.
     * @param frame ventana principal
     * @param game juego
     */
    public ConfigurationPanel(POOBSoccer frame, POOBSoccerGame game){
        this.frame = frame;
        this.game = game;
        setLayout(new BorderLayout(GAP, GAP));
        setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        setBackground(MenuStyle.BACKGROUND);
        add(MenuStyle.createTitle(TITLE, MenuStyle.SUBTITLE_FONT), BorderLayout.NORTH);
        add(createOptions(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        updateLimit();
    }

    /** @return la grilla con una etiqueta y un selector por opción */
    private JPanel createOptions(){
        modeLabel = MenuStyle.createLabel("");
        leftCountry = createCombo(game.getCountryIds());
        rightCountry = createCombo(game.getCountryIds());
        leftControl = createControlCombo();
        rightControl = createControlCombo();
        rules = createCombo(game.getRuleIds());
        limitLabel = MenuStyle.createLabel("");
        limit = new JTextField(LIMIT_COLUMNS);
        MenuStyle.styleField(limit);
        field = createCombo(game.getFieldIds());
        rules.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                updateLimit();
            }
        });
        ActionListener modeListener = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                updateMode();
            }
        };
        leftControl.addActionListener(modeListener);
        rightControl.addActionListener(modeListener);
        JPanel options = new JPanel(new GridLayout(0, 2, GAP, GAP));
        options.setBackground(MenuStyle.BACKGROUND);
        options.add(MenuStyle.createLabel("Modalidad"));
        options.add(modeLabel);
        options.add(MenuStyle.createLabel("Personaje 1"));
        options.add(leftCountry);
        options.add(MenuStyle.createLabel("Control 1"));
        options.add(leftControl);
        options.add(MenuStyle.createLabel("Personaje 2"));
        options.add(rightCountry);
        options.add(MenuStyle.createLabel("Control 2"));
        options.add(rightControl);
        options.add(MenuStyle.createLabel("Tipo de partido"));
        options.add(rules);
        options.add(limitLabel);
        options.add(limit);
        options.add(MenuStyle.createLabel("Resolución"));
        options.add(field);
        return options;
    }

    /** @return los botones de volver, mejoras y jugar */
    private JPanel createButtons(){
        JButton back = MenuStyle.createButton("Volver");
        JButton upgrade = MenuStyle.createButton("Mejoras");
        JButton play = MenuStyle.createButton("Jugar");
        upgrade.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.showUpgrades();
            }
        });
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
        buttons.setBackground(MenuStyle.BACKGROUND);
        buttons.add(back);
        buttons.add(upgrade);
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
        MenuStyle.styleField(box);
        return box;
    }

    /** @return selector de quién maneja a un jugador: Humano o un perfil de máquina */
    private JComboBox<String> createControlCombo(){
        ArrayList<String> items = new ArrayList<String>();
        items.add(HUMAN);
        items.addAll(game.getProfileIds());
        return createCombo(items);
    }

    /**
     * Deja escogida la modalidad del menú principal.
     * @param leftMachine true si el jugador 1 empieza como máquina
     * @param rightMachine true si el jugador 2 empieza como máquina
     */
    public void setMode(boolean leftMachine, boolean rightMachine){
        chooseControl(leftControl, leftMachine);
        chooseControl(rightControl, rightMachine);
        updateMode();
    }

    /**
     * @param control selector de un jugador
     * @param machine true para el primer perfil de máquina, false para Humano
     */
    private void chooseControl(JComboBox<String> control, boolean machine){
        if (machine && control.getItemCount() > FIRST_PROFILE_INDEX){
            control.setSelectedIndex(FIRST_PROFILE_INDEX);
        }
        else{
            control.setSelectedIndex(HUMAN_INDEX);
        }
    }

    /** Muestra la modalidad según los controles, por ejemplo "Máquina vs Jugador". */
    private void updateMode(){
        modeLabel.setText(controlText(leftControl) + VERSUS + controlText(rightControl));
    }

    /**
     * @param control selector de un jugador
     * @return "Jugador" si lo maneja una persona, "Máquina" si lo maneja un perfil
     */
    private String controlText(JComboBox<String> control){
        if (profileOf(control) == null){
            return HUMAN_TEXT;
        }
        return MACHINE_TEXT;
    }

    /**
     * @param control selector de un jugador
     * @return perfil escogido, o null si lo maneja una persona
     */
    private String profileOf(JComboBox<String> control){
        String chosen = (String) control.getSelectedItem();
        if (HUMAN.equals(chosen)){
            return null;
        }
        return chosen;
    }

    /** Cambia la etiqueta y el valor propuesto del límite según el tipo de partido. */
    private void updateLimit(){
        String rulesId = (String) rules.getSelectedItem();
        try{
            limitLabel.setText(game.getRuleLimitName(rulesId));
            limit.setText(String.valueOf(game.getRuleDefaultLimit(rulesId)));
        }
        catch (POOBSoccerException e){
            showError(e, e.getMessage());
        }
    }

    /** Lee lo escogido y pide iniciar el partido; si algo está mal, muestra el error. */
    private void play(){
        int number;
        try{
            number = Integer.parseInt(limit.getText().trim());
        }
        catch (NumberFormatException e){
            showError(e, NOT_A_NUMBER + limit.getText());
            return;
        }
        try{
            frame.playMatch((String) leftCountry.getSelectedItem(), (String) rightCountry.getSelectedItem(),
                            profileOf(leftControl), profileOf(rightControl), (String) rules.getSelectedItem(),
                            number, (String) field.getSelectedItem());
        }
        catch (POOBSoccerException e){
            showError(e, e.getMessage());
        }
    }

    /**
     * Registra el error y muestra el mensaje.
     * @param e error que ocurrió
     * @param message mensaje para el usuario
     */
    private void showError(Exception e, String message){
        game.recordError(e);
        JOptionPane.showMessageDialog(this, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
