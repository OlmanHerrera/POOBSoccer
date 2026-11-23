package src.domain;


/**
 * Datos que el partido les manda a sus observadores cuando hay un gol
 * o cuando termina: el lado (el que marcó, o el ganador al final) y el marcador.
 */
public class GoalEvent
{
  private Match source;
  private Side scorer;
  private int leftScore, rightScore;

  /**
   * Crea el aviso.
   * @param source partido que avisa
   * @param scorer lado que marcó, o el ganador si es el aviso de fin
   * @param leftScore goles del jugador izquierdo
   * @param rightScore goles del jugador derecho
   */
  public GoalEvent(Match source, Side scorer, int leftScore, int rightScore)
  {
      this.source = source;
      this.scorer = scorer;
      this.leftScore = leftScore;
      this.rightScore = rightScore;
  }

  /** @return partido que avisa */
  public Match getSource()
  {  return source;  }

  /** @return lado que marcó, o el ganador si es el aviso de fin */
  public Side getScorer()
  {  return scorer;  }

  /** @return goles del jugador izquierdo */
  public int getLeftScore()
  {  return leftScore;  }

  /** @return goles del jugador derecho */
  public int getRightScore()
  {  return rightScore;  }
}
