package src.domain;

/**
 * Estadística de un jugador (velocidad, patada, salto, supervivencia...).
 * La parte fija es el multiplicador por nivel; cada estadística decide
 * qué atributo del jugador escala.
 */
public abstract class Stat
{
    /**
     * Parte fija de la plantilla: 1 + 0.1 * (nivel - 1).
     * @param level nivel de 1 a 10
     * @return multiplicador del nivel
     */
    public double multiplier(int level){
        return 0; // pendiente MC6
    }

    /** @return nombre de la estadística */
    public abstract String getName();

    /**
     * Escala el atributo del jugador que corresponde a esta estadística.
     * @param p jugador
     * @param level nivel de la estadística
     */
    public abstract void apply(Player p, int level);
}
