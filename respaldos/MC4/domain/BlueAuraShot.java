package src.domain;

/**
 * Tiro de poder: Aura Azul de Corea.
 */
public class BlueAuraShot implements AbilityStrategy
{
    /** @return daño base del tiro */
    public int getBaseDamage(){
        return 0; // pendiente MC4
    }

    /**
     * Lanza el balón con su trayectoria.
     * @param ball balón que sale disparado
     * @param shooter lado del jugador que dispara
     */
    public void launch(BallSprite ball, Side shooter){
        // pendiente MC4
    }

    /** @return efecto que recibe el rival */
    public PlayerState createEffect(){
        return null; // pendiente MC4
    }
}
