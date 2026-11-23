package src.domain;

/**
 * Perfil defensivo: se para entre el rival y su propio arco.
 */
public class DefensiveProfile extends TargetProfile{
    private static final String ID = "Defensiva";

    /** @return nombre del perfil */
    public String getId(){
        return ID;
    }

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return acción para quedar entre el rival y su arco
     */
    public Action decide(Player p, Match m){
        PVector rival = centerOf(m.getOpponent(p).getBounds());
        PVector myGoal = new PVector(m.getGoalLineX(p.getSide()), rival.y);

        PVector guard = rival.get();
        guard.add(myGoal);
        guard.div(2);
        return moveTo(p, guard.x);
    }
}
