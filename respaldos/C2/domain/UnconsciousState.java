package src.domain;
import java.awt.Color;

/**
 * Inconsciente (Tornado): el jugador es lanzado al aire y durante 3 s
 * no puede moverse, saltar ni patear.
 */
public class UnconsciousState extends PlayerState
{
    private static final String NAME = "Inconsciente";
    private static final double DURATION_SECONDS = 3;
    private static final float LAUNCH_SPEED_Y = 5;

    private static final Color COLOR = new Color(200, 200, 200);

    /** @return gris */
    protected Color getColor(){
        return COLOR;
    }

    /** @return nombre del efecto */
    public String getName(){
        return NAME;
    }

    /** @return 3 segundos */
    protected double getDurationSeconds(){
        return DURATION_SECONDS;
    }

    /**
     * Lo detiene y lo lanza hacia arriba.
     * @param p jugador inconsciente
     */
    protected void enter(Player p){
        p.stop();
        p.launch(0, -LAUNCH_SPEED_Y);
    }

    /**
     * No hace nada al terminar: ya cayó al suelo.
     * @param p jugador inconsciente
     */
    protected void exit(Player p){
    }

    /**
     * @param a acción pedida
     * @return no puede moverse, saltar ni patear; solo activar su poder
     */
    protected Action filter(Action a){
        if (a == Action.POWER){
            return a;
        }
        return null;
    }
}
