package src.domain;
import java.util.Random;

/**
 * Perfil torpe: escoge al azar sus movimientos, cambiando cada cierto número de ciclos.
 */
public class ClumsyProfile implements PlayerStrategy
{
    private Random random;
    private int updatesCounter;
    private Action current;
    public static final int MAX_UPDATES = 50;

    /**
     * Crea el perfil empezando a moverse a la izquierda.
     */
    public ClumsyProfile(){
        random = new Random();
        updatesCounter= 0;
        current = Action.MOVE_LEFT;
    }

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return una acción escogida al azar
     */
    public Action decide(Player p, Match m){
        updatesCounter++;
        if (updatesCounter  == MAX_UPDATES){
            current = Action.values()[random.nextInt(Action.values().length)];
            updatesCounter = 0;
        }

        return current;
    }

}
