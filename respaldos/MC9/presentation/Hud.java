package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.Side;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * Información permanente del partido, dibujada sobre la cancha:
 * marcador y tiempo. Le pide todos los datos a la fachada.
 */
public class Hud
{
    private static final Font SCORE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Color STRIP_COLOR = new Color(0, 0, 0, 150);
    private static final int STRIP_HEIGHT = 50;
    private static final int SCORE_Y = 24;
    private static final int TIME_Y = 42;
    private static final String SCORE_SEPARATOR = "  -  ";

    /**
     * Dibuja la franja de arriba con el marcador y el tiempo.
     * @param g donde se dibuja
     * @param game fachada del dominio
     * @param width ancho de la cancha
     */
    public void draw(Graphics g, POOBSoccerGame game, int width){
        g.setColor(STRIP_COLOR);
        g.fillRect(0, 0, width, STRIP_HEIGHT);
        g.setColor(Color.white);
        g.setFont(SCORE_FONT);
        String score = game.getGoals(Side.LEFT) + SCORE_SEPARATOR + game.getGoals(Side.RIGHT);
        drawCentered(g, score, width / 2, SCORE_Y);
        g.setFont(TEXT_FONT);
        drawCentered(g, game.getTimeText(), width / 2, TIME_Y);
    }

    /**
     * Escribe un texto centrado alrededor de una x.
     * @param g donde se escribe
     * @param text texto
     * @param centerX x del centro del texto
     * @param y y de la línea base del texto
     */
    public void drawCentered(Graphics g, String text, int centerX, int y){
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, centerX - fm.stringWidth(text) / 2, y);
    }
}
