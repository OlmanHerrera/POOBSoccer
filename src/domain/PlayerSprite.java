
package src.domain;

public  class PlayerSprite extends Sprite{
    
    private static final int FLOOR_DIST = 143;
        //distance of player's top from the floor
    private static final double  DURATION = 0.9;
    
    private int period;
    /*in ms. The game's animation period used by the image cycling */
    
    
    // global
    private String imageName;
    
    private static final int XSTEP = 1;
        //step distance for moving along x-axis
    public PlayerSprite(int locx, int w, int h, ImagesLoader imsLd, int p, String fnm)
    {
        
        super(locx,h-FLOOR_DIST, w, h, imsLd, fnm);
        this.imageName = fnm;
        period = p;
        setVelocity(0,0); // no movement
   
        
    }
    
    
    public void moveLeft()
    {
        setVelocity(-XSTEP, 0);
        setImage(imageName);
        loopImage(period,DURATION);
    }
    public void stayStill()
    {
        setVelocity(0,0);
        stopLooping();
    }   
    public void moveRight(){
        setVelocity(XSTEP,0);
        setImage(imageName);
        loopImage(period, DURATION);
    }
    public void updateSprite()
    {
        if ((location.x - getWidth() <= 0) && (velocity.x < 0)){
            setVelocity(0,0);
            location.x=0;
        }
        else if ((location.x + getWidth()>= getPWidth()) && velocity.x > 0){
            setVelocity(0,0);
            location.x = location.x-getWidth();
        }
        
        
        super.updateSprite();
    }
    
}