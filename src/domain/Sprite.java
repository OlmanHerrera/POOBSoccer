package src.domain;
import java.awt.*;
import java.awt.image.*;

public class Sprite
{
    //defaultstep sizes (how far to move in each update)
    private static final int XSTEP = 5;
    private static final int YSTEP = 0;
    

    private int pWidth, pHeight; //Panel dimensions
    
    
    //Protected vars;
    protected PVector location; // location of sprite 
    protected PVector velocity ; //amount to move for each update
    
    //default dimensions when there is no image
    public static final int SIZE = 12;
    
    
    // image-related globals 
    
    private ImagesLoader imsLoader;
    private String imageName;
    private BufferedImage image;
    private int width, height; // image Dimensions
    
    private ImagesPlayer player; //for playing a loop of images
    private boolean isLooping;
    
    
    
    //global
    
    private boolean isActive = true;//a sprite is updated and drawn only when active

    
    
    public Sprite(int x, int y, int w, int h, ImagesLoader imsLd, String name)
    {
        location = new PVector(x,y);
        pWidth = w; pHeight = h;
        velocity = new PVector(XSTEP, YSTEP);
        imsLoader = imsLd;
        setImage(name); //the sprite's default image is 'name'
    } // end of Sprite's constructor
    
    
    public void setImage(String name)
    {
        imageName = name;
        image = imsLoader.getImage(imageName);
        if (image == null){
            System.out.println("No sprite image for "+imageName);
            width = SIZE;
            height = SIZE;
            
        }
        else{
            width = image.getWidth();
            height = image.getHeight();
        }
        
        //no image loop playing
        player = null;
        isLooping = false;
    }
    
    public void loopImage(int animPeriod, double seqDuration)
    {
        if (imsLoader.numImages(imageName) > 1){
            player = null;  //for garbage cllection of previous player
            player = new ImagesPlayer(imageName, animPeriod, seqDuration, true , imsLoader);
            isLooping = true;
        }
        else{
            System.out.println(imageName + " is not a sequence of images");
        }

    }
    
    public void playImageOnce(int animPeriod, double seqDuration, ImagesPlayerWatcher w)
    {
        if (imsLoader.numImages(imageName) > 1){
            player = new ImagesPlayer(imageName, animPeriod, seqDuration, false, imsLoader);
            player.setWatcher(w);
            isLooping = true;
        }
    }

    public int getImagePosition()
    {
        if (isLooping){
            return player.getCurrentPosition();
        }
        return -1;
    }

    public void stopLooping()
    {
        if (isLooping){
            player.stop();
            isLooping = false;
        }
    }
    
    public Rectangle getMyRectangle()
    {return new Rectangle((int)location.x,(int)location.y,width,height);}
    
    public void updateSprite()
    {
        if (isActive()){
            location.add(velocity);
            if (isLooping){
                player.updateTick(); //update the player
            }
        }
    }
    
    public void drawSprite(Graphics g)
    {
        if(isActive()){
            if (image == null){ // the sprite has no image
                g.setColor(Color.yellow); //draw a yellow circle instead
                g.fillOval((int)location.x,(int) location.y,SIZE,SIZE);
                g.setColor(Color.black);
                return;
        
            }
            else if (isLooping){
                image = player.getCurrentImage();
            }
            g.drawImage(image,(int)location.x, (int) location.y, null);
        
        }
    }
    
    public boolean isActive(){
        return isActive == true;
    }
    
    public int getWidth(){
        return width;
    }
    
    
    public int getPWidth(){
        return pWidth; 
    }
    
    
    public void setPosition(int locx, int locy)
    {
        location.x = locx;
        location.y = locy;
    }
    
    public void setVelocity(int dx, int dy  ){
        velocity.x = dx;
        velocity.y = dy;
    }
    
    public int getHeight(){
        return height;
    }
    
    public int getPHeight(){
        return pHeight;
    }
}