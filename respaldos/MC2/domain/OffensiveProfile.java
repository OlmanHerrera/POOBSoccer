package src.domain;


public class OffensiveProfile extends TargetProfile
{
    private static final int LOOK_AHEAD = 20;
    private static final int BEHIND = 30;

    public OffensiveProfile(){

    }

    public Action decide(PlayerSprite p, Match m){
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
