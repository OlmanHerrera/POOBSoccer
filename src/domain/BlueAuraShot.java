package src.domain;
import java.awt.Color;

/**
 * Tiro de poder de Corea del Sur, Aura Azul: sale recto y rápido y empuja al rival hacia su arco.
 */
public class BlueAuraShot implements AbilityStrategy{
    private static final int DAMAGE = 40;
    private static final float SPEED = 9;
    private static final Color COLOR = new Color(40, 120, 255);

    /** @return daño base del Aura Azul */
    public int getBaseDamage(){
        return DAMAGE;
    }

    /**
     * Sale recto y horizontal hacia el arco rival.
     * @param ball balón que sale disparado
     */
    public void launch(BallSprite ball){
        ball.shoot(ball.getPowerDirection() * SPEED, 0);
    }

    /**
     * Mantiene el tiro recto: no cae y no pierde velocidad.
     * @param ball balón con el poder
     */
    public void steer(BallSprite ball){
        ball.keepStraight(SPEED);
    }

    /** @return efecto de empuje */
    public PlayerState createEffect(){
        return new PushedState();
    }

    /** @return azul */
    public Color getColor(){
        return COLOR;
    }
}
