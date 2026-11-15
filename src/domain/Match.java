package src.domain;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Match
{
  public static final int LEFT = 1;   
  public static final int RIGHT = 2;    

  // goal mouths on the screen (they must match where the panel draws goal_*.png)
  private static final int LEFT_GOAL_LINE = 46;    // x of the left goal line
  private static final int RIGHT_GOAL_LINE = 454;  // x of the right goal line
  private static final int CROSSBAR_Y = 281;       // y of the crossbar of both goals

  private static final int GOALS_TO_WIN = 5;

  private BallSprite ball;
  private PlayerSprite leftPlayer, rightPlayer;

  private int leftScore = 0;
  private int rightScore = 0;
  private boolean isOver = false;

  private ArrayList<MatchObserver> observers = new ArrayList<>();

  public Match(BallSprite ball, PlayerSprite leftPlayer, PlayerSprite rightPlayer)
  {
      this.ball = ball;
      this.leftPlayer = leftPlayer;
      this.rightPlayer = rightPlayer;
  }

  public void addObserver(MatchObserver o)
  {
      if (!observers.contains(o)){
          observers.add(o);
      }
  }

  public void removeObserver(MatchObserver o)
  {  observers.remove(o);  }

  private void notifyGoal(GoalEvent e)
  // fire the event: call the method of every observer
  {
      for (MatchObserver o : observers){
          o.goalScored(e);
      }
  }

  private void notifyEnd(GoalEvent e)
  {
      for (MatchObserver o : observers){
          o.matchEnded(e);
      }
  }


  public void update()
  // called once per game update
  {
      if (isOver){
          return;
      }
      ball.updateSprite();
      leftPlayer.updateSprite();
      rightPlayer.updateSprite();
      checkGoal();
  }

  private void checkGoal()
  {
      Rectangle r = ball.getMyRectangle();
      boolean belowCrossbar = r.y > CROSSBAR_Y;

      if (belowCrossbar && (r.x + r.width < LEFT_GOAL_LINE)){
          goal(RIGHT);
      }
      else if (belowCrossbar && (r.x > RIGHT_GOAL_LINE)){
          goal(LEFT);
      }
  }

  private void goal(int scorer)
  {
      if (scorer == LEFT){
          leftScore++;
      }
      else{
          rightScore++;
      }
      ball.initPosition();     // back to play, so the same goal is not counted again

      GoalEvent e = new GoalEvent(this, scorer, leftScore, rightScore);
      notifyGoal(e);

      if ((leftScore >= GOALS_TO_WIN) || (rightScore >= GOALS_TO_WIN)){
          isOver = true;
          notifyEnd(e);
      }
  } // end of goal()


  public int getLeftScore()
  {  return leftScore;  }

  public int getRightScore()
  {  return rightScore;  }

  public boolean isOver()
  {  return isOver;  }
} // end of Match class
