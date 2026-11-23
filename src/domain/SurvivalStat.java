package src.domain;

/**
 * Survival: vida máxima del personaje.
 */
public class SurvivalStat extends Stat{
    private static final String NAME = "Survival";

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
        p.setLifeMultiplier(multiplier(level));
    }
}
