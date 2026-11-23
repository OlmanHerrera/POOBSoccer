package src.domain;

/**
 * Speed (velocidad): velocidad de desplazamiento del personaje.
 */
public class SpeedStat extends Stat
{
    private static final String NAME = "Speed";

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
        p.setSpeedMultiplier(multiplier(level));
    }
}
