
package src.domain;

import java.awt.Rectangle;

public  class PlayerSprite extends Sprite implements ImagesPlayerWatcher{
    
    private static final double  DURATION = 0.9;
    
    private int period;
    /*in ms. The game's animation period used by the image cycling */
    
    
    // global
    private String imageName;

    private PlayerStrategy strategy;
    private static final int XSTEP = 1;
        //step distance for moving along x-axis
        
    private Side side;
    private Action lastAction = Action.STAY_STILL;

    private PVector acceleration = new PVector(0, 0);
    private float mass = 1;
    private int groundY;
    private static final float GRAVITY = 0.1f;
    private static final float JUMP = 4;

    private String kickName;
    private boolean kicking = false;
    private boolean kickHitDone = false;
    private static final double KICK_DURATION = 0.5;
    
    

    public PlayerSprite(Field field, ImagesLoader imsLd, int p, String fnm, Side side)
    {

        super(field.getStartX(side), 0, field.getWidth(), field.getHeight(), imsLd, fnm);
        this.imageName = fnm;
        this.kickName = fnm.replace("_run", "_kick");
        period = p;
        setVelocity(0,0); // no movement
        this.side = side;
        groundY = field.getFloorY() - getHeight(); // los pies quedan sobre el suelo
        location.y = groundY;


    }
    
    

    public void moveLeft()
    {
        velocity.x = -XSTEP;
        if (!kicking){
            setImage(imageName);
            loopImage(period,DURATION);
        }
    }
    public void stayStill()
    {
        velocity.x = 0;
        if (!kicking){
            stopLooping();
        }
    }
    public void moveRight()
    {
        velocity.x = XSTEP;
        if (!kicking){
            setImage(imageName);
            loopImage(period, DURATION);
        }
    }

    public void kick()
    {
        if (kicking){
            return;
        }
        kicking = true;
        kickHitDone = false;
        setImage(kickName);
        playImageOnce(period, KICK_DURATION, this);
    }

    public void sequenceEnded(String name)
    {
        kicking = false;
        setImage(imageName);
        if (velocity.x != 0){
            loopImage(period, DURATION);
        }
    }

    public boolean isKickActive()
    {
        if (!kicking){
            return false;
        }
        if (kickHitDone){
            return false;
        }
        int frame = getImagePosition();
        if (frame == 2 || frame == 3){
            return true;
        }
        return false;
    }

    public void markKickHit()
    {
        kickHitDone = true;
    }

    public PVector getHeadCenter()
    {
        Rectangle r = getMyRectangle();
        return new PVector(r.x + r.width/2, r.y + r.height/3);
    }

    public float getHeadRadius()
    {
        return getMyRectangle().width/2;
    }

    public Rectangle getBootBox()
    {
        Rectangle r = getMyRectangle();
        int w = r.width/2 + 4;
        int h = r.height/3;
        if (side == Side.LEFT){
            return new Rectangle(r.x + r.width - w, r.y + r.height - h, w, h);
        }
        return new Rectangle(r.x, r.y + r.height - h, w, h);
    }
    public void jump()
    {
        if (isOnGround()){
            applyForce(new PVector(0, -JUMP));
        }
    }

    public void applyForce(PVector force)
    {
        PVector f = force.get();
        f.div(mass);
        acceleration.add(f);
    }

    private boolean isOnGround()
    {
        return location.y >= groundY;
    }

    public void updateSprite()
    {
        applyForce(new PVector(0, GRAVITY * mass));
        velocity.add(acceleration);
        acceleration.mult(0);

        checkEdges();
        super.updateSprite();
        checkGround();
    }
    


    private void checkGround()
    {
        if (location.y >= groundY){
            location.y = groundY;
            if (velocity.y > 0){
                velocity.y = 0;
            }
        }
    }

    private void checkEdges()
    {
        if ((location.x + velocity.x < 0) && (velocity.x < 0)){
            location.x = 0;
            stayStill();
        }
        else if ((location.x + getWidth() + velocity.x > getPWidth()) && (velocity.x > 0)){
            location.x = getPWidth() - getWidth();
            stayStill();
        }
    }
    public void setStrategy(PlayerStrategy ps)
    {
        this.strategy = ps;
    }
    
    public Side getSide()
    {
        return side;
    }

    public void perform(Action a)

    {
        if (a == Action.JUMP){
            jump();
            return;
        }
        if (a == Action.KICK){
            kick();
            return;
        }
        if ((a == null) || (a == lastAction)){
            return;
        }
        lastAction = a;
        if (a == Action.MOVE_LEFT){
            this.moveLeft();
        }
        else if (a == Action.MOVE_RIGHT){
            this.moveRight();
        }
        else if (a == Action.STAY_STILL){
            this.stayStill();
        }
    }
    
    
    public void think(Match m)
    {
        if (strategy == null){
            return;
        }
        Action a= strategy.decide(this, m);
        perform(a);
        
    }
}