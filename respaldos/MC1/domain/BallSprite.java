package src.domain;
import java.awt.*;
import src.presentation.*;
public class BallSprite extends Sprite
{
    //images used for the balls
    private static final String[] ballNames = {"ball"};



    private int nameIndex;

    private PVector acceleration = new PVector(0, 0);
    private float mass = 1;
    private static final float GRAVITY = 0.05f;
    private static final float TOP_SPEED = 8;
    private static final float FRICTION_C = 0.02f;
    private static final float NORMAL = 1;
    private static final float BOUNCE = 0.7f;
    private static final float MIN_BOUNCE = 0.5f;
    private static final float K = 0.15f;
    private static final int FLOOR = 341;
    private static final float KICK_X = 4;
    private static final float KICK_Y = 3.5f;

    private POOBSoccerPanel sp;
    private PlayerSprite leftPlayer;
    private PlayerSprite rightPlayer;


    public BallSprite(int w, int h, ImagesLoader imsLd, POOBSoccerPanel sp, PlayerSprite l, PlayerSprite r)
    {   super(w/2,0,w,h, imsLd, ballNames[0]);
            // the ball is positioned in the middle at the top of the panel
        this.sp = sp;
        leftPlayer = l;
        rightPlayer = r;

        nameIndex = 0;

        initPosition();
    }



    public void initPosition(){
        setImage(ballNames[nameIndex]);
        // set a diferent nameIndex

        setPosition((int) (getPWidth()/2), 0);
                        //half of the top
        setVelocity(0,0);

    }


    public void applyForce(PVector force)
    {
        PVector f = force.get();
        f.div(mass);
        acceleration.add(f);
    }

    public void updateSprite()
    {
        PVector gravity = new PVector(0, GRAVITY * mass);
        applyForce(gravity);
        if (isOnFloor()){
            applyFriction();
        }
        hasHitPlayer(leftPlayer);
        hasHitPlayer(rightPlayer);

        velocity.add(acceleration);
        velocity.limit(TOP_SPEED);
        acceleration.mult(0);

        hasHitWall();
        super.updateSprite();
    }

    private boolean isOnFloor()
    {
        return location.y + getHeight() >= FLOOR - 1;
    }

    private void applyFriction()
    {
        PVector friction = velocity.get();
        friction.mult(-1);
        friction.normalize();
        friction.mult(FRICTION_C * NORMAL);
        applyForce(friction);
    }

    private void hasHitPlayer(PlayerSprite player)
    {
        Rectangle b = getMyRectangle();
        if (!b.intersects(player.getMyRectangle())){
            return;
        }

        if (player.isKickActive() && b.intersects(player.getBootBox())){
            kicked(player);
            return;
        }

        PVector ballCenter = new PVector(b.x + b.width/2, b.y + b.height/2);
        PVector dir = ballCenter.get();
        dir.sub(player.getHeadCenter());
        float distance = dir.mag();
        float restLength = player.getHeadRadius() + b.width/2;

        if (distance < restLength){
            float stretch = restLength - distance;
            dir.normalize();
            dir.mult(K * stretch);
            applyForce(dir);
        }
    }

    private void kicked(PlayerSprite player)
    {
        float dir = 1;
        if (player.getSide() == Side.RIGHT){
            dir = -1;
        }
        velocity.mult(0);
        applyForce(new PVector(dir * KICK_X, -KICK_Y));
        player.markKickHit();
    }

    public void bounceOn(int surfaceY)
    {
        location.y = surfaceY - getHeight();
        if (velocity.y > 0){
            velocity.y = -velocity.y * BOUNCE;
            if (Math.abs(velocity.y) < MIN_BOUNCE){
                velocity.y = 0;
            }
        }
    }

    public void hasHitWall(){
        if ((location.x <= 0)&& (velocity.x < 0)){
            location.x = 0;
            velocity.x = -velocity.x * BOUNCE; // move Right
        }
        else if ((location.x + getWidth() >= getPWidth()) && (velocity.x > 0)){
            location.x = getPWidth() - getWidth();
            velocity.x = -velocity.x * BOUNCE;
        }

        if ((location.y + getHeight() >= FLOOR) && (velocity.y > 0)){
            location.y = FLOOR - getHeight();
            velocity.y = -velocity.y * BOUNCE;
            if (Math.abs(velocity.y) < MIN_BOUNCE){
                velocity.y = 0;
            }
        }
        else if((location.y <= 0) && (velocity.y < 0)){
            location.y = 0;
            velocity.y = -velocity.y;
        }

    }





}
