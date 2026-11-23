package src.domain;

/**
 * Jump: altura del salto del personaje.
 */
public class JumpStat extends Stat{
    private static final String NAME = "Jump";

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
        p.setJumpMultiplier(multiplier(level));
    }
}
