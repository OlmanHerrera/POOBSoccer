package src.domain;

/**
 * Cancha de una resolución: su tamaño y la geometría que sale de él.
 */
public class Field
{
    private String id;
    private int width;
    private int height;

    /**
     * Crea una cancha.
     * @param id identificador de la resolución
     * @param width ancho en píxeles
     * @param height alto en píxeles
     */
    public Field(String id, int width, int height){
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /** @return identificador de la resolución */
    public String getId(){
        return id;
    }

    /** @return ancho en píxeles */
    public int getWidth(){
        return width;
    }

    /** @return alto en píxeles */
    public int getHeight(){
        return height;
    }

    /** @return y del suelo */
    public int getFloorY(){
        return 0; // pendiente MC1
    }

    /** @return y del travesaño */
    public int getCrossbarY(){
        return 0; // pendiente MC1
    }

    /**
     * @param s lado del arco
     * @return x de la línea de gol de ese lado
     */
    public int getGoalLineX(Side s){
        return 0; // pendiente MC1
    }

    /**
     * @param s lado del jugador
     * @return x donde arranca el jugador de ese lado
     */
    public int getStartX(Side s){
        return 0; // pendiente MC1
    }
}
