package src.domain;
import java.awt.Color;

/**
 * Tiro de poder de un personaje: su daño base, cómo sale el balón,
 * cómo sigue mientras lleva el poder, el efecto que causa y el color de su aura.
 */
public interface AbilityStrategy
{
    /** @return daño base del tiro (DañoTiro), antes del multiplicador de Kick */
    int getBaseDamage();

    /**
     * Lanza el balón con la salida propia del poder. El balón ya sabe quién disparó.
     * @param ball balón que sale disparado
     */
    void launch(BallSprite ball);

    /**
     * Corrige el balón en cada ciclo mientras lleva el poder.
     * @param ball balón con el poder
     */
    void steer(BallSprite ball);

    /**
     * Crea el efecto que recibe el rival cuando el tiro lo golpea.
     * @return un efecto nuevo
     */
    PlayerState createEffect();

    /** @return color del aura del personaje con este poder */
    Color getColor();
}
