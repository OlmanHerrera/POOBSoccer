package src.domain;

/**
 * Perfil ofensivo: su prioridad es anotar. Se ubica detrás del balón,
 * anticipando hacia dónde va, para empujarlo hacia el arco rival.
 */
public class OffensiveProfile extends TargetProfile
{
    private static final int LOOK_AHEAD = 20;
    private static final int BEHIND = 30;

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return acción para quedar detrás del balón
     */
    public Action decide(Player p, Match m){
        PVector ball = centerOf(m.getBallBounds());
        PVector speed = m.getBallVelocity();
        speed.mult(LOOK_AHEAD);
        ball.add(speed);

        if (p.getSide() == Side.LEFT){
            ball.add(new PVector(-BEHIND, 0));
        }
        else{
            ball.add(new PVector(BEHIND, 0));
        }
        return moveTo(p, ball.x);
    }

}
