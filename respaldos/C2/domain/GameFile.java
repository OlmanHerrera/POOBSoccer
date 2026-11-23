package src.domain;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Detalles de los archivos del juego, en un solo lugar:
 * el partido se guarda y se abre como objeto (.dat) y el progreso de los
 * jugadores se exporta y se importa como texto (.txt), una línea por dato:
 * "Jugador1, Puntos, 325" o "Jugador1, Kick, 3".
 */
public class GameFile
{
    public static final String MATCH_EXTENSION = ".dat";
    public static final String PROGRESS_EXTENSION = ".txt";
    private static final String HEADER = "# POOBSoccer - progreso: jugador, dato, valor";
    private static final String COMMENT = "#";
    private static final String SEPARATOR = ",";
    private static final String OUTPUT_SEPARATOR = ", ";
    private static final String LEFT_LABEL = "Jugador1";
    private static final String RIGHT_LABEL = "Jugador2";
    private static final String POINTS_KEY = "Puntos";
    private static final int FIELDS = 3;
    private static final int PLAYER_PART = 0;
    private static final int KEY_PART = 1;
    private static final int VALUE_PART = 2;

    /**
     * Guarda el partido completo como objeto.
     * @param match partido, ya preparado para guardarse
     * @param file archivo .dat
     * @throws POOBSoccerException si la extensión no es .dat o no se puede escribir
     */
    public void saveMatch(Match match, File file) throws POOBSoccerException{
        checkExtension(file, MATCH_EXTENSION);
        ObjectOutputStream out = null;
        try{
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(match);
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_WRITE + file.getName(), e);
        }
        finally{
            close(out);
        }
    }

    /**
     * Abre un partido guardado. Falta reconstruirlo con restore.
     * @param file archivo .dat
     * @return el partido leído
     * @throws POOBSoccerException si la extensión no es .dat, no existe o no es un partido válido
     */
    public Match openMatch(File file) throws POOBSoccerException{
        checkExtension(file, MATCH_EXTENSION);
        checkExists(file);
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(new FileInputStream(file));
            return (Match) in.readObject();
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.DAMAGED_MATCH + file.getName(), e);
        }
        catch (ClassNotFoundException e){
            throw new POOBSoccerException(POOBSoccerException.DAMAGED_MATCH + file.getName(), e);
        }
        catch (ClassCastException e){
            throw new POOBSoccerException(POOBSoccerException.DAMAGED_MATCH + file.getName(), e);
        }
        finally{
            close(in);
        }
    }

    /**
     * Escribe el progreso de los dos jugadores: puntos y nivel de cada estadística.
     * @param left jugador 1
     * @param right jugador 2
     * @param statNames estadísticas registradas
     * @param file archivo .txt
     * @throws POOBSoccerException si la extensión no es .txt o no se puede escribir
     */
    public void exportProgress(Player left, Player right, ArrayList<String> statNames, File file)
                               throws POOBSoccerException{
        checkExtension(file, PROGRESS_EXTENSION);
        PrintWriter out = null;
        try{
            out = new PrintWriter(new FileWriter(file));
            out.println(HEADER);
            writePlayer(out, LEFT_LABEL, left, statNames);
            writePlayer(out, RIGHT_LABEL, right, statNames);
            if (out.checkError()){
                throw new POOBSoccerException(POOBSoccerException.CANNOT_WRITE + file.getName());
            }
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_WRITE + file.getName(), e);
        }
        finally{
            close(out);
        }
    }

    /**
     * Escribe las líneas de un jugador.
     * @param out archivo abierto
     * @param label Jugador1 o Jugador2
     * @param p jugador
     * @param statNames estadísticas registradas
     */
    private void writePlayer(PrintWriter out, String label, Player p, ArrayList<String> statNames){
        out.println(label + OUTPUT_SEPARATOR + POINTS_KEY + OUTPUT_SEPARATOR + p.getPoints());
        for (int i = 0; i < statNames.size(); i++){
            String stat = statNames.get(i);
            out.println(label + OUTPUT_SEPARATOR + stat + OUTPUT_SEPARATOR + p.getLevel(stat));
        }
    }

    /**
     * Lee un progreso y, solo si todo el archivo es válido, se lo pone a los jugadores.
     * Lo que el archivo no diga queda en su valor inicial (0 puntos, nivel 1).
     * @param file archivo .txt
     * @param left jugador 1
     * @param right jugador 2
     * @param catalog catálogo, para revisar que cada estadística exista
     * @throws POOBSoccerException si el archivo no existe, no se puede leer o tiene una línea mal escrita
     */
    public void importProgress(File file, Player left, Player right, Catalog catalog) throws POOBSoccerException{
        checkExtension(file, PROGRESS_EXTENSION);
        checkExists(file);
        Player newLeft = new Player(Side.LEFT);
        Player newRight = new Player(Side.RIGHT);
        BufferedReader in = null;
        try{
            in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            int number = 1;
            while (line != null){
                readLine(line, number, newLeft, newRight, catalog);
                line = in.readLine();
                number++;
            }
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_READ + file.getName(), e);
        }
        finally{
            close(in);
        }
        left.copyProgress(newLeft);
        right.copyProgress(newRight);
    }

    /**
     * Interpreta una línea del progreso. Las líneas vacías y las que empiezan con # se saltan.
     * @param line texto de la línea
     * @param number número de la línea, para el mensaje de error
     * @param newLeft progreso leído del jugador 1
     * @param newRight progreso leído del jugador 2
     * @param catalog catálogo, para revisar que la estadística exista
     * @throws POOBSoccerException si la línea está mal escrita
     */
    private void readLine(String line, int number, Player newLeft, Player newRight, Catalog catalog)
                          throws POOBSoccerException{
        String text = line.trim();
        if (text.isEmpty() || text.startsWith(COMMENT)){
            return;
        }
        String[] parts = text.split(SEPARATOR);
        if (parts.length != FIELDS){
            throw lineError(number, POOBSoccerException.BAD_FORMAT);
        }
        Player p = playerFor(parts[PLAYER_PART].trim(), number, newLeft, newRight);
        String key = parts[KEY_PART].trim();
        int value = numberIn(parts[VALUE_PART].trim(), number);
        try{
            if (key.equals(POINTS_KEY)){
                p.setPoints(value);
            }
            else{
                catalog.getStat(key);
                p.setLevel(key, value);
            }
        }
        catch (POOBSoccerException e){
            throw lineError(number, e.getMessage());
        }
    }

    /**
     * @param label primer dato de la línea
     * @param number número de la línea
     * @param newLeft progreso leído del jugador 1
     * @param newRight progreso leído del jugador 2
     * @return el jugador de esa etiqueta
     * @throws POOBSoccerException si no es Jugador1 ni Jugador2
     */
    private Player playerFor(String label, int number, Player newLeft, Player newRight) throws POOBSoccerException{
        if (label.equals(LEFT_LABEL)){
            return newLeft;
        }
        if (label.equals(RIGHT_LABEL)){
            return newRight;
        }
        throw lineError(number, POOBSoccerException.UNKNOWN_PLAYER + label);
    }

    /**
     * @param text tercer dato de la línea
     * @param number número de la línea
     * @return el número escrito
     * @throws POOBSoccerException si no es un número entero
     */
    private int numberIn(String text, int number) throws POOBSoccerException{
        try{
            return Integer.parseInt(text);
        }
        catch (NumberFormatException e){
            throw lineError(number, POOBSoccerException.NOT_A_NUMBER + text);
        }
    }

    /**
     * @param number número de la línea
     * @param detail qué está mal
     * @return la excepción con el número de línea
     */
    private POOBSoccerException lineError(int number, String detail){
        return new POOBSoccerException(POOBSoccerException.BAD_LINE + number + ": " + detail);
    }

    /**
     * @param file archivo escogido
     * @param extension extensión que debe tener
     * @throws POOBSoccerException si tiene otra extensión
     */
    private void checkExtension(File file, String extension) throws POOBSoccerException{
        if (!file.getName().endsWith(extension)){
            throw new POOBSoccerException(POOBSoccerException.WRONG_EXTENSION + extension + ": " + file.getName());
        }
    }

    /**
     * @param file archivo escogido
     * @throws POOBSoccerException si no existe
     */
    private void checkExists(File file) throws POOBSoccerException{
        if (!file.exists()){
            throw new POOBSoccerException(POOBSoccerException.FILE_NOT_FOUND + file.getName());
        }
    }

    /**
     * Cierra un archivo si quedó abierto; si no se puede cerrar, lo deja en el registro de errores.
     * @param file archivo abierto, o null
     */
    private void close(Closeable file){
        if (file == null){
            return;
        }
        try{
            file.close();
        }
        catch (IOException e){
            ErrorLog.record(e);
        }
    }
}
