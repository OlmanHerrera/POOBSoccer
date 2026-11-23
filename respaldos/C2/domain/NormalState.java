package src.domain;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Sin efecto: el jugador puede hacer todo y este estado no vence nunca.
 */
public class NormalState extends PlayerState
{
    private static final String NAME = "Normal";
    private static final double FOREVER = Double.MAX_VALUE;

    /**
     * Sin efecto no se dibuja nada sobre el jugador.
     * @param g donde se dibuja
     * @param body rectángulo del jugador
     */
    public void draw(Graphics g, Rectangle body){
    }

    /** @return blanco (no se usa porque este estado no se dibuja) */
    protected Color getColor(){
        return Color.white;
    }

    /** @return nombre del estado */
    public String getName(){
        return NAME;
    }

    /** @return una duración que nunca se alcanza */
    protected double getDurationSeconds(){
        return FOREVER;
    }

    /**
     * No hace nada al empezar.
     * @param p jugador
     */
    protected void enter(Player p){
    }

    /**
     * No hace nada al terminar.
     * @param p jugador
     */
    protected void exit(Player p){
    }

    /**
     * @param a acción pedida
     * @return la misma acción: todo está permitido
     */
    protected Action filter(Action a){
        return a;
    }
}
