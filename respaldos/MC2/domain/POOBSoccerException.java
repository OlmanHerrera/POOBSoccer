package src.domain;

/**
 * Excepción propia del juego POOBSoccer.
 */
public class POOBSoccerException extends Exception
{
    /**
     * Crea la excepción con su mensaje.
     * @param message descripción del error
     */
    public POOBSoccerException(String message){
        super(message);
    }
}
