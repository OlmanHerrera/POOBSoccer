package src.domain;


/**
 * Quien quiere enterarse de los goles y del final de un partido (patrón Observador).
 */
public interface MatchObserver
{
  /**
   * Hubo un gol.
   * @param e lado que marcó y marcador
   */
  void goalScored(GoalEvent e);

  /**
   * Terminó el partido.
   * @param e lado ganador y marcador final
   */
  void matchEnded(GoalEvent e);
}
