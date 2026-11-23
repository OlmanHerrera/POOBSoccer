package src.presentation;
import src.domain.POOBSoccerGame;
import src.domain.Side;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Información permanente del partido, dibujada sobre la cancha: marcador, tiempo
 * y, por jugador, personaje, vida, barra de poder, aura, efecto, puntos y estadísticas.
 * Le pide todos los datos a la fachada.
 */
public class Hud
{
    private static final Font SCORE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font INFO_FONT = new Font("SansSerif", Font.BOLD, 10);
    private static final Color STRIP_COLOR = new Color(0, 0, 0, 160);
    private static final Color LIFE_COLOR = new Color(60, 200, 80);
    private static final Color POWER_COLOR = new Color(80, 170, 255);
    private static final Color BAR_BACKGROUND = new Color(90, 90, 90);
    private static final Color AURA_COLOR = Color.yellow;
    private static final int STRIP_HEIGHT = 74;
    private static final int SCORE_Y = 26;
    private static final int TIME_Y = 44;
    private static final int MARGIN = 8;
    private static final int INFO_WIDTH = 172;
    private static final int LINE_HEIGHT = 13;
    private static final int BAR_X = 62;
    private static final int BAR_WIDTH = 70;
    private static final int BAR_HEIGHT = 8;
    private static final int AURA_GAP = 4;
    private static final String SCORE_SEPARATOR = "  -  ";
    private static final String LEFT_TITLE = "J1 ";
    private static final String RIGHT_TITLE = "J2 ";
    private static final String MACHINE_TEXT = " (máquina)";
    private static final String AURA_TEXT = "AURA";

    /**
     * Dibuja la franja de arriba con el marcador, el tiempo y los dos jugadores.
     * @param g donde se dibuja
     * @param game fachada del dominio
     * @param width ancho de la cancha
     */
    public void draw(Graphics g, POOBSoccerGame game, int width){
        g.setColor(STRIP_COLOR);
        g.fillRect(0, 0, width, STRIP_HEIGHT);
        drawScore(g, game, width);
        drawPlayer(g, game, Side.LEFT, LEFT_TITLE, MARGIN);
        drawPlayer(g, game, Side.RIGHT, RIGHT_TITLE, width - INFO_WIDTH - MARGIN);
    }

    /** Marcador y tiempo en el centro. */
    private void drawScore(Graphics g, POOBSoccerGame game, int width){
        g.setColor(Color.white);
        g.setFont(SCORE_FONT);
        String score = game.getGoals(Side.LEFT) + SCORE_SEPARATOR + game.getGoals(Side.RIGHT);
        drawCentered(g, score, width / 2, SCORE_Y);
        g.setFont(TEXT_FONT);
        drawCentered(g, game.getTimeText(), width / 2, TIME_Y);
    }

    /**
     * Información de un jugador, una cosa por línea.
     * @param g donde se dibuja
     * @param game fachada del dominio
     * @param s lado del jugador
     * @param title "J1 " o "J2 "
     * @param x borde izquierdo del bloque
     */
    private void drawPlayer(Graphics g, POOBSoccerGame game, Side s, String title, int x){
        g.setFont(INFO_FONT);
        g.setColor(Color.white);
        int y = LINE_HEIGHT;
        g.drawString(title + game.getCountryName(s) + machineText(game, s), x, y);
        y = y + LINE_HEIGHT;
        double life = game.getLife(s);
        double maxLife = game.getMaxLife(s);
        drawBar(g, x, y, "Vida " + (int) life + "/" + (int) maxLife, life / maxLife, LIFE_COLOR);
        y = y + LINE_HEIGHT;
        drawBar(g, x, y, "Poder", game.getPowerCharge(s), POWER_COLOR);
        if (game.isAuraActive(s)){
            g.setColor(AURA_COLOR);
            g.drawString(AURA_TEXT, x + BAR_X + BAR_WIDTH + AURA_GAP, y);
        }
        g.setColor(Color.white);
        y = y + LINE_HEIGHT;
        g.drawString("Efecto: " + game.getEffectName(s) + "   Puntos: " + game.getPoints(s), x, y);
        y = y + LINE_HEIGHT;
        g.drawString(statsText(game, s), x, y);
    }

    /**
     * Una barra con su texto a la izquierda.
     * @param g donde se dibuja
     * @param x borde izquierdo
     * @param y línea base del texto
     * @param label texto
     * @param fraction parte llena, de 0 a 1
     * @param color color de la parte llena
     */
    private void drawBar(Graphics g, int x, int y, String label, double fraction, Color color){
        g.setColor(Color.white);
        g.drawString(label, x, y);
        int top = y - BAR_HEIGHT;
        g.setColor(BAR_BACKGROUND);
        g.fillRect(x + BAR_X, top, BAR_WIDTH, BAR_HEIGHT);
        g.setColor(color);
        g.fillRect(x + BAR_X, top, (int) (BAR_WIDTH * fraction), BAR_HEIGHT);
    }

    /** @return " (máquina)" si ese lado lo juega la máquina, o nada */
    private String machineText(POOBSoccerGame game, Side s){
        if (game.isMachine(s)){
            return MACHINE_TEXT;
        }
        return "";
    }

    /** @return las estadísticas con su nivel, por ejemplo "Speed 1 Kick 3 ..." */
    private String statsText(POOBSoccerGame game, Side s){
        ArrayList<String> names = game.getStatNames();
        String text = "";
        for (int i = 0; i < names.size(); i++){
            text = text + names.get(i) + " " + game.getStatLevel(s, names.get(i)) + " ";
        }
        return text;
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
