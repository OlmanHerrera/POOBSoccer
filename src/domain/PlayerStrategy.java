package src.domain;

/**
 * Perfil de un jugador máquina: decide qué hacer en cada ciclo del juego.
 */
public interface PlayerStrategy{
    /** @return nombre del perfil, con el que se registra y se guarda */
    String getId();

    /**
     * @param me jugador que decide
     * @param m partido actual
     * @return acción que quiere hacer, o null si no hace nada
     */
    Action decide(Player me, Match m);
}
