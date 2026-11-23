package src.domain;
import java.io.Serializable;

/**
 * Posición y velocidad de un cuerpo en la cancha, guardadas para poder
 * reconstruir su sprite al abrir un partido.
 */
public class Motion implements Serializable
{
    private static final long serialVersionUID = 1L;

    private float x;
    private float y;
    private float vx;
    private float vy;

    /**
     * Crea el registro de movimiento.
     * @param x posición en x
     * @param y posición en y
     * @param vx velocidad en x
     * @param vy velocidad en y
     */
    public Motion(float x, float y, float vx, float vy){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /** @return posición en x */
    public float getX(){
        return x;
    }

    /** @return posición en y */
    public float getY(){
        return y;
    }

    /** @return velocidad en x */
    public float getVx(){
        return vx;
    }

    /** @return velocidad en y */
    public float getVy(){
        return vy;
    }
}
