package src.domain;
import java.lang.Thread;
import javax.swing.*;
import javax.swing.event.*;

public class GamePanel extends JPanel implements  Runnable{
    boolean doStop = false;
    Thread animation;

    public static void main(String[] args){
        //Here we'll initialize the animation loop
    }

    @Override 
    public void run(){
        ///Here there are the three stages {game Update, render and a short sleep}
    }
    

    public void doStop(){
        this.doStop = true;
    }

    public boolean keepRunning(){
        return this.doStop == false;
    }


}