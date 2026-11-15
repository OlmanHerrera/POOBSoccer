package src.domain;
import java.awt.*;
import src.presentation.*;
public class BallSprite extends Sprite 
{
    //images used for the balls
    private static final String[] ballNames = {"ball"};



    private int nameIndex;

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
        
        setPosition((int) (getPWidth()* Math.random()), 0); 
                        //somewhere along the top
        setVelocity(1,1);

    }


    public void updateSprite()
    {
        hasHitPlayer();
        hasHitWall();
        super.updateSprite();
    }

    private void hasHitPlayer()
    {
        Rectangle rect = getMyRectangle();
        if (rect.intersects(leftPlayer.getMyRectangle())){
            Rectangle interRect = rect.intersection(leftPlayer.getMyRectangle());
            velocity.y = - velocity.y;
            location.y -= interRect.height; // move the ball up 
        }
        else if (rect.intersects(rightPlayer.getMyRectangle())){
            Rectangle interRect = rect.intersection(rightPlayer.getMyRectangle());
            velocity.y = - velocity.y;
            location.y -= interRect.height; // move the ball up 
        }
    }
  
    
    public void hasHitWall(){
        if ((location.x <= 0)&& (velocity.x < 0)){
            velocity.x = -velocity.x; // move Right
        }
        else if ((location.x + getWidth() >= getPWidth()) && (velocity.x > 0)){
            velocity.x = -velocity.x;
        }
        
        if ((location.y + getHeight() >= 341) && (velocity.y > 0)){
            velocity.y = -velocity.y;
        }
        else if((location.y <= 0) && (velocity.y < 0)){
            velocity.y = -velocity.y;
        }
        
    }
 
  
    

   
}