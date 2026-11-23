package src.domain;

/**
 * Tiro de poder: Rayo de Camerún.
 */
public class LightningShot implements AbilityStrategy
{
    /** @return daño base del tiro */
    public int getBaseDamage(){
        return 0; // pendiente MC5
    }

    /**
     * Lanza el balón con su trayectoria.
     * @param ball balón que sale disparado
     * @param shooter lado del jugador que dispara
     */
    public void launch(BallSprite ball, Side shooter){
        // pendiente MC5
    }

    /** @return efecto que recibe el rival */
    public PlayerState createEffect(){
        return null; // pendiente MC5
    }
}
