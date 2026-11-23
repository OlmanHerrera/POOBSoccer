package src.domain;

/**
 * Perfil ofensivo: se ubica detrás del balón para llevarlo al arco rival.
 */
public class OffensiveProfile extends TargetProfile{
    private static final String ID = "Ofensiva";
    private static final int LOOK_AHEAD = 20;
    private static final int BEHIND = 30;

    /** @return nombre del perfil */
    public String getId(){
        return ID;
    }

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return acción para quedar detrás del balón
     */
    public Action decide(Player p, Match m){
        PVector target = centerOf(m.getBallBounds());
        PVector speed = m.getBallVelocity();
        speed.mult(LOOK_AHEAD);
        target.add(speed);
        if (p.getSide() == Side.LEFT){
            target.add(new PVector(-BEHIND, 0));
        }
        else{
            target.add(new PVector(BEHIND, 0));
        }
        return moveTo(p, target.x);
    }
}
