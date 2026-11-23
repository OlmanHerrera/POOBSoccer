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

  public static final int SECONDS_PER_MINUTE = 60;
  private static final int WINNER_POINTS = 100;
  private static final int LOSER_POINTS = 25;
  private static final String BACKGROUND = "background";
  private static final String TIME_FORMAT = "%02d:%02d";

  private Field field;
  private MatchRules rules;
  private int limit;
  private ArrayList<FieldElement> elements;
  private ImagesLoader loader;
  private BallSprite ball;
  private Player leftPlayer, rightPlayer;
  private int updatesPerSecond;

  private int elapsedUpdates = 0;
  private boolean paused = false;
  private boolean isOver = false;
  private Side winner = null;

  private ArrayList<MatchObserver> observers = new ArrayList<>();

  /**
   * Crea un partido. El partido crea su balón.
   * @param field cancha
   * @param rules tipo de partido
   * @param limit minutos o goles, según el tipo de partido
   * @param elements elementos de la cancha que actúan sobre el balón
   * @param leftPlayer jugador izquierdo, ya preparado para el partido
   * @param rightPlayer jugador derecho, ya preparado para el partido
   * @param loader cargador de imágenes
   * @param updatesPerSecond ciclos del bucle por segundo
   */
  public Match(Field field, MatchRules rules, int limit, ArrayList<FieldElement> elements,
               Player leftPlayer, Player rightPlayer, ImagesLoader loader, int updatesPerSecond)
  {
      this.field = field;
      this.rules = rules;
      this.limit = limit;
      this.elements = elements;
      this.leftPlayer = leftPlayer;
      this.rightPlayer = rightPlayer;
      this.loader = loader;
      this.updatesPerSecond = updatesPerSecond;
      ball = new BallSprite(field, loader, leftPlayer.getSprite(), rightPlayer.getSprite(), updatesPerSecond);
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
   * Avanza el partido un ciclo: corre el reloj, los jugadores deciden, todo se mueve,
   * se revisan patadas y poderes, los elementos de la cancha revisan el balón
   * y las reglas dicen si terminó.
   */
  public void update()
  {
      if (isOver || paused){
          return;
      }
      elapsedUpdates++;
      leftPlayer.think(this);
      rightPlayer.think(this);
      ball.updateSprite();
      leftPlayer.update(updatesPerSecond);
      rightPlayer.update(updatesPerSecond);
      leftPlayer.tryKick(rightPlayer);
      rightPlayer.tryKick(leftPlayer);
      checkPowerShot(leftPlayer);
      checkPowerShot(rightPlayer);
      checkPowerHit();
      checkElements();
      if (rules.isOver(this)){
          finish();
      }
  }

  /**
   * Si el jugador tiene el aura y toca el balón, ejecuta su tiro de poder.
   * @param p jugador
   */
  private void checkPowerShot(Player p)
  {
      if (!p.isAuraActive()){
          return;
      }
      if (!ball.getMyRectangle().intersects(p.getBounds())){
          return;
      }
      p.shootPower(ball);
  }

  /**
   * Si el balón con poder golpea al rival de quien lo disparó, se aplican
   * el efecto y el daño (DañoTiro x multiplicadorKick) y el balón pierde el poder.
   */
  private void checkPowerHit()
  {
      AbilityStrategy power = ball.getPower();
      if (power == null){
          return;
      }
      Player shooter = getPlayer(ball.getPowerOwner());
      Player rival = getOpponent(shooter);
      if (!ball.getMyRectangle().intersects(rival.getBounds())){
          return;
      }
      rival.applyEffect(power.createEffect());
      rival.takeDamage(power.getBaseDamage() * shooter.getKickMultiplier());
      ball.discharge();
  }

  /**
   * Termina el partido con el ganador que dicen las reglas, le da 100 puntos al
   * ganador y 25 al perdedor, y avisa a los observadores.
   */
  private void finish()
  {
      isOver = true;
      winner = rules.winner(this);
      Player winnerPlayer = getPlayer(winner);
      winnerPlayer.addPoints(WINNER_POINTS);
      getOpponent(winnerPlayer).addPoints(LOSER_POINTS);
      notifyEnd(new GoalEvent(this, winner, leftPlayer.getGoals(), rightPlayer.getGoals()));
  }

  /** Pausa el partido: el reloj y el juego se detienen. */
  public void pause()
  {  paused = true;  }

  /** Reanuda el partido. */
  public void resume()
  {  paused = false;  }

  /** @return true si el partido está en pausa */
  public boolean isPaused()
  {  return paused;  }

  /** Los jugadores terminan el partido antes de tiempo: no hay ganador. */
  public void abandon()
  {
      isOver = true;
      winner = null;
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
   * Suma el gol, sube un cuarto la barra de quien lo recibió, devuelve el balón al centro
   * y a los jugadores a su posición, y avisa a los observadores.
   * Lo llama el elemento de la cancha que detecta el gol.
   * @param scorer lado que marcó
   */
  public void goal(Side scorer)
  {
      Player p = getPlayer(scorer);
      p.scoreGoal();
      getOpponent(p).concede();
      ball.initPosition();
      leftPlayer.returnToStart();
      rightPlayer.returnToStart();

      notifyGoal(new GoalEvent(this, scorer, leftPlayer.getGoals(), rightPlayer.getGoals()));
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

  /** @return lado ganador, o null si no hubo ganador o no ha terminado */
  public Side getWinner()
  {  return winner;  }

  /** @return lado que va ganando, o null si van empatados */
  public Side getLeader()
  {
      if (leftPlayer.getGoals() > rightPlayer.getGoals()){
          return Side.LEFT;
      }
      if (rightPlayer.getGoals() > leftPlayer.getGoals()){
          return Side.RIGHT;
      }
      return null;
  }

  /** @return minutos o goles del límite del partido */
  public int getLimit()
  {  return limit;  }

  /** @return segundos jugados, contados en ciclos del bucle */
  public int getElapsedSeconds()
  {  return elapsedUpdates / updatesPerSecond;  }

  /** @return texto del tiempo según el tipo de partido */
  public String getStatusText()
  {  return rules.getStatusText(this);  }

  /**
   * @param seconds segundos
   * @return el tiempo como mm:ss
   */
  public String formatTime(int seconds)
  {
      return String.format(TIME_FORMAT, seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
  }

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
