package src.domain;

/**
 * Tiro de poder de un país: su daño base, su trayectoria y el efecto que causa.
 */
public interface AbilityStrategy
{
    /** @return daño base del tiro, antes del multiplicador de Kick */
    int getBaseDamage();

    /**
     * Lanza el balón con la trayectoria propia del poder.
     * @param ball balón que sale disparado
     * @param shooter lado del jugador que dispara
     */
    void launch(BallSprite ball, Side shooter);

    /**
     * Crea el efecto que recibe el rival cuando el tiro lo golpea.
     * @return un efecto nuevo
     */
    PlayerState createEffect();
}
