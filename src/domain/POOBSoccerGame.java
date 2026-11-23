package src.domain;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

/**
 * Entrada al dominio desde la interfaz: partidos, jugadores, archivos y datos para mostrar.
 */
public class POOBSoccerGame implements MatchObserver{
    public static final String MATCH_EXTENSION = GameFile.MATCH_EXTENSION;
    public static final String PROGRESS_EXTENSION = GameFile.PROGRESS_EXTENSION;
    private static final long NANOS_PER_SECOND = 1000000000L;
    private static final long NANOS_PER_MILLI = 1000000L;
    private static final String IMAGES_FILE = "imsInfo.txt";
    private static final String PROGRESS_FILE = "poobsoccer_progreso.txt";
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 99;

    private Catalog catalog;
    private ImagesLoader loader;
    private GameFile gameFile;
    private File progressFile;
    private Player leftPlayer;
    private Player rightPlayer;
    private Match match;
    private Field field;
    private ArrayList<MatchObserver> observers;
    private int periodMillis;
    private int updatesPerSecond;

    /**
     * Crea el juego; el progreso de los jugadores se guarda en poobsoccer_progreso.txt.
     * @param period período del bucle en nanosegundos
     */
    public POOBSoccerGame(long period){
        catalog = new Catalog();
        loader = new ImagesLoader(IMAGES_FILE);
        gameFile = new GameFile();
        progressFile = new File(PROGRESS_FILE);
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
     * @return nombre del límite, como "Minutos" o "Goles"
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
     * @param profileId perfil de la máquina que juega a la derecha, o null si son dos jugadores
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
        addObserversToMatch();
    }

    private void addObserversToMatch(){
        match.addObserver(this);
        for (int i = 0; i < observers.size(); i++){
            match.addObserver(observers.get(i));
        }
    }

    /**
     * Guarda el partido en curso para continuarlo después donde quedó.
     * @param file archivo .dat
     * @throws POOBSoccerException si no hay partido en curso o no se puede escribir
     */
    public void saveAs(File file) throws POOBSoccerException{
        if (match == null || match.isOver()){
            throw new POOBSoccerException(POOBSoccerException.NO_MATCH_TO_SAVE);
        }
        match.prepareToSave();
        gameFile.saveMatch(match, file);
    }

    /**
     * Abre un partido guardado y lo deja como partido actual.
     * @param file archivo .dat
     * @throws POOBSoccerException si el archivo no existe o está dañado
     */
    public void open(File file) throws POOBSoccerException{
        Match opened = gameFile.openMatch(file);
        opened.restore(catalog, loader, periodMillis);
        field = opened.getField();
        leftPlayer = opened.getPlayer(Side.LEFT);
        rightPlayer = opened.getPlayer(Side.RIGHT);
        match = opened;
        addObserversToMatch();
    }

    /**
     * Exporta los puntos y las estadísticas de los jugadores a un archivo de texto.
     * @param file archivo .txt
     * @throws POOBSoccerException si no se puede escribir
     */
    public void exportAs(File file) throws POOBSoccerException{
        gameFile.exportProgress(leftPlayer, rightPlayer, catalog.getStatNames(), file);
    }

    /**
     * Importa el progreso de los jugadores desde un archivo de texto.
     * @param file archivo .txt
     * @throws POOBSoccerException si no existe o alguna línea está mal escrita
     */
    public void import_(File file) throws POOBSoccerException{
        gameFile.importProgress(file, leftPlayer, rightPlayer, catalog);
    }

    /**
     * Carga el progreso de la ejecución anterior, si existe.
     * @throws POOBSoccerException si el archivo de progreso está dañado
     */
    public void loadProgress() throws POOBSoccerException{
        if (progressFile.exists()){
            import_(progressFile);
        }
    }

    /**
     * Guarda el progreso de los jugadores para la siguiente ejecución.
     * @throws POOBSoccerException si no se puede escribir
     */
    public void saveProgress() throws POOBSoccerException{
        exportAs(progressFile);
    }

    /**
     * Los goles no cambian el progreso guardado.
     * @param e lado que marcó
     */
    public void goalScored(GoalEvent e){
    }

    /**
     * Guarda el progreso al terminar el partido; si falla, lo deja en el registro de errores.
     * @param e lado ganador
     */
    public void matchEnded(GoalEvent e){
        try{
            saveProgress();
        }
        catch (POOBSoccerException ex){
            ErrorLog.record(ex);
        }
    }

    private void checkLimit(int limit) throws POOBSoccerException{
        if (limit < MIN_LIMIT || limit > MAX_LIMIT){
            throw new POOBSoccerException(POOBSoccerException.BAD_LIMIT + MIN_LIMIT + " y " + MAX_LIMIT + ".");
        }
    }

    private PlayerStrategy findMachine(String profileId) throws POOBSoccerException{
        if (profileId == null){
            return null;
        }
        return catalog.getProfile(profileId);
    }

    /**
     * Ejecuta la acción de un jugador humano; si ese lado es la máquina, no hace nada.
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

    /**
     * Escribe un error en el registro de errores.
     * @param e error que ocurrió
     */
    public void recordError(Exception e){
        ErrorLog.record(e);
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

    /** @param o objeto que recibe los avisos de cada partido */
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

    /** Termina el partido sin ganador. */
    public void endMatch(){
        if (match != null){
            match.abandon();
        }
    }

    /** @return true si el partido actual ya terminó */
    public boolean isMatchOver(){
        return match != null && match.isOver();
    }

    /** @return texto del tiempo del partido según su tipo */
    public String getTimeText(){
        return match.getStatusText();
    }

    /**
     * @param s lado
     * @return nombre del personaje de ese jugador
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
     * @return true si esa estadística ya está en el nivel máximo
     */
    public boolean isMaxLevel(Side s, String statName){
        return getPlayer(s).isMaxLevel(statName);
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

    private Player getPlayer(Side s){
        if (s == Side.LEFT){
            return leftPlayer;
        }
        return rightPlayer;
    }
}
