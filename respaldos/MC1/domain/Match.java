package src.domain;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Match
{

  private static final int LEFT_GOAL_LINE = 65;
  private static final int RIGHT_GOAL_LINE = 435;
  private static final int CROSSBAR_Y = 221;

  private static final int GOALS_TO_WIN = 5;

  private BallSprite ball;
  private PlayerSprite leftPlayer, rightPlayer;

  private int leftScore = 0;
  private int rightScore = 0;
  private boolean isOver = false;

  private ArrayList<MatchObserver> observers = new ArrayList<>();
  private Rectangle lastBall;

  public Match(BallSprite ball, PlayerSprite leftPlayer, PlayerSprite rightPlayer)
  {
      this.ball = ball;
      this.leftPlayer = leftPlayer;
      this.rightPlayer = rightPlayer;
      lastBall = ball.getMyRectangle();
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
  {
      for (int i = 0; i < observers.size(); i++){
          observers.get(i).goalScored(e);
      }
  }

  private void notifyEnd(GoalEvent e)
  {
      for (int i = 0; i < observers.size(); i++){
          observers.get(i).matchEnded(e);
      }
  }


  public void update()
  {
      if (isOver){
          return;
      }
      leftPlayer.think(this);
      rightPlayer.think(this);
      ball.updateSprite();
      leftPlayer.updateSprite();
      rightPlayer.updateSprite();
      checkGoalRoof();
      checkGoal();
      lastBall = ball.getMyRectangle();
  }

  private void checkGoalRoof()
  {
      Rectangle r = ball.getMyRectangle();
      int center = r.x + r.width/2;
      if (center > LEFT_GOAL_LINE && center < RIGHT_GOAL_LINE){
          return;
      }
      if (lastBall.y + lastBall.height <= CROSSBAR_Y && r.y + r.height > CROSSBAR_Y){
          ball.bounceOn(CROSSBAR_Y);
      }
  }

  private void checkGoal()
  {
      Rectangle r = ball.getMyRectangle();
      if (r.y <= CROSSBAR_Y){
          return;
      }
      if (lastBall.x + lastBall.width >= LEFT_GOAL_LINE && r.x + r.width < LEFT_GOAL_LINE){
          goal(Side.RIGHT);
      }
      else if (lastBall.x <= RIGHT_GOAL_LINE && r.x > RIGHT_GOAL_LINE){
          goal(Side.LEFT);
      }
  }

  private void goal(Side scorer)
  {
      if (scorer == Side.LEFT){
          leftScore++;
      }
      else{
          rightScore++;
      }
      ball.initPosition();

      GoalEvent e = new GoalEvent(this, scorer, leftScore, rightScore);
      notifyGoal(e);

      if ((leftScore >= GOALS_TO_WIN) || (rightScore >= GOALS_TO_WIN)){
          isOver = true;
          notifyEnd(e);
      }
  }


  public int getLeftScore()
  {  return leftScore;  }

  public int getRightScore()
  {  return rightScore;  }

  public boolean isOver()
  {  return isOver;  }


  public Rectangle getBallBounds()
  {  return ball.getMyRectangle();  }

  public PlayerSprite getOpponent(PlayerSprite p)
  { 
    if (p == leftPlayer){
        return rightPlayer;
    }else{
        return leftPlayer;
    }

  }

  public PVector getBallVelocity()
  {
    return ball.velocity.get();
  }

  public int getGoalLineX(Side s)
  {
    if (s == Side.LEFT){
        return LEFT_GOAL_LINE;
    }else{
        return RIGHT_GOAL_LINE;
    }
  }


}
