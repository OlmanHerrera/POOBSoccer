package src.domain;
import java.awt.Color;

/**
 * Tiro de poder de Camerún, Rayo: sale recto, cubierto de electricidad.
 * Si golpea al rival, lo electrocuta.
 */
public class LightningShot implements AbilityStrategy
{
    private static final int DAMAGE = 60;
    private static final float SPEED = 8;
    private static final Color COLOR = new Color(255, 220, 0);

    /** @return daño base del Rayo */
    public int getBaseDamage(){
        return DAMAGE;
    }

    /**
     * Sale recto hacia el arco rival.
     * @param ball balón que sale disparado
     */
    public void launch(BallSprite ball){
        ball.shoot(ball.getPowerDirection() * SPEED, 0);
    }

    /**
     * Mantiene el tiro recto.
     * @param ball balón con el poder
     */
    public void steer(BallSprite ball){
        ball.keepStraight(SPEED);
    }

    /** @return efecto de electrocutado */
    public PlayerState createEffect(){
        return new ElectrocutedState();
    }

    /** @return amarillo eléctrico */
    public Color getColor(){
        return COLOR;
    }
}
