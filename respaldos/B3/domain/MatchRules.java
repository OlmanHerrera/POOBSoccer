package src.domain;

/**
 * Regla de un tipo de partido: qué significa su límite, cuándo termina,
 * quién gana y qué se muestra del tiempo.
 */
public interface MatchRules
{
    /** @return nombre del tipo de partido */
    String getId();

    /** @return qué cuenta el límite del partido, por ejemplo "minutos" o "goles" */
    String getLimitName();

    /** @return límite que se propone si el usuario no escoge otro */
    int getDefaultLimit();

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

    /**
     * @param m partido actual
     * @return texto del tiempo para mostrar en pantalla
     */
    String getStatusText(Match m);
}
