package src.domain;

/**
 * Partido por goles: gana el primero que llegue a un número de goles, sin límite de tiempo.
 */
public class GoalsRules implements MatchRules
{
    private static final String ID = "Por goles";
    private static final String LIMIT_NAME = "goles";
    private static final int DEFAULT_GOALS = 5;
    private static final String GOAL_TARGET = "Meta de goles: ";

    /** @return nombre del tipo de partido */
    public String getId(){
        return ID;
    }

    /** @return el límite se cuenta en goles */
    public String getLimitName(){
        return LIMIT_NAME;
    }

    /** @return goles propuestos para ganar */
    public int getDefaultLimit(){
        return DEFAULT_GOALS;
    }

    /**
     * @param m partido actual
     * @return true si algún jugador llegó a los goles del límite
     */
    public boolean isOver(Match m){
        if (m.getGoals(Side.LEFT) >= m.getLimit()){
            return true;
        }
        return m.getGoals(Side.RIGHT) >= m.getLimit();
    }

    /**
     * @param m partido terminado
     * @return lado que llegó primero a los goles
     */
    public Side winner(Match m){
        return m.getLeader();
    }

    /**
     * @param m partido actual
     * @return meta de goles y tiempo jugado
     */
    public String getStatusText(Match m){
        return GOAL_TARGET + m.getLimit() + " - " + m.formatTime(m.getElapsedSeconds());
    }
}
