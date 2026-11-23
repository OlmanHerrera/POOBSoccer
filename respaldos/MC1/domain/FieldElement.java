package src.domain;

/**
 * Elemento de la cancha que actúa sobre el balón (arco, travesaño...).
 */
public interface FieldElement
{
    /**
     * Revisa el balón en este ciclo y actúa si hace falta.
     * @param ball balón
     * @param field cancha de la que sale la geometría
     * @param match partido, para avisar un gol
     */
    void interact(BallSprite ball, Field field, Match match);
}
