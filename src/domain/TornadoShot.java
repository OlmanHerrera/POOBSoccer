package src.domain;
import java.awt.Color;

/**
 * Tiro de poder de Nigeria, Tornado: un remolino a ras de suelo que deja inconsciente al rival.
 */
public class TornadoShot implements AbilityStrategy{
    private static final int DAMAGE = 40;
    private static final float SPEED = 6;
    private static final float DROP_SPEED = 3;
    private static final float HOP_SPEED = 2.5f;
    private static final Color COLOR = new Color(40, 200, 90);

    /** @return daño base del Tornado */
    public int getBaseDamage(){
        return DAMAGE;
    }

    /**
     * Sale hacia el arco rival bajando al suelo.
     * @param ball balón que sale disparado
     */
    public void launch(BallSprite ball){
        ball.shoot(ball.getPowerDirection() * SPEED, DROP_SPEED);
    }

    /**
     * Avanza a ras de suelo dando saltos cortos.
     * @param ball balón con el poder
     */
    public void steer(BallSprite ball){
        ball.rollOnFloor(SPEED, HOP_SPEED);
    }

    /** @return efecto de inconsciente */
    public PlayerState createEffect(){
        return new UnconsciousState();
    }

    /** @return verde */
    public Color getColor(){
        return COLOR;
    }
}
