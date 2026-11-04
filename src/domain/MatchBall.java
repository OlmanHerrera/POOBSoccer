package src.domain;
import java.awt.*;

public class MatchBall implements Entity
{
    private PVector location;
    private PVector velocity;
    private static final int SIZE = 20;
    private int PWIDTH;
    private int PHEIGHT;
    
    public MatchBall(int PWIDTH, int PHEIGHT){
        location = new PVector(0,0);
        velocity = new PVector(2,2);
        this.PWIDTH = PWIDTH;
        this.PHEIGHT = PHEIGHT;
    }
    
    @Override
    public void update(){
        location.add(velocity);
    }
    
    
    @Override
    public void display(Graphics g){
        g.setColor(Color.black);
        g.fillOval((int) location.x, (int) location.y ,SIZE, SIZE);
    }
    
    
    @Override
    public void checkEdges()
    {
        if (location.x < 0 || location.x + SIZE > PWIDTH) { 
            velocity.x *= -1;
        }
        if (location.y < 0 || location.y + SIZE > PHEIGHT) {
            velocity.y *= -1;
        }
    }
   
}