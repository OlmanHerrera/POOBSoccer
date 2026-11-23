package src.domain;

/**
 * Cancha de una resolución: su tamaño y la geometría que sale de él.
 * Las medidas se tomaron en la cancha de referencia de 500x400; para
 * cualquier otro tamaño se calculan con una regla de tres.
 */
public class Field
{
    private static final int REFERENCE_WIDTH = 500;
    private static final int REFERENCE_HEIGHT = 400;
    private static final int REFERENCE_FLOOR_Y = 341;
    private static final int REFERENCE_CROSSBAR_Y = 221;
    private static final int REFERENCE_LEFT_GOAL_X = 65;
    private static final int REFERENCE_RIGHT_GOAL_X = 435;
    private static final int REFERENCE_LEFT_START_X = 125;
    private static final int REFERENCE_RIGHT_START_X = 375;

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

    /** @return y del suelo, donde se apoyan el balón y los jugadores */
    public int getFloorY(){
        return scaleY(REFERENCE_FLOOR_Y);
    }

    /** @return y del travesaño de los dos arcos */
    public int getCrossbarY(){
        return scaleY(REFERENCE_CROSSBAR_Y);
    }

    /**
     * @param s lado del arco
     * @return x de la línea de gol de ese lado
     */
    public int getGoalLineX(Side s){
        if (s == Side.LEFT){
            return scaleX(REFERENCE_LEFT_GOAL_X);
        }
        return scaleX(REFERENCE_RIGHT_GOAL_X);
    }

    /**
     * @param s lado del jugador
     * @return x donde arranca el jugador de ese lado
     */
    public int getStartX(Side s){
        if (s == Side.LEFT){
            return scaleX(REFERENCE_LEFT_START_X);
        }
        return scaleX(REFERENCE_RIGHT_START_X);
    }

    /**
     * Regla de tres: pasa una x de la cancha de referencia a esta cancha.
     * @param referenceX x medida en la cancha de 500x400
     * @return x equivalente en esta cancha
     */
    private int scaleX(int referenceX){
        return referenceX * width / REFERENCE_WIDTH;
    }

    /**
     * Regla de tres: pasa una y de la cancha de referencia a esta cancha.
     * @param referenceY y medida en la cancha de 500x400
     * @return y equivalente en esta cancha
     */
    private int scaleY(int referenceY){
        return referenceY * height / REFERENCE_HEIGHT;
    }
}
