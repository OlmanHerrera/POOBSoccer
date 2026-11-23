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
    private static final double BASE_LIFE = 100;
    private static final double FULL_CHARGE = 1;
    private static final double GOAL_BONUS = 0.25;
    private static final double FILL_SECONDS = 10;
    private static final double KICK_DAMAGE = 10;
    private static final int TOWARDS_LEFT = -1;
    private static final int TOWARDS_RIGHT = 1;

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
    private double kickMultiplier;
    private double lifeMultiplier;

    /**
     * Crea el jugador de un lado de la cancha.
     * @param side lado de la cancha
     */
    public Player(Side side){
        this.side = side;
        country = null;
        sprite = null;
        strategy = null;
        state = new NormalState();
        levels = new HashMap<String, Integer>();
        goals = 0;
        powerCharge = 0;
        auraActive = false;
        points = 0;
        kickMultiplier = 1;  // pendiente MC6: lo pone la estadística Kick
        lifeMultiplier = 1;  // pendiente MC6: lo pone la estadística Survival
        life = getMaxLife();
    }

    /**
     * Prepara al jugador para un partido nuevo: personaje, perfil y cuerpo en la cancha.
     * Empieza sin goles, con la barra vacía, sin aura, sin efecto y con la vida al máximo.
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
        powerCharge = 0;
        auraActive = false;
        state = new NormalState();
        life = getMaxLife();
    }

    /** @return nombre de la animación de correr según el lado */
    private String imageName(){
        if (side == Side.LEFT){
            return LEFT_IMAGE;
        }
        return RIGHT_IMAGE;
    }

    /**
     * Ejecuta una acción si el efecto activo la permite.
     * @param a acción pedida
     */
    public void perform(Action a){
        Action allowed = state.filter(a);
        if (allowed == null){
            return;
        }
        if (allowed == Action.POWER){
            activatePower();
            return;
        }
        sprite.perform(allowed);
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
     * Avanza un ciclo: mueve su cuerpo, carga la barra y cuenta el tiempo del efecto.
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public void update(int updatesPerSecond){
        sprite.updateSprite();
        addCharge(FULL_CHARGE / (FILL_SECONDS * updatesPerSecond));
        state.update(this, updatesPerSecond);
    }

    /**
     * Suma carga a la barra de poder sin pasarse de llena.
     * @param amount carga que se suma
     */
    private void addCharge(double amount){
        powerCharge = powerCharge + amount;
        if (powerCharge > FULL_CHARGE){
            powerCharge = FULL_CHARGE;
        }
    }

    /**
     * Con la barra llena y sin aura, activa el aura: la barra se vacía
     * y empieza a recargarse de inmediato.
     */
    private void activatePower(){
        if (powerCharge < FULL_CHARGE || auraActive){
            return;
        }
        auraActive = true;
        powerCharge = 0;
    }

    /**
     * Ejecuta el tiro de poder de su personaje sobre el balón y el aura desaparece.
     * @param ball balón que tocó con el aura activa
     */
    public void shootPower(BallSprite ball){
        AbilityStrategy power = country.getAbility();
        ball.charge(power, side);
        power.launch(ball);
        auraActive = false;
    }

    /**
     * Si su patada alcanza al rival, le hace daño una sola vez por patada.
     * @param rival el otro jugador
     */
    public void tryKick(Player rival){
        if (!sprite.isKickActive()){
            return;
        }
        if (!sprite.getBootBox().intersects(rival.getBounds())){
            return;
        }
        rival.takeDamage(KICK_DAMAGE * kickMultiplier);
        sprite.markKickHit();
    }

    /**
     * Reemplaza el efecto actual por uno nuevo (los efectos no se acumulan).
     * @param newState efecto nuevo
     */
    public void applyEffect(PlayerState newState){
        state.exit(this);
        state = newState;
        state.enter(this);
    }

    /**
     * Resta vida. La vida no baja de cero; al llegar a cero queda aturdido.
     * Si ya está en cero (aturdido), no recibe más daño.
     * @param damage daño recibido
     */
    public void takeDamage(double damage){
        if (life == 0){
            return;
        }
        life = life - damage;
        if (life <= 0){
            life = 0;
            applyEffect(new StunnedState());
        }
    }

    /** Vuelve a la vida máxima (al terminar el aturdimiento). */
    public void restoreLife(){
        life = getMaxLife();
    }

    /** Recibió un gol: su barra sube de inmediato un cuarto. */
    public void concede(){
        addCharge(GOAL_BONUS);
    }

    /**
     * Lanza su cuerpo con una velocidad (lo usan los efectos).
     * @param vx velocidad horizontal
     * @param vy velocidad vertical (negativa hacia arriba)
     */
    public void launch(float vx, float vy){
        sprite.applyForce(new PVector(vx, vy));
    }

    /** Detiene su cuerpo (lo usan los efectos). */
    public void stop(){
        sprite.stop();
    }

    /** @return -1 si su arco está a la izquierda, 1 si está a la derecha */
    public int getOwnGoalDirection(){
        if (side == Side.LEFT){
            return TOWARDS_LEFT;
        }
        return TOWARDS_RIGHT;
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

    /** @return vida máxima: 100 por el multiplicador de Survival */
    public double getMaxLife(){
        return BASE_LIFE * lifeMultiplier;
    }

    /** @return multiplicador de daño de Kick */
    public double getKickMultiplier(){
        return kickMultiplier;
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
        return state.getName();
    }
}
