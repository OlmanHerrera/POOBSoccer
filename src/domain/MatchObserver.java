package src.domain;

/**
 * Recibe los avisos de goles y del final de un partido.
 */
public interface MatchObserver{
    /**
     * Hubo un gol.
     * @param e lado que marcó
     */
    void goalScored(GoalEvent e);

    /**
     * Terminó el partido.
     * @param e lado ganador
     */
    void matchEnded(GoalEvent e);
}
