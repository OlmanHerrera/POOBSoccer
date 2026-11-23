package src.domain;
import java.awt.Color;

/**
 * Electrocutado: durante 3 s no puede saltar y se mueve a la mitad de su velocidad.
 */
public class ElectrocutedState extends PlayerState{
    private static final String NAME = "Electrocutado";
    private static final double DURATION_SECONDS = 3;
    private static final double SLOW_SPEED = 0.5;
    private static final double NORMAL_SPEED = 1;

    private static final Color COLOR = new Color(255, 220, 0);

    /** @return amarillo eléctrico */
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
     * Baja la velocidad a la mitad.
     * @param p jugador electrocutado
     */
    protected void enter(Player p){
        p.setEffectSpeedFactor(SLOW_SPEED);
    }

    /**
     * Devuelve la velocidad normal.
     * @param p jugador electrocutado
     */
    protected void exit(Player p){
        p.setEffectSpeedFactor(NORMAL_SPEED);
    }

    /**
     * @param a acción pedida
     * @return todo menos saltar
     */
    protected Action filter(Action a){
        if (a == Action.JUMP){
            return null;
        }
        return a;
    }
}
