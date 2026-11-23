package src.domain;
import java.util.Random;

/**
 * Perfil torpe: escoge al azar sus movimientos, cambiando cada cierto número de ciclos.
 */
public class ClumsyProfile implements PlayerStrategy{
    private static final String ID = "Torpe";
    private static final int MAX_UPDATES = 50;

    private Random random;
    private int counter;
    private Action current;

    /** Crea el perfil empezando a moverse a la izquierda. */
    public ClumsyProfile(){
        random = new Random();
        counter = 0;
        current = Action.MOVE_LEFT;
    }

    /** @return nombre del perfil */
    public String getId(){
        return ID;
    }

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return una acción escogida al azar
     */
    public Action decide(Player p, Match m){
        counter++;
        if (counter == MAX_UPDATES){
            current = Action.values()[random.nextInt(Action.values().length)];
            counter = 0;
        }
        return current;
    }
}
