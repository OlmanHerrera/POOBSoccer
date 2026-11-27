package src.domain;
import java.awt.Rectangle;

/**
 * Perfil que se mueve hacia una x objetivo que decide cada perfil concreto.
 */
public abstract class TargetProfile implements PlayerStrategy{
    private static final int MARGIN = 5;

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return acción que quiere hacer
     */
    public abstract Action decide(Player p, Match m);

    /**
     * @param r rectángulo
     * @return centro del rectángulo
     */
    protected PVector centerOf(Rectangle r){
        return new PVector(r.x + r.width/2, r.y + r.height/2);
    }

    /**
     * Patea si alcanza el balón; si no, se acerca a la x objetivo.
     * @param p jugador que decide
     * @param m partido actual
     * @param targetX x a la que quiere llegar
     * @return patear, o la acción para acercarse
     */
    protected Action kickOrMoveTo(Player p, Match m, float targetX){
        if (p.canKick(m.getBallBounds())){
            return Action.KICK;
        }
        return moveTo(p, targetX);
    }

    /**
     * Acción para acercarse a una x objetivo.
     * @param p jugador que se mueve
     * @param targetX x a la que quiere llegar
     * @return moverse a la izquierda, a la derecha o quedarse quieto
     */
    protected Action moveTo(Player p, float targetX){
        PVector me = centerOf(p.getBounds());
        if (me.x < targetX - MARGIN){
            return Action.MOVE_RIGHT;
        }
        else if (me.x > targetX + MARGIN){
            return Action.MOVE_LEFT;
        }
        else{
            return Action.STAY_STILL;
        }
    }
}
