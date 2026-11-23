package src.domain;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Fachada del dominio. Es lo único que la presentación conoce:
 * recibe los eventos de la interfaz y entrega los datos del HUD.
 */
public class POOBSoccerGame
{
    private static final long NANOS_PER_SECOND = 1000000000L;

    private Catalog catalog;
    private Match match;
    private int updatesPerSecond;

    /**
     * Crea el juego.
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccerGame(long period){
        catalog = new Catalog();
        match = null;   // pendiente MC2
        updatesPerSecond = (int) (NANOS_PER_SECOND / period);
    }

    /** @return identificadores de los países */
    public ArrayList<String> getCountryIds(){
        return catalog.getCountryIds();
    }

    /** @return identificadores de los perfiles de máquina */
    public ArrayList<String> getProfileIds(){
        return catalog.getProfileIds();
    }

    /** @return identificadores de los tipos de partido */
    public ArrayList<String> getRuleIds(){
        return catalog.getRuleIds();
    }

    /** @return identificadores de las resoluciones */
    public ArrayList<String> getFieldIds(){
        return catalog.getFieldIds();
    }

    /** @return nombres de las estadísticas */
    public ArrayList<String> getStatNames(){
        return catalog.getStatNames();
    }

    /**
     * Inicia un partido con lo escogido en los menús.
     * @param leftCountry país del jugador izquierdo
     * @param rightCountry país del jugador derecho
     * @param profileId perfil de la máquina, o null si es PvP
     * @param rulesId tipo de partido
     * @param limit minutos o goles, según el tipo de partido
     * @param fieldId resolución
     * @param loader cargador de imágenes
     */
    public void startMatch(String leftCountry, String rightCountry, String profileId,
                           String rulesId, int limit, String fieldId, ImagesLoader loader){
        // pendiente MC2
    }

    /**
     * Un jugador humano pide una acción.
     * @param s lado del jugador
     * @param a acción
     */
    public void perform(Side s, Action a){
        // pendiente MC2
    }

    /** Avanza el partido un ciclo. */
    public void update(){
        // pendiente MC2
    }

    /** Pausa el partido. */
    public void pause(){
        // pendiente MC3
    }

    /** Reanuda el partido. */
    public void resume(){
        // pendiente MC3
    }

    /** @return true si el partido está en pausa */
    public boolean isPaused(){
        return false; // pendiente MC3
    }

    /** @param o observador de goles y fin del partido */
    public void addObserver(MatchObserver o){
        // pendiente MC2
    }

    /** @param g donde se dibujan los sprites */
    public void draw(Graphics g){
        // pendiente MC2
    }

    /** @return segundos que faltan */
    public int getTimeLeft(){
        return 0; // pendiente MC3
    }

    /**
     * @param s lado
     * @return goles de ese jugador
     */
    public int getGoals(Side s){
        return 0; // pendiente MC2
    }

    /**
     * @param s lado
     * @return vida de ese jugador
     */
    public double getLife(Side s){
        return 0; // pendiente MC4
    }

    /**
     * @param s lado
     * @return vida máxima de ese jugador
     */
    public double getMaxLife(Side s){
        return 0; // pendiente MC6
    }

    /**
     * @param s lado
     * @return carga de la barra, de 0 a 1
     */
    public double getPowerCharge(Side s){
        return 0; // pendiente MC4
    }

    /**
     * @param s lado
     * @return true si el aura está activa
     */
    public boolean isAuraActive(Side s){
        return false; // pendiente MC4
    }

    /**
     * @param s lado
     * @return nombre del efecto activo
     */
    public String getEffectName(Side s){
        return null; // pendiente MC4
    }

    /**
     * @param s lado
     * @return puntos de ese jugador
     */
    public int getPoints(Side s){
        return 0; // pendiente MC6
    }
}
