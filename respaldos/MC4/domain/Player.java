package src.domain;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;

/**
 * Jugador de un lado de la cancha: identidad, goles, vida, barra de poder,
 * aura, efecto, estadísticas y puntos. El mismo jugador juega todos los partidos;
 * en cada partido escoge personaje y recibe un cuerpo nuevo (su sprite),
 * que solo ejecuta lo que el jugador decide.
 */
public class Player
{
    private static final String LEFT_IMAGE = "player1_run";
    private static final String RIGHT_IMAGE = "player2_run";

    private Side side;
    private CountryDefinition country;
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
     * Crea el jugador de un lado de la cancha.
     * @param side lado de la cancha
     */
    public Player(Side side){
        this.side = side;
        country = null;
        sprite = null;
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
     * Prepara al jugador para un partido nuevo: personaje, perfil y cuerpo en la cancha.
     * @param country personaje escogido
     * @param strategy perfil de máquina, o null si lo juega una persona
     * @param field cancha del partido
     * @param loader cargador de imágenes
     * @param period período del bucle en milisegundos, para las animaciones
     */
    public void prepare(CountryDefinition country, PlayerStrategy strategy, Field field,
                        ImagesLoader loader, int period){
        this.country = country;
        this.strategy = strategy;
        sprite = new PlayerSprite(field, loader, period, imageName(), side);
        goals = 0;
    }

    /** @return nombre de la animación de correr según el lado */
    private String imageName(){
        if (side == Side.LEFT){
            return LEFT_IMAGE;
        }
        return RIGHT_IMAGE;
    }

    /**
     * Ejecuta una acción con su cuerpo.
     * @param a acción pedida
     */
    public void perform(Action a){
        if (a == Action.POWER){
            return; // activar el poder: pendiente MC4
        }
        sprite.perform(a);
    }

    /**
     * Si el jugador es máquina, le pide la acción a su perfil.
     * @param m partido actual
     */
    public void think(Match m){
        if (strategy == null){
            return;
        }
        perform(strategy.decide(this, m));
    }

    /**
     * Avanza un ciclo.
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public void update(int updatesPerSecond){
        sprite.updateSprite(); // barra y efecto: pendiente MC4
    }

    /** Suma un gol. */
    public void scoreGoal(){
        goals++;
    }

    /** Vuelve a su posición inicial (después de un gol). */
    public void returnToStart(){
        sprite.returnToStart();
    }

    /**
     * Dibuja al jugador.
     * @param g donde se dibuja
     */
    public void draw(Graphics g){
        sprite.drawSprite(g);
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

    /** @return true si lo juega la máquina */
    public boolean isMachine(){
        return strategy != null;
    }

    /** @return rectángulo de su cuerpo en la cancha */
    public Rectangle getBounds(){
        return sprite.getMyRectangle();
    }

    /** @return personaje del jugador */
    public CountryDefinition getCountry(){
        return country;
    }

    /** @return lado de la cancha */
    public Side getSide(){
        return side;
    }

    /** @return cuerpo del jugador en la cancha */
    public PlayerSprite getSprite(){
        return sprite;
    }

    /** @return goles marcados en el partido */
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
