package src.domain;

/**
 * Aviso de un gol o del final de un partido.
 */
public class GoalEvent{
    private Side scorer;

    /**
     * Crea el aviso.
     * @param scorer lado que marcó, o el ganador si es el aviso del final
     */
    public GoalEvent(Side scorer){
        this.scorer = scorer;
    }

    /** @return lado que marcó, o el ganador si es el aviso del final */
    public Side getScorer(){
        return scorer;
    }
}
