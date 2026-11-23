package src.domain;
import java.awt.Color;

/**
 * Tiro de poder de Estados Unidos, Ilusión: el balón se divide en 8 balones
 * idénticos; 7 son falsos y desaparecen al terminar el efecto.
 * Si el balón real golpea al rival, lo confunde.
 */
public class DeceptiveShot implements AbilityStrategy
{
    private static final int DAMAGE = 30;
    private static final float SPEED_X = 6;
    private static final float SPEED_Y = 3;
    private static final int FAKE_BALLS = 7;
    private static final Color COLOR = new Color(230, 40, 60);

    /** @return daño base de la Ilusión */
    public int getBaseDamage(){
        return DAMAGE;
    }

    /**
     * Sale hacia el arco rival y aparecen los 7 balones falsos.
     * @param ball balón que sale disparado
     */
    public void launch(BallSprite ball){
        ball.shoot(ball.getPowerDirection() * SPEED_X, -SPEED_Y);
        ball.addDecoys(FAKE_BALLS, ConfusedState.DURATION_SECONDS);
    }

    /**
     * No corrige nada: el balón real vuela normal entre los falsos.
     * @param ball balón con el poder
     */
    public void steer(BallSprite ball){
    }

    /** @return efecto de confusión */
    public PlayerState createEffect(){
        return new ConfusedState();
    }

    /** @return rojo */
    public Color getColor(){
        return COLOR;
    }
}
