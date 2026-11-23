package src.domain;

/**
 * Kick (patada): daño que el personaje le hace al rival; no cambia la fuerza del balón.
 */
public class KickStat extends Stat
{
    private static final String NAME = "Kick";

    /** @return nombre de la estadística */
    public String getName(){
        return NAME;
    }

    /**
     * Le pone al jugador el multiplicador de su nivel.
     * @param p jugador
     * @param level nivel de la estadística
     */
    public void apply(Player p, int level){
        p.setKickMultiplier(multiplier(level));
    }
}
