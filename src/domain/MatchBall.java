package src.domain;
import java.awt.*;

public class MatchBall implements Entity
{
    private PVector location;
    private PVector velocity;
    private PVector acceleration;
    private float mass;
    private static final int SIZE = 20;
    private int PWIDTH;
    private int PHEIGHT;
    private float topspeed;

    
    public MatchBall(int PWIDTH, int PHEIGHT){
        location = new PVector(0, 0);
        velocity = new PVector(0,0);
        acceleration = new PVector((float) 5, 0);
        topspeed = 10;
        mass = 1;
        this.PWIDTH = PWIDTH;
        this.PHEIGHT = PHEIGHT;
    }
    
    @Override
    public void update(){
        velocity.add(acceleration);
        velocity.limit(5);
        location.add(velocity);
        acceleration.mult(0);
    }
    
    
    @Override
    public void display(Graphics g){
        g.setColor(Color.black);
        g.fillOval((int) location.x, (int) location.y ,SIZE, SIZE);
    }
    
    
    @Override
    public void checkEdges(){
        if (location.x < 0) {
            location.x = 0;
            velocity.x = Math.abs(velocity.x);    // siempre hacia la derecha
        } else if (location.x + SIZE > PWIDTH) {
            location.x = PWIDTH - SIZE;
            velocity.x = -Math.abs(velocity.x);   // siempre hacia la izquierda
        }
        if (location.y < 0) {
            location.y = 0;
            velocity.y = Math.abs(velocity.y);    // siempre hacia abajo
        } else if (location.y + SIZE > PHEIGHT) {
            location.y = PHEIGHT - SIZE;
            velocity.y = -Math.abs(velocity.y);   // siempre hacia arriba
        }

    }
    
    public void applyForce(PVector force){
        PVector f = force.get(); // get a copy of the force
        f.div(mass);
        acceleration.add(f);
    }
    
    public float getMass(){
        return mass;
    }
    
    public PVector getVelocity(){
        return velocity.get();
    }
    

   
}