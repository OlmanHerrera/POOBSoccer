package src.domain;
import java.util.Random;



public class ClumsyProfile implements PlayerStrategy
{
    private Random random;
    private int updatesCounter;
    private Action current;
    public static final int MAX_UPDATES = 50;
    public ClumsyProfile(){
        random = new Random();
        updatesCounter= 0;
        current = Action.MOVE_LEFT;
    }
    
    public Action decide(PlayerSprite p, Match m){
        updatesCounter++;
        if (updatesCounter  == MAX_UPDATES){
            current = Action.values()[random.nextInt(Action.values().length)];
            updatesCounter = 0;
        }
        
        return current;
    }
   
}