package src.domain;

/**
 * Partido por tiempo: dura un número de minutos y gana quien tenga más goles.
 * Si al acabar el tiempo hay empate, sigue en muerte súbita hasta el siguiente gol.
 */
public class TimedRules implements MatchRules
{
    private static final String ID = "Por tiempo";
    private static final String LIMIT_NAME = "minutos";
    private static final int DEFAULT_MINUTES = 3;
    private static final String SUDDEN_DEATH = "Muerte súbita";

    /** @return nombre del tipo de partido */
    public String getId(){
        return ID;
    }

    /** @return el límite se cuenta en minutos */
    public String getLimitName(){
        return LIMIT_NAME;
    }

    /** @return minutos propuestos */
    public int getDefaultLimit(){
        return DEFAULT_MINUTES;
    }

    /**
     * Termina cuando se acabó el tiempo y alguien va ganando.
     * Con empate no termina: eso es la muerte súbita.
     * @param m partido actual
     * @return true si el partido terminó
     */
    public boolean isOver(Match m){
        if (m.getElapsedSeconds() < limitSeconds(m)){
            return false;
        }
        return m.getLeader() != null;
    }

    /**
     * @param m partido terminado
     * @return lado con más goles
     */
    public Side winner(Match m){
        return m.getLeader();
    }

    /**
     * @param m partido actual
     * @return tiempo que falta; con el tiempo cumplido y empate, el aviso de muerte súbita
     */
    public String getStatusText(Match m){
        int secondsLeft = limitSeconds(m) - m.getElapsedSeconds();
        if (secondsLeft > 0){
            return m.formatTime(secondsLeft);
        }
        if (m.getLeader() == null){
            return SUDDEN_DEATH;
        }
        return m.formatTime(0);
    }

    /**
     * @param m partido actual
     * @return duración del partido en segundos
     */
    private int limitSeconds(Match m){
        return m.getLimit() * Match.SECONDS_PER_MINUTE;
    }
}
