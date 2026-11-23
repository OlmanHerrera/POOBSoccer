package src.presentation;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Colores y letras de los menús y de los mensajes, solo en blanco, negro y grises.
 */
public class MenuStyle{
    public static final Color BACKGROUND = new Color(235, 235, 235);
    public static final Color TITLE = Color.BLACK;
    public static final Color TEXT = new Color(30, 30, 30);
    public static final Color BUTTON = new Color(64, 64, 64);
    public static final Color BUTTON_TEXT = Color.WHITE;
    public static final Color FIELD = Color.WHITE;
    public static final Color DARK_GRAY = new Color(90, 90, 90);
    public static final Color MIDDLE_GRAY = new Color(160, 160, 160);
    public static final Color LIGHT_GRAY = new Color(200, 200, 200);
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final String[] MESSAGE_ICONS = {"OptionPane.errorIcon", "OptionPane.warningIcon",
                                                   "OptionPane.informationIcon", "OptionPane.questionIcon"};

    /**
     * Pone los grises en los menús, los mensajes y los selectores de archivos.
     * @throws UnsupportedLookAndFeelException si no se puede cambiar la apariencia de Swing
     */
    public static void applyColors() throws UnsupportedLookAndFeelException{
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme(){
            protected ColorUIResource getPrimary1(){
                return new ColorUIResource(DARK_GRAY);
            }

            protected ColorUIResource getPrimary2(){
                return new ColorUIResource(MIDDLE_GRAY);
            }

            protected ColorUIResource getPrimary3(){
                return new ColorUIResource(LIGHT_GRAY);
            }
        });
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT);
        for (int i = 0; i < MESSAGE_ICONS.length; i++){
            UIManager.put(MESSAGE_ICONS[i], grayIcon(UIManager.getIcon(MESSAGE_ICONS[i])));
        }
    }

    /**
     * @param icon icono a color
     * @return el mismo icono en grises
     */
    private static Icon grayIcon(Icon icon){
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        for (int x = 0; x < image.getWidth(); x++){
            for (int y = 0; y < image.getHeight(); y++){
                Color c = new Color(image.getRGB(x, y), true);
                int gray = (c.getRed() * 30 + c.getGreen() * 59 + c.getBlue() * 11) / 100;
                image.setRGB(x, y, new Color(gray, gray, gray, c.getAlpha()).getRGB());
            }
        }
        return new ImageIcon(image);
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
