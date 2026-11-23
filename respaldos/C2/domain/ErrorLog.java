package src.domain;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Registro de errores para los programadores: cada error se agrega al final
 * de un archivo de texto con la fecha, el tipo, el mensaje y dónde ocurrió.
 */
public class ErrorLog
{
    private static final String FILE_NAME = "poobsoccer_errores.log";

    /**
     * Escribe un error al final del archivo de registro.
     * @param e error que ocurrió
     */
    public static void record(Exception e){
        try{
            PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true));
            out.println(new Date() + " - " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(out);
            out.close();
        }
        catch (IOException io){
            System.err.println("No se pudo escribir el registro de errores: " + io.getMessage());
        }
    }
}
