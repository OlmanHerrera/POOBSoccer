package src.domain;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Los dos arcos: hay gol cuando el balón cruza una línea de gol
 * por debajo del travesaño.
 */
public class Goal implements FieldElement
{
    private static final String LEFT_IMAGE = "goal_left";
    private static final String RIGHT_IMAGE = "goal_right";

    /**
     * Dibuja los dos arcos con el tamaño de la cancha.
     * @param g donde se dibuja
     * @param field cancha de la que salen la posición y el tamaño de los arcos
     * @param loader cargador de imágenes
     */
    public void draw(Graphics g, Field field, ImagesLoader loader){
        int top = field.getGoalTopY();
        int w = field.getGoalWidth();
        int h = field.getGoalHeight();
        g.drawImage(loader.getImage(LEFT_IMAGE), 0, top, w, h, null);
        g.drawImage(loader.getImage(RIGHT_IMAGE), field.getWidth() - w, top, w, h, null);
    }

    /**
     * Compara la posición anterior y la actual del balón con cada línea de gol.
     * @param ball balón
     * @param field cancha de la que salen las líneas de gol y el travesaño
     * @param match partido al que se le avisa el gol
     */
    public void interact(BallSprite ball, Field field, Match match){
        Rectangle r = ball.getMyRectangle();
        Rectangle last = ball.getPreviousRectangle();
        int leftLine = field.getGoalLineX(Side.LEFT);
        int rightLine = field.getGoalLineX(Side.RIGHT);
        if (r.y <= field.getCrossbarY()){
            return;
        }
        if (last.x + last.width >= leftLine && r.x + r.width < leftLine){
            match.goal(Side.RIGHT);
        }
        else if (last.x <= rightLine && r.x > rightLine){
            match.goal(Side.LEFT);
        }
    }
}
