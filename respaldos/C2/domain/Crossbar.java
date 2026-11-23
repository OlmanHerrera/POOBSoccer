package src.domain;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Travesaño de los dos arcos: el balón que cae encima de un arco rebota en él.
 */
public class Crossbar implements FieldElement
{
    /**
     * No dibuja nada: el travesaño ya viene en la imagen de cada arco.
     * @param g donde se dibuja
     * @param field cancha
     * @param loader cargador de imágenes
     */
    public void draw(Graphics g, Field field, ImagesLoader loader){
    }

    /**
     * Si el balón está sobre un arco y acaba de cruzar el travesaño desde arriba, rebota.
     * @param ball balón
     * @param field cancha de la que salen las líneas de gol y el travesaño
     * @param match partido actual (el travesaño no lo necesita)
     */
    public void interact(BallSprite ball, Field field, Match match){
        Rectangle r = ball.getMyRectangle();
        Rectangle last = ball.getPreviousRectangle();
        int center = r.x + r.width/2;
        int crossbarY = field.getCrossbarY();
        if (center > field.getGoalLineX(Side.LEFT) && center < field.getGoalLineX(Side.RIGHT)){
            return;
        }
        if (last.y + last.height <= crossbarY && r.y + r.height > crossbarY){
            ball.bounceOn(crossbarY);
        }
    }
}
