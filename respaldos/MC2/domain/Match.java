package src.domain;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Match
{

  private static final int GOALS_TO_WIN = 5;

  private Field field;
  private ArrayList<FieldElement> elements;
  private BallSprite ball;
  private PlayerSprite leftPlayer, rightPlayer;

  private int leftScore = 0;
  private int rightScore = 0;
  private boolean isOver = false;

  private ArrayList<MatchObserver> observers = new ArrayList<>();

  /**
   * Crea un partido.
   * @param field cancha
   * @param elements elementos de la cancha que actúan sobre el balón
   * @param ball balón
   * @param leftPlayer jugador izquierdo
   * @param rightPlayer jugador derecho
   */
  public Match(Field field, ArrayList<FieldElement> elements, BallSprite ball,
               PlayerSprite leftPlayer, PlayerSprite rightPlayer)
  {
      this.field = field;
      this.elements = elements;
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
      checkElements();
  }

  /**
   * Cada elemento de la cancha revisa el balón en este ciclo.
   */
  private void checkElements()
  {
      for (int i = 0; i < elements.size(); i++){
          elements.get(i).interact(ball, field, this);
      }
  }

  /**
   * Suma el gol, vuelve el balón al centro y avisa a los observadores.
   * Lo llama el elemento de la cancha que detecta el gol.
   * @param scorer lado que marcó
   */
  public void goal(Side scorer)
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
    return field.getGoalLineX(s);
  }


}
