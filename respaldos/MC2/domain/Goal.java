package src.domain;
import java.awt.Rectangle;

/**
 * Los dos arcos: hay gol cuando el balón cruza una línea de gol
 * por debajo del travesaño.
 */
public class Goal implements FieldElement
{
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
