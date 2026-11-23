package src.domain;
import java.awt.Color;

/**
 * Aturdimiento: cuando la vida llega a cero el jugador no puede moverse,
 * saltar ni patear durante 3 s; al terminar recupera su vida máxima.
 */
public class StunnedState extends PlayerState
{
    private static final String NAME = "Aturdido";
    private static final double DURATION_SECONDS = 3;

    private static final Color COLOR = new Color(255, 140, 0);

    /** @return naranja */
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
     * El jugador se detiene.
     * @param p jugador aturdido
     */
    protected void enter(Player p){
        p.stop();
    }

    /**
     * Al terminar (o si otro efecto lo reemplaza) recupera la vida máxima.
     * @param p jugador aturdido
     */
    protected void exit(Player p){
        p.restoreLife();
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
