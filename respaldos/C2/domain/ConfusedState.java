package src.domain;
import java.awt.Color;

/**
 * Confusión (Ilusión): durante 2.5 s el rival ve 8 balones iguales y no sabe
 * cuál es el real. Los balones falsos los crea el tiro; este efecto no le
 * quita acciones al jugador.
 */
public class ConfusedState extends PlayerState
{
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
