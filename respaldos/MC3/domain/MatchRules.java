package src.domain;

/**
 * Regla de un tipo de partido: cuándo termina y quién gana.
 */
public interface MatchRules
{
    /** @return identificador del tipo de partido */
    String getId();

    /**
     * @param m partido actual
     * @return true si el partido terminó
     */
    boolean isOver(Match m);

    /**
     * @param m partido terminado
     * @return lado ganador
     */
    Side winner(Match m);
}
