package src.domain;
import java.awt.Rectangle;


public abstract class TargetProfile implements PlayerStrategy
{
    private static final int MARGIN = 5;

    public abstract Action decide(PlayerSprite p, Match m);

    protected PVector centerOf(Rectangle r){
        return new PVector(r.x + r.width/2, r.y + r.height/2);
    }

    protected Action moveTo(PlayerSprite p, float targetX){
        PVector me = centerOf(p.getMyRectangle());
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
