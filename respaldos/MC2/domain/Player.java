package src.domain;
import java.util.HashMap;

/**
 * Datos y decisiones de un jugador en el partido: identidad, goles, vida,
 * barra de poder, aura, efecto, estadísticas y puntos.
 * Su sprite solo ejecuta lo que el jugador decide.
 */
public class Player
{
    private CountryDefinition country;
    private Side side;
    private PlayerSprite sprite;
    private PlayerStrategy strategy;
    private PlayerState state;
    private HashMap<String, Integer> levels;

    private int goals;
    private double life;
    private double powerCharge;
    private boolean auraActive;
    private int points;

    /**
     * Crea un jugador de un país en un lado de la cancha.
     * @param country país escogido
     * @param side lado de la cancha
     */
    public Player(CountryDefinition country, Side side){
        this.country = country;
        this.side = side;
        sprite = null;   // pendiente MC2
        strategy = null;
        state = null;    // pendiente MC4
        levels = new HashMap<String, Integer>();
        goals = 0;
        life = 0;        // pendiente MC6
        powerCharge = 0;
        auraActive = false;
        points = 0;
    }

    /**
     * Ejecuta una acción pasando primero por el efecto activo.
     * @param a acción pedida
     */
    public void perform(Action a){
        // pendiente MC2
    }

    /**
     * Si el jugador es máquina, le pide la acción a su perfil.
     * @param m partido actual
     */
    public void think(Match m){
        // pendiente MC2
    }

    /**
     * Avanza un ciclo: barra de poder y efecto activo.
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public void update(int updatesPerSecond){
        // pendiente MC4
    }

    /**
     * Reemplaza el efecto actual por uno nuevo.
     * @param newState efecto nuevo
     */
    public void applyEffect(PlayerState newState){
        // pendiente MC4
    }

    /**
     * Resta vida al jugador.
     * @param damage daño recibido
     */
    public void takeDamage(double damage){
        // pendiente MC4
    }

    /**
     * El jugador recibió un gol.
     */
    public void concede(){
        // pendiente MC4
    }

    /** @return multiplicador de daño según el nivel de Kick */
    public double kickMultiplier(){
        return 0; // pendiente MC6
    }

    /**
     * @param statName nombre de la estadística
     * @return precio de la siguiente mejora
     */
    public int upgradePrice(String statName){
        return 0; // pendiente MC6
    }

    /**
     * Compra una mejora de una estadística.
     * @param statName nombre de la estadística
     */
    public void upgrade(String statName){
        // pendiente MC6
    }

    /** @param ps perfil de máquina, o null si es humano */
    public void setStrategy(PlayerStrategy ps){
        strategy = ps;
    }

    /** @return país del jugador */
    public CountryDefinition getCountry(){
        return country;
    }

    /** @return lado de la cancha */
    public Side getSide(){
        return side;
    }

    /** @return sprite del jugador */
    public PlayerSprite getSprite(){
        return sprite;
    }

    /** @return goles marcados */
    public int getGoals(){
        return goals;
    }

    /** @return vida actual */
    public double getLife(){
        return life;
    }

    /** @return vida máxima */
    public double getMaxLife(){
        return 0; // pendiente MC6
    }

    /** @return carga de la barra de poder, de 0 a 1 */
    public double getPowerCharge(){
        return powerCharge;
    }

    /** @return true si el aura está activa */
    public boolean isAuraActive(){
        return auraActive;
    }

    /** @return puntos acumulados */
    public int getPoints(){
        return points;
    }

    /** @return nombre del efecto activo */
    public String getEffectName(){
        return null; // pendiente MC4
    }
}
