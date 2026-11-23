package src.domain;

/**
 * Estadística de un jugador (Speed, Kick, Jump, Survival...). Todas empiezan en 1
 * y suben hasta 10; cada punto sobre 1 suma un 10 % sobre el valor base.
 * La parte fija es ese multiplicador; cada estadística decide qué atributo escala.
 */
public abstract class Stat
{
    private static final double BASE_MULTIPLIER = 1;
    private static final double PERCENT_PER_LEVEL = 0.10;
    private static final int FIRST_LEVEL = 1;

    /**
     * Parte fija de la plantilla: 1 + 0.10 * (nivel - 1).
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
