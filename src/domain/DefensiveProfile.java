package src.domain;


public class DefensiveProfile extends TargetProfile
{

    public DefensiveProfile(){

    }

    public Action decide(PlayerSprite p, Match m){
        PVector rival = centerOf(m.getOpponent(p).getMyRectangle());
        PVector myGoal = new PVector(m.getGoalLineX(p.getSide()), rival.y);

        PVector guard = rival.get();
        guard.add(myGoal);
        guard.div(2);
        return moveTo(p, guard.x);
    }
}
