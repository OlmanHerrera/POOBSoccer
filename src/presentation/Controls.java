package src.presentation;
import src.domain.Action;
import src.domain.POOBSoccerException;
import src.domain.Side;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Teclas de cada jugador.
 */
public class Controls{
    public static final int PAUSE_KEY = KeyEvent.VK_P;
    public static final int EXIT_KEY = KeyEvent.VK_ESCAPE;
    private static final String FILE = "/controles.txt";
    private static final String SEPARATOR = ";";
    private static final String COMMENT = "#";
    private static final String KEY_PREFIX = "VK_";
    private static final String LEFT = "izquierda";
    private static final String RIGHT = "derecha";
    private static final int PARTS = 3;

    private HashMap<Integer, Action> leftKeys;
    private HashMap<Integer, Action> rightKeys;

    /**
     * Crea los controles con lo que dice controles.txt.
     * @throws POOBSoccerException si el archivo no existe o alguna línea está mal escrita
     */
    public Controls() throws POOBSoccerException{
        leftKeys = new HashMap<Integer, Action>();
        rightKeys = new HashMap<Integer, Action>();
        load();
    }

    /**
     * Lee controles.txt línea por línea; se saltan las vacías y las que empiezan con #.
     * @throws POOBSoccerException si el archivo no existe, no se puede leer o tiene una línea mal escrita
     */
    private void load() throws POOBSoccerException{
        InputStream in = Controls.class.getResourceAsStream(FILE);
        if (in == null){
            throw new POOBSoccerException(POOBSoccerException.NO_CONTROLS + FILE);
        }
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int number = 1;
            while (line != null){
                String text = line.trim();
                if (!text.isEmpty() && !text.startsWith(COMMENT)){
                    register(text.split(SEPARATOR), number);
                }
                line = reader.readLine();
                number++;
            }
            reader.close();
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_READ + FILE, e);
        }
    }

    /**
     * Registra una línea de controles.
     * @param parts tecla, lado y acción, separados por ;
     * @param number número de la línea, para el mensaje de error
     * @throws POOBSoccerException si falta algo, algo no existe o la tecla no se puede usar
     */
    private void register(String[] parts, int number) throws POOBSoccerException{
        if (parts.length < PARTS){
            throw lineError(number, POOBSoccerException.MISSING_DATA);
        }
        String keyName = parts[0].trim().toUpperCase();
        int key = keyCode(keyName, number);
        if (key == PAUSE_KEY || key == EXIT_KEY){
            throw lineError(number, POOBSoccerException.RESERVED_KEY + keyName);
        }
        if (leftKeys.containsKey(key) || rightKeys.containsKey(key)){
            throw lineError(number, POOBSoccerException.REPEATED_KEY + keyName);
        }
        Action action = action(parts[2].trim().toUpperCase(), number);
        String side = parts[1].trim().toLowerCase();
        if (side.equals(LEFT)){
            leftKeys.put(key, action);
        }
        else if (side.equals(RIGHT)){
            rightKeys.put(key, action);
        }
        else{
            throw lineError(number, POOBSoccerException.UNKNOWN_SIDE + parts[1].trim());
        }
    }

    /**
     * Busca con reflexión la constante de KeyEvent de esa tecla: A es KeyEvent.VK_A.
     * @param keyName nombre de la tecla sin el VK_, por ejemplo A, LEFT o SPACE
     * @param number número de la línea, para el mensaje de error
     * @return código de la tecla
     * @throws POOBSoccerException si KeyEvent no tiene esa tecla
     */
    private int keyCode(String keyName, int number) throws POOBSoccerException{
        try{
            return KeyEvent.class.getField(KEY_PREFIX + keyName).getInt(null);
        }
        catch (NoSuchFieldException e){
            throw lineError(number, POOBSoccerException.UNKNOWN_KEY + keyName);
        }
        catch (IllegalAccessException e){
            throw lineError(number, POOBSoccerException.UNKNOWN_KEY + keyName);
        }
    }

    /**
     * @param actionName nombre de la acción, por ejemplo KICK
     * @param number número de la línea, para el mensaje de error
     * @return la acción con ese nombre
     * @throws POOBSoccerException si no existe
     */
    private Action action(String actionName, int number) throws POOBSoccerException{
        try{
            return Action.valueOf(actionName);
        }
        catch (IllegalArgumentException e){
            throw lineError(number, POOBSoccerException.UNKNOWN_ACTION + actionName);
        }
    }

    private POOBSoccerException lineError(int number, String detail){
        return new POOBSoccerException(POOBSoccerException.CONTROLS_LINE + number + ": " + detail);
    }

    /**
     * @param s lado del jugador
     * @param keyCode tecla presionada
     * @return acción de esa tecla para ese jugador, o null si no es suya
     */
    public Action getAction(Side s, int keyCode){
        if (s == Side.LEFT){
            return leftKeys.get(keyCode);
        }
        return rightKeys.get(keyCode);
    }
}
