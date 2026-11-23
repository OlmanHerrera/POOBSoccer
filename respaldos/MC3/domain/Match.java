package src.domain;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Partido entre dos jugadores: coordina cada ciclo del juego, cuenta los goles
 * y avisa a sus observadores.
 */
public class Match
{

  private static final int GOALS_TO_WIN = 5; // pendiente MC3
  private static final String BACKGROUND = "background";

  private Field field;
  private ArrayList<FieldElement> elements;
  private ImagesLoader loader;
  private BallSprite ball;
  private Player leftPlayer, rightPlayer;
  private int updatesPerSecond;

  private boolean isOver = false;

  private ArrayList<MatchObserver> observers = new ArrayList<>();

  /**
   * Crea un partido. El partido crea su balón.
   * @param field cancha
   * @param elements elementos de la cancha que actúan sobre el balón
   * @param leftPlayer jugador izquierdo, ya preparado para el partido
   * @param rightPlayer jugador derecho, ya preparado para el partido
   * @param loader cargador de imágenes
   * @param updatesPerSecond ciclos del bucle por segundo
   */
  public Match(Field field, ArrayList<FieldElement> elements, Player leftPlayer, Player rightPlayer,
               ImagesLoader loader, int updatesPerSecond)
  {
      this.field = field;
      this.elements = elements;
      this.leftPlayer = leftPlayer;
      this.rightPlayer = rightPlayer;
      this.loader = loader;
      this.updatesPerSecond = updatesPerSecond;
      ball = new BallSprite(field, loader, leftPlayer.getSprite(), rightPlayer.getSprite());
  }

  /** @param o observador que quiere saber de los goles y del final */
  public void addObserver(MatchObserver o)
  {
      if (!observers.contains(o)){
          observers.add(o);
      }
  }

  /** @param o observador que ya no quiere avisos */
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

  /**
   * Avanza el partido un ciclo: los jugadores deciden, todo se mueve
   * y los elementos de la cancha revisan el balón.
   */
  public void update()
  {
      if (isOver){
          return;
      }
      leftPlayer.think(this);
      rightPlayer.think(this);
      ball.updateSprite();
      leftPlayer.update(updatesPerSecond);
      rightPlayer.update(updatesPerSecond);
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
   * Suma el gol, devuelve el balón al centro y a los jugadores a su posición,
   * y avisa a los observadores. Lo llama el elemento de la cancha que detecta el gol.
   * @param scorer lado que marcó
   */
  public void goal(Side scorer)
  {
      Player p = getPlayer(scorer);
      p.scoreGoal();
      ball.initPosition();
      leftPlayer.returnToStart();
      rightPlayer.returnToStart();

      GoalEvent e = new GoalEvent(this, scorer, leftPlayer.getGoals(), rightPlayer.getGoals());
      notifyGoal(e);

      if (p.getGoals() >= GOALS_TO_WIN){
          isOver = true;
          notifyEnd(e);
      }
  }

  /**
   * Dibuja la cancha, el balón, los jugadores y los elementos de la cancha.
   * @param g donde se dibuja
   */
  public void draw(Graphics g)
  {
      g.drawImage(loader.getImage(BACKGROUND), 0, 0, field.getWidth(), field.getHeight(), null);
      ball.drawSprite(g);
      leftPlayer.draw(g);
      rightPlayer.draw(g);
      for (int i = 0; i < elements.size(); i++){
          elements.get(i).draw(g, field, loader);
      }
  }

  /**
   * @param s lado
   * @return jugador de ese lado
   */
  public Player getPlayer(Side s)
  {
    if (s == Side.LEFT){
        return leftPlayer;
    }
    return rightPlayer;
  }

  /**
   * @param s lado
   * @return goles del jugador de ese lado
   */
  public int getGoals(Side s)
  {  return getPlayer(s).getGoals();  }

  /** @return true si el partido ya terminó */
  public boolean isOver()
  {  return isOver;  }

  /** @return rectángulo del balón */
  public Rectangle getBallBounds()
  {  return ball.getMyRectangle();  }

  /**
   * @param p un jugador del partido
   * @return el otro jugador
   */
  public Player getOpponent(Player p)
  {
    if (p == leftPlayer){
        return rightPlayer;
    }else{
        return leftPlayer;
    }

  }

  /** @return copia de la velocidad del balón */
  public PVector getBallVelocity()
  {
    return ball.velocity.get();
  }

  /**
   * @param s lado del arco
   * @return x de la línea de gol de ese lado
   */
  public int getGoalLineX(Side s)
  {
    return field.getGoalLineX(s);
  }


}
