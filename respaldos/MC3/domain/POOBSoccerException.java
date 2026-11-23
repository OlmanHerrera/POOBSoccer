package src.domain;

/**
 * Excepción propia del juego POOBSoccer. Sus mensajes son para mostrárselos
 * al usuario tal cual.
 */
public class POOBSoccerException extends Exception
{
    public static final String UNKNOWN_COUNTRY = "No existe el personaje: ";
    public static final String UNKNOWN_PROFILE = "No existe el perfil de máquina: ";
    public static final String UNKNOWN_RULES = "No existe el tipo de partido: ";
    public static final String UNKNOWN_FIELD = "No existe la resolución de cancha: ";

    /**
     * Crea la excepción con su mensaje.
     * @param message descripción del error
     */
    public POOBSoccerException(String message){
        super(message);
    }
}
