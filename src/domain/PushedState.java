package src.domain;
import java.awt.Color;

/**
 * Empuje: durante 1.5 s el jugador es lanzado hacia su propio arco y no se controla.
 */
public class PushedState extends PlayerState{
    private static final String NAME = "Empuje";
    private static final double DURATION_SECONDS = 1.5;
    private static final float PUSH_SPEED_X = 4;
    private static final float PUSH_SPEED_Y = 3;

    private static final Color COLOR = new Color(40, 120, 255);

    /** @return azul */
    protected Color getColor(){
        return COLOR;
    }

    /** @return nombre del efecto */
    public String getName(){
        return NAME;
    }

    /** @return 1.5 segundos */
    protected double getDurationSeconds(){
        return DURATION_SECONDS;
    }

    /**
     * Lanza al jugador hacia su propio arco.
     * @param p jugador empujado
     */
    protected void enter(Player p){
        p.stop();
        p.launch(p.getOwnGoalDirection() * PUSH_SPEED_X, -PUSH_SPEED_Y);
    }

    /**
     * Al terminar el empuje el jugador queda quieto.
     * @param p jugador empujado
     */
    protected void exit(Player p){
        p.stop();
    }

    /**
     * @param a acción pedida
     * @return solo puede activar su poder
     */
    protected Action filter(Action a){
        if (a == Action.POWER){
            return a;
        }
        return null;
    }
}
