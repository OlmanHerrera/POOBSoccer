package src.domain;

/**
 * Estadística de un jugador, que va del nivel 1 al 10.
 */
public abstract class Stat{
    private static final double BASE_MULTIPLIER = 1;
    private static final double PERCENT_PER_LEVEL = 0.10;
    private static final int FIRST_LEVEL = 1;

    /**
     * Multiplicador del nivel: cada nivel sobre 1 suma un 10 %.
     * @param level nivel de 1 a 10
     * @return multiplicador del nivel
     */
    public double multiplier(int level){
        return BASE_MULTIPLIER + PERCENT_PER_LEVEL * (level - FIRST_LEVEL);
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
