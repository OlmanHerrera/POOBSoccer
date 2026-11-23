package src.domain;
import java.awt.Color;

/**
 * Confusión: durante 2.5 s el rival no sabe cuál de los 8 balones es el real.
 */
public class ConfusedState extends PlayerState{
    public static final double DURATION_SECONDS = 2.5;
    private static final String NAME = "Confusión";

    private static final Color COLOR = new Color(220, 60, 220);

    /** @return morado */
    protected Color getColor(){
        return COLOR;
    }

    /** @return nombre del efecto */
    public String getName(){
        return NAME;
    }

    /** @return 2.5 segundos */
    protected double getDurationSeconds(){
        return DURATION_SECONDS;
    }

    /**
     * No hace nada al empezar.
     * @param p jugador confundido
     */
    protected void enter(Player p){
    }

    /**
     * No hace nada al terminar.
     * @param p jugador confundido
     */
    protected void exit(Player p){
    }

    /**
     * @param a acción pedida
     * @return la misma acción
     */
    protected Action filter(Action a){
        return a;
    }
}
