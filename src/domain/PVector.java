package src.domain;

/**
 * Vector de dos dimensiones para posiciones, velocidades y fuerzas.
 */
public class PVector{
    float x;
    float y;

    /**
     * Crea el vector.
     * @param x componente en x
     * @param y componente en y
     */
    public PVector(float x, float y){
        this.x = x;
        this.y = y;
    }

    /** @param v vector que se suma */
    public void add(PVector v){
        x = x + v.x;
        y = y + v.y;
    }

    /** @param v vector que se resta */
    public void sub(PVector v){
        x = x - v.x;
        y = y - v.y;
    }

    /** @param n número por el que se multiplica */
    public void mult(float n){
        x = x * n;
        y = y * n;
    }

    /** @param n número por el que se divide */
    public void div(float n){
        x = x / n;
        y = y / n;
    }

    /** @return magnitud del vector */
    public float mag(){
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Deja el vector con magnitud 1 sin cambiar su dirección. */
    public void normalize(){
        float m = mag();
        if (m != 0){
            div(m);
        }
    }

    /** @param max magnitud máxima que puede tener */
    public void limit(float max){
        if (mag() > max){
            normalize();
            mult(max);
        }
    }

    /** @return copia del vector */
    public PVector get(){
        return new PVector(x, y);
    }
}
