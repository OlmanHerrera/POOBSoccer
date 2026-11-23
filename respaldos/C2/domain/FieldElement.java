package src.domain;
import java.awt.Graphics;

/**
 * Elemento de la cancha que actúa sobre el balón y se dibuja (arco, travesaño...).
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

    /**
     * Dibuja el elemento sobre la cancha.
     * @param g donde se dibuja
     * @param field cancha de la que sale la geometría
     * @param loader cargador de imágenes
     */
    void draw(Graphics g, Field field, ImagesLoader loader);
}
