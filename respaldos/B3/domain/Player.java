package src.domain;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Jugador de un lado de la cancha: identidad, goles, vida, barra de poder,
 * aura, efecto, estadísticas y puntos. El mismo jugador juega todos los partidos;
 * en cada partido escoge personaje y recibe un cuerpo nuevo (su sprite),
 * que solo ejecuta lo que el jugador decide.
 * Al guardarse, del personaje y del perfil se guarda su nombre, y del cuerpo
 * su posición y velocidad; al abrirse se reconstruyen desde el catálogo.
 */
public class Player implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String LEFT_IMAGE = "player1_run";
    private static final String RIGHT_IMAGE = "player2_run";
    private static final double BASE_LIFE = 100;
    private static final double FULL_CHARGE = 1;
    private static final double GOAL_BONUS = 0.25;
    private static final double FILL_SECONDS = 10;
    private static final double KICK_DAMAGE = 10;
    private static final int TOWARDS_LEFT = -1;
    private static final int TOWARDS_RIGHT = 1;
    private static final int INITIAL_LEVEL = 1;
    private static final int MAX_LEVEL = 10;
    private static final int BASE_PRICE = 500;
    private static final int PRICE_GROWTH = 2;
    private static final int AURA_MARGIN = 8;
    private static final int AURA_ALPHA = 90;
    private static final int AURA_LINE = 2;

    private Side side;
    private transient CountryDefinition country;
    private transient PlayerSprite sprite;
    private transient PlayerStrategy strategy;
    private String countryId;
    private String profileId;
    private Motion motion;
    private PlayerState state;
    private HashMap<String, Integer> levels;

    private int goals;
    private double life;
    private double powerCharge;
    private boolean auraActive;
    private int points;
    private int pointsGained;
    private double kickMultiplier;
    private double lifeMultiplier;
    private double speedMultiplier;
    private double jumpMultiplier;
    private double effectSpeedFactor;

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
        pointsGained = 0;
        kickMultiplier = 1;
        lifeMultiplier = 1;
        speedMultiplier = 1;
        jumpMultiplier = 1;
        effectSpeedFactor = 1;
        life = getMaxLife();
    }

    /**
     * Prepara al jugador para un partido nuevo: personaje, perfil, cuerpo en la cancha
     * y sus estadísticas. Empieza sin goles, con la barra vacía, sin aura, sin efecto
     * y con la vida al máximo.
     * @param country personaje escogido
     * @param strategy perfil de máquina, o null si lo juega una persona
     * @param field cancha del partido
     * @param loader cargador de imágenes
     * @param period período del bucle en milisegundos, para las animaciones
     * @param stats estadísticas del juego
     */
    public void prepare(CountryDefinition country, PlayerStrategy strategy, Field field,
                        ImagesLoader loader, int period, ArrayList<Stat> stats){
        this.country = country;
        this.strategy = strategy;
        countryId = country.getId();
        profileId = null;
        if (strategy != null){
            profileId = strategy.getId();
        }
        sprite = new PlayerSprite(field, loader, period, imageName(), side);
        goals = 0;
        pointsGained = 0;
        powerCharge = 0;
        auraActive = false;
        state = new NormalState();
        effectSpeedFactor = 1;
        applyStats(stats);
        life = getMaxLife();
    }

    /**
     * Cada estadística escala su atributo según el nivel del jugador.
     * @param stats estadísticas del juego
     */
    private void applyStats(ArrayList<Stat> stats){
        for (int i = 0; i < stats.size(); i++){
            Stat s = stats.get(i);
            s.apply(this, getLevel(s.getName()));
        }
        refreshBody();
    }

    /** Le pasa a su cuerpo la velocidad y el salto que le dan sus estadísticas y su efecto. */
    private void refreshBody(){
        refreshSpeed();
        // con gravedad constante la altura crece con el cuadrado del impulso:
        // para que la altura suba 10 % por punto, el impulso usa la raíz del multiplicador
        sprite.setJumpFactor((float) Math.sqrt(jumpMultiplier));
    }

    /** Antes de guardar, anota la posición y la velocidad de su cuerpo. */
    public void prepareToSave(){
        motion = sprite.getMotion();
    }

    /**
     * Después de abrir un partido guardado, recupera su personaje y su perfil por
     * nombre desde el catálogo y reconstruye su cuerpo donde estaba.
     * @param catalog catálogo del juego
     * @param field cancha del partido
     * @param loader cargador de imágenes
     * @param period período del bucle en milisegundos, para las animaciones
     * @throws POOBSoccerException si el personaje o el perfil ya no están registrados
     */
    public void restore(Catalog catalog, Field field, ImagesLoader loader, int period) throws POOBSoccerException{
        country = catalog.getCountry(countryId);
        strategy = null;
        if (profileId != null){
            strategy = catalog.getProfile(profileId);
        }
        sprite = new PlayerSprite(field, loader, period, imageName(), side);
        sprite.setMotion(motion);
        refreshBody();
    }

    /** @param m multiplicador de velocidad (estadística Speed) */
    public void setSpeedMultiplier(double m){
        speedMultiplier = m;
    }

    /** @param m multiplicador de salto (estadística Jump) */
    public void setJumpMultiplier(double m){
        jumpMultiplier = m;
    }

    /** @param m multiplicador de daño (estadística Kick) */
    public void setKickMultiplier(double m){
        kickMultiplier = m;
    }

    /** @param m multiplicador de vida máxima (estadística Survival) */
    public void setLifeMultiplier(double m){
        lifeMultiplier = m;
    }

    /**
     * Cambia la velocidad por un efecto (por ejemplo, electrocutado la baja a la mitad).
     * @param factor 1 es la velocidad sin efecto
     */
    public void setEffectSpeedFactor(double factor){
        effectSpeedFactor = factor;
        refreshSpeed();
    }

    /** Le pasa a su cuerpo la velocidad: la de su estadística por la del efecto. */
    private void refreshSpeed(){
        sprite.setSpeedFactor((float) (speedMultiplier * effectSpeedFactor));
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

    /** @return true si tiene la barra llena y no tiene aura, o sea, si puede activar el poder */
    public boolean canActivatePower(){
        return powerCharge >= FULL_CHARGE && !auraActive;
    }

    /**
     * @param target rectángulo de algo en la cancha, por ejemplo el balón
     * @return true si está al alcance de su pie
     */
    public boolean canKick(Rectangle target){
        return sprite.getBootBox().intersects(target);
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
     * Lo golpeó un tiro de poder: recibe el efecto del poder y el daño
     * DañoTiro x multiplicadorKick de quien disparó.
     * @param power poder que lo golpeó
     * @param shooter jugador que disparó
     */
    public void receivePowerShot(AbilityStrategy power, Player shooter){
        applyEffect(power.createEffect());
        takeDamage(power.getBaseDamage() * shooter.getKickMultiplier());
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
        sprite.launch(vx, vy);
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
     * Dibuja al jugador, su aura si tiene el poder activo y su efecto si tiene uno.
     * @param g donde se dibuja
     */
    public void draw(Graphics g){
        if (auraActive){
            drawAura(g);
        }
        sprite.drawSprite(g);
        state.draw(g, getBounds());
    }

    /**
     * El aura: un halo del color de su poder alrededor del cuerpo.
     * @param g donde se dibuja
     */
    private void drawAura(Graphics g){
        Color c = country.getAbility().getColor();
        Rectangle r = getBounds();
        int x = r.x - AURA_MARGIN;
        int y = r.y - AURA_MARGIN;
        int w = r.width + AURA_MARGIN + AURA_MARGIN;
        int h = r.height + AURA_MARGIN + AURA_MARGIN;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), AURA_ALPHA));
        g.fillOval(x, y, w, h);
        g.setColor(c);
        g.drawOval(x, y, w, h);
        g.drawOval(x + 1, y + 1, w - AURA_LINE, h - AURA_LINE);
    }

    /**
     * @param statName nombre de la estadística
     * @return nivel de esa estadística (todas empiezan en 1)
     */
    public int getLevel(String statName){
        Integer level = levels.get(statName);
        if (level == null){
            return INITIAL_LEVEL;
        }
        return level;
    }

    /**
     * @param statName nombre de la estadística
     * @return true si ya está en el nivel máximo (10)
     */
    public boolean isMaxLevel(String statName){
        return getLevel(statName) >= MAX_LEVEL;
    }

    /**
     * El precio se duplica con cada punto de mejora: 500, 1000, 2000...
     * @param statName nombre de la estadística
     * @return precio del siguiente punto de mejora
     */
    public int upgradePrice(String statName){
        int price = BASE_PRICE;
        for (int level = INITIAL_LEVEL; level < getLevel(statName); level++){
            price = price * PRICE_GROWTH;
        }
        return price;
    }

    /**
     * Compra un punto de mejora de una estadística con sus puntos.
     * @param statName nombre de la estadística
     * @throws POOBSoccerException si ya está en el nivel máximo o no le alcanzan los puntos
     */
    public void upgrade(String statName) throws POOBSoccerException{
        int level = getLevel(statName);
        if (isMaxLevel(statName)){
            throw new POOBSoccerException(POOBSoccerException.MAX_LEVEL_REACHED + statName + ".");
        }
        int price = upgradePrice(statName);
        if (points < price){
            throw new POOBSoccerException(POOBSoccerException.NOT_ENOUGH_POINTS + statName
                                          + " cuesta " + price + " y tienes " + points + ".");
        }
        points = points - price;
        levels.put(statName, level + 1);
    }

    /**
     * Recibe los puntos de un partido jugado.
     * @param amount puntos ganados
     */
    public void addPoints(int amount){
        points = points + amount;
        pointsGained = amount;
    }

    /**
     * Pone los puntos leídos de un archivo de progreso.
     * @param amount puntos
     * @throws POOBSoccerException si son negativos
     */
    public void setPoints(int amount) throws POOBSoccerException{
        if (amount < 0){
            throw new POOBSoccerException(POOBSoccerException.BAD_POINTS + amount);
        }
        points = amount;
    }

    /**
     * Pone el nivel de una estadística leído de un archivo de progreso.
     * @param statName nombre de la estadística
     * @param level nivel
     * @throws POOBSoccerException si el nivel no está entre 1 y 10
     */
    public void setLevel(String statName, int level) throws POOBSoccerException{
        if (level < INITIAL_LEVEL || level > MAX_LEVEL){
            throw new POOBSoccerException(POOBSoccerException.BAD_LEVEL + level);
        }
        levels.put(statName, level);
    }

    /**
     * Toma los puntos y los niveles de otro jugador (al importar un progreso ya revisado).
     * @param other jugador con el progreso leído
     */
    public void copyProgress(Player other){
        points = other.points;
        levels = new HashMap<String, Integer>(other.levels);
    }

    /** @return puntos que ganó en el último partido */
    public int getPointsGained(){
        return pointsGained;
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
