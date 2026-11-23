package src.domain;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Fachada del dominio. Es lo único que la presentación conoce:
 * recibe los eventos de la interfaz y entrega los datos para mostrar.
 * Guarda a los dos jugadores, que juegan todos los partidos.
 */
public class POOBSoccerGame
{
    private static final long NANOS_PER_SECOND = 1000000000L;
    private static final long NANOS_PER_MILLI = 1000000L;
    private static final String IMS_INFO = "imsInfo.txt";
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 99;

    private Catalog catalog;
    private ImagesLoader loader;
    private Player leftPlayer;
    private Player rightPlayer;
    private Match match;
    private Field field;
    private ArrayList<MatchObserver> observers;
    private int periodMillis;
    private int updatesPerSecond;

    /**
     * Crea el juego.
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccerGame(long period){
        catalog = new Catalog();
        loader = new ImagesLoader(IMS_INFO);
        leftPlayer = new Player(Side.LEFT);
        rightPlayer = new Player(Side.RIGHT);
        match = null;
        field = null;
        observers = new ArrayList<MatchObserver>();
        periodMillis = (int) (period / NANOS_PER_MILLI);
        updatesPerSecond = (int) (NANOS_PER_SECOND / period);
    }

    /** @return nombres de los personajes */
    public ArrayList<String> getCountryIds(){
        return catalog.getCountryIds();
    }

    /** @return nombres de los perfiles de máquina */
    public ArrayList<String> getProfileIds(){
        return catalog.getProfileIds();
    }

    /** @return nombres de los tipos de partido */
    public ArrayList<String> getRuleIds(){
        return catalog.getRuleIds();
    }

    /** @return nombres de las resoluciones */
    public ArrayList<String> getFieldIds(){
        return catalog.getFieldIds();
    }

    /** @return nombres de las estadísticas */
    public ArrayList<String> getStatNames(){
        return catalog.getStatNames();
    }

    /**
     * @param rulesId tipo de partido
     * @return qué cuenta su límite, por ejemplo "minutos" o "goles"
     * @throws POOBSoccerException si el tipo de partido no existe
     */
    public String getRuleLimitName(String rulesId) throws POOBSoccerException{
        return catalog.getRules(rulesId).getLimitName();
    }

    /**
     * @param rulesId tipo de partido
     * @return límite propuesto para ese tipo de partido
     * @throws POOBSoccerException si el tipo de partido no existe
     */
    public int getRuleDefaultLimit(String rulesId) throws POOBSoccerException{
        return catalog.getRules(rulesId).getDefaultLimit();
    }

    /**
     * Inicia un partido con lo escogido en los menús.
     * @param leftCountry personaje del jugador izquierdo
     * @param rightCountry personaje del jugador derecho
     * @param profileId perfil de la máquina que juega a la derecha, o null si es jugador contra jugador
     * @param rulesId tipo de partido
     * @param limit minutos o goles, según el tipo de partido
     * @param fieldId resolución de la cancha
     * @throws POOBSoccerException si algo de lo escogido no existe o el límite no es válido
     */
    public void startMatch(String leftCountry, String rightCountry, String profileId,
                           String rulesId, int limit, String fieldId) throws POOBSoccerException{
        CountryDefinition left = catalog.getCountry(leftCountry);
        CountryDefinition right = catalog.getCountry(rightCountry);
        PlayerStrategy machine = findMachine(profileId);
        MatchRules rules = catalog.getRules(rulesId);
        checkLimit(limit);
        field = catalog.getField(fieldId);
        leftPlayer.prepare(left, null, field, loader, periodMillis, catalog.getStats());
        rightPlayer.prepare(right, machine, field, loader, periodMillis, catalog.getStats());
        match = new Match(field, rules, limit, catalog.getElements(), leftPlayer, rightPlayer,
                          loader, updatesPerSecond);
        for (int i = 0; i < observers.size(); i++){
            match.addObserver(observers.get(i));
        }
    }

    /**
     * @param limit minutos o goles escogidos
     * @throws POOBSoccerException si está fuera del rango permitido
     */
    private void checkLimit(int limit) throws POOBSoccerException{
        if (limit < MIN_LIMIT || limit > MAX_LIMIT){
            throw new POOBSoccerException(POOBSoccerException.BAD_LIMIT + MIN_LIMIT + " y " + MAX_LIMIT
                                          + " (escribiste " + limit + ").");
        }
    }

    /**
     * @param profileId perfil escogido, o null si no hay máquina
     * @return el perfil, o null si es jugador contra jugador
     */
    private PlayerStrategy findMachine(String profileId) throws POOBSoccerException{
        if (profileId == null){
            return null;
        }
        return catalog.getProfile(profileId);
    }

