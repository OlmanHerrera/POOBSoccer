package src.presentation;
import javax.swing.*;
import java.awt.*;

/**
 * Colores y letras de los menús y de los mensajes, en un solo lugar.
 * Solo colores sólidos de fondo, de texto y de botones.
 */
public class MenuStyle
{
    public static final Color BACKGROUND = new Color(225, 242, 228);
    public static final Color TITLE = new Color(0, 90, 50);
    public static final Color TEXT = new Color(25, 45, 35);
    public static final Color BUTTON = new Color(0, 120, 70);
    public static final Color BUTTON_TEXT = Color.WHITE;
    public static final Color FIELD = Color.WHITE;
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 13);

    /** Pone los colores de los mensajes, de los selectores de archivos y de los botones. */
    public static void applyToDialogs(){
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background", BUTTON);
        UIManager.put("Button.foreground", BUTTON_TEXT);
    }

    /**
     * @param text texto del botón
     * @return botón con los colores del menú
     */
    public static JButton createButton(String text){
        JButton button = new JButton(text);
        button.setBackground(BUTTON);
        button.setForeground(BUTTON_TEXT);
        button.setFont(TEXT_FONT);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * @param text texto de la etiqueta
     * @return etiqueta con el color y la letra del menú
     */
    public static JLabel createLabel(String text){
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(TEXT_FONT);
        return label;
    }

    /**
     * @param text título
     * @param font letra del título
     * @return título centrado con el color de los títulos
     */
    public static JLabel createTitle(String text, Font font){
        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setForeground(TITLE);
        title.setFont(font);
        return title;
    }

    /** @param field selector o campo de texto al que se le ponen los colores del menú */
    public static void styleField(JComponent field){
        field.setBackground(FIELD);
        field.setForeground(TEXT);
        field.setFont(TEXT_FONT);
    }
}
