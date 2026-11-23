package src.domain;

/**
 * Excepción propia del juego, con mensajes para mostrar al usuario.
 */
public class POOBSoccerException extends Exception{
    public static final String UNKNOWN_COUNTRY = "No existe el personaje: ";
    public static final String UNKNOWN_PROFILE = "No existe el perfil de máquina: ";
    public static final String UNKNOWN_RULES = "No existe el tipo de partido: ";
    public static final String UNKNOWN_FIELD = "No existe la resolución: ";
    public static final String BAD_LIMIT = "Los minutos o goles deben estar entre ";
    public static final String UNKNOWN_STAT = "No existe la estadística: ";
    public static final String MAX_LEVEL_REACHED = "Nivel máximo alcanzado: ";
    public static final String NOT_ENOUGH_POINTS = "Puntos insuficientes para ";
    public static final String NO_MATCH_TO_SAVE = "No hay partido para guardar.";
    public static final String WRONG_EXTENSION = "El archivo debe tener extensión ";
    public static final String FILE_NOT_FOUND = "No existe el archivo: ";
    public static final String CANNOT_WRITE = "No se pudo escribir el archivo: ";
    public static final String CANNOT_READ = "No se pudo leer el archivo: ";
    public static final String DAMAGED_MATCH = "No es un partido guardado o está dañado: ";
    public static final String BAD_LINE = "Línea ";
    public static final String BAD_FORMAT = "debe ser jugador, dato, valor.";
    public static final String UNKNOWN_PLAYER = "el jugador debe ser Jugador1 o Jugador2, no ";
    public static final String NOT_A_NUMBER = "el valor debe ser un número entero, no ";
    public static final String BAD_POINTS = "Los puntos no pueden ser negativos: ";
    public static final String BAD_LEVEL = "El nivel debe estar entre 1 y 10: ";

    /**
     * Crea la excepción con su mensaje.
     * @param message descripción del error
     */
    public POOBSoccerException(String message){
        super(message);
    }

    /**
     * Crea la excepción con su mensaje y el error que la causó.
     * @param message descripción del error
     * @param cause error que la causó
     */
    public POOBSoccerException(String message, Exception cause){
        super(message, cause);
    }
}