    /**
     * Un jugador humano pide una acción. Si ese lado lo juega la máquina, se ignora.
     * @param s lado del jugador
     * @param a acción
     */
    public void perform(Side s, Action a){
        if (match == null){
            return;
        }
        Player p = getPlayer(s);
        if (p.isMachine()){
            return;
        }
        p.perform(a);
    }

    /** Avanza el partido un ciclo. */
    public void update(){
        if (match != null){
            match.update();
        }
    }

    /**
     * Dibuja el partido.
     * @param g donde se dibuja
     */
    public void draw(Graphics g){
        if (match != null){
            match.draw(g);
        }
    }

    /** @param o observador de goles y del final de cada partido */
    public void addObserver(MatchObserver o){
        observers.add(o);
        if (match != null){
            match.addObserver(o);
        }
    }

    /** @return true si ya se inició algún partido */
    public boolean hasMatch(){
        return match != null;
    }

    /** @return ancho de la cancha del partido actual */
    public int getFieldWidth(){
        return field.getWidth();
    }

    /** @return alto de la cancha del partido actual */
    public int getFieldHeight(){
        return field.getHeight();
    }

    /** Pausa el partido. */
    public void pause(){
        if (match != null){
            match.pause();
        }
    }

    /** Reanuda el partido. */
    public void resume(){
        if (match != null){
            match.resume();
        }
    }

    /** @return true si el partido está en pausa */
    public boolean isPaused(){
        return match != null && match.isPaused();
    }

    /** Los jugadores terminan el partido en cualquier momento, sin ganador. */
    public void endMatch(){
        if (match != null){
            match.abandon();
        }
    }

    /** @return true si el partido actual ya terminó */
    public boolean isMatchOver(){
        return match != null && match.isOver();
    }

    /** @return lado ganador del partido, o null si no hubo ganador */
    public Side getWinner(){
        return match.getWinner();
    }

    /** @return texto del tiempo del partido según su tipo */
    public String getTimeText(){
        return match.getStatusText();
    }

    /**
     * @param s lado
     * @return nombre del personaje de ese jugador en el partido actual
     */
    public String getCountryName(Side s){
        return getPlayer(s).getCountry().getId();
    }

    /**
     * @param s lado
     * @return true si ese lado lo juega la máquina
     */
    public boolean isMachine(Side s){
        return getPlayer(s).isMachine();
    }

    /**
     * @param s lado
     * @return goles de ese jugador
     */
    public int getGoals(Side s){
        return getPlayer(s).getGoals();
    }

    /**
     * @param s lado
     * @return vida de ese jugador
     */
    public double getLife(Side s){
        return getPlayer(s).getLife();
    }

    /**
     * @param s lado
     * @return vida máxima de ese jugador
     */
    public double getMaxLife(Side s){
        return getPlayer(s).getMaxLife();
    }

    /**
     * @param s lado
     * @return carga de la barra, de 0 a 1
     */
    public double getPowerCharge(Side s){
        return getPlayer(s).getPowerCharge();
    }

    /**
     * @param s lado
     * @return true si el aura está activa
     */
    public boolean isAuraActive(Side s){
        return getPlayer(s).isAuraActive();
    }

    /**
     * @param s lado
     * @return nombre del efecto activo
     */
    public String getEffectName(Side s){
        return getPlayer(s).getEffectName();
    }

    /**
     * @param s lado
     * @return puntos acumulados de ese jugador
     */
    public int getPoints(Side s){
        return getPlayer(s).getPoints();
    }

    /**
     * @param s lado
     * @return puntos que ganó ese jugador en el último partido
     */
    public int getPointsGained(Side s){
        return getPlayer(s).getPointsGained();
    }

    /**
     * @param s lado
     * @param statName nombre de la estadística
     * @return nivel de esa estadística del jugador
     */
    public int getStatLevel(Side s, String statName){
        return getPlayer(s).getLevel(statName);
    }

    /**
     * @param s lado
     * @param statName nombre de la estadística
     * @return precio del siguiente punto de mejora
     */
    public int getUpgradePrice(Side s, String statName){
        return getPlayer(s).upgradePrice(statName);
    }

    /**
     * Un jugador compra un punto de mejora; se nota desde el siguiente partido.
     * @param s lado
     * @param statName nombre de la estadística
     * @throws POOBSoccerException si la estadística no existe, ya está al máximo o faltan puntos
     */
    public void upgrade(Side s, String statName) throws POOBSoccerException{
        catalog.getStat(statName);
        getPlayer(s).upgrade(statName);
    }

    /**
     * @param s lado
     * @return jugador de ese lado
     */
    private Player getPlayer(Side s){
        if (s == Side.LEFT){
            return leftPlayer;
        }
        return rightPlayer;
    }
}
