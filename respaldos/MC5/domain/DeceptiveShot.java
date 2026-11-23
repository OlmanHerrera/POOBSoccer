package src.domain;
import java.awt.Color;

/**
 * Tiro de poder: Ilusión de Estados Unidos.
 */
public class DeceptiveShot implements AbilityStrategy
{
    /** @return daño base del tiro */
    public int getBaseDamage(){
        return 0; // pendiente MC5
    }

    /**
     * Lanza el balón con su salida.
     * @param ball balón que sale disparado
     */
    public void launch(BallSprite ball){
        // pendiente MC5
    }

    /**
     * Corrige el balón mientras lleva el poder.
     * @param ball balón con el poder
     */
    public void steer(BallSprite ball){
        // pendiente MC5
    }

    /** @return efecto que recibe el rival */
    public PlayerState createEffect(){
        return null; // pendiente MC5
    }

    /** @return color del aura */
    public Color getColor(){
        return null; // pendiente MC5
    }
}
