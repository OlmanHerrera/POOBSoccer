package src.domain;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Partido entre dos jugadores que avanza un ciclo a la vez.
 */
public class Match implements Serializable{
    private static final long serialVersionUID = 1L;
    public static final int SECONDS_PER_MINUTE = 60;
    private static final int WINNER_POINTS = 100;
    private static final int LOSER_POINTS = 25;
    private static final String BACKGROUND = "background";
    private static final String TIME_FORMAT = "%02d:%02d";

    private transient Field field;
    private transient MatchRules rules;
    private transient ArrayList<FieldElement> elements;
    private transient ImagesLoader loader;
    private transient BallSprite ball;
    private transient ArrayList<MatchObserver> observers = new ArrayList<MatchObserver>();
    private String fieldId;
    private String rulesId;
    private Motion ballMotion;
    private Side ballPowerOwner;
    private int limit;
    private Player leftPlayer;
    private Player rightPlayer;
    private int updatesPerSecond;
    private int elapsedUpdates = 0;
    private boolean paused = false;
    private boolean over = false;
    private Side winner = null;

    /**
     * Crea un partido con su balón.
     * @param field cancha
     * @param rules tipo de partido
     * @param limit minutos o goles, según el tipo de partido
     * @param elements elementos de la cancha
     * @param leftPlayer jugador izquierdo ya preparado
     * @param rightPlayer jugador derecho ya preparado
     * @param loader cargador de imágenes
     * @param updatesPerSecond ciclos del juego por segundo
     */
    public Match(Field field, MatchRules rules, int limit, ArrayList<FieldElement> elements,
                 Player leftPlayer, Player rightPlayer, ImagesLoader loader, int updatesPerSecond){
        this.field = field;
        this.rules = rules;
        this.limit = limit;
        this.elements = elements;
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
        this.loader = loader;
        this.updatesPerSecond = updatesPerSecond;
        fieldId = field.getId();
        rulesId = rules.getId();
        ball = new BallSprite(field, loader, leftPlayer.getSprite(), rightPlayer.getSprite(), updatesPerSecond);
    }

    /** Anota el movimiento del balón y de los jugadores antes de guardar. */
    public void prepareToSave(){
        ballMotion = ball.getMotion();
        ballPowerOwner = null;
        if (ball.getPower() != null){
            ballPowerOwner = ball.getPowerOwner();
        }
        leftPlayer.prepareToSave();
        rightPlayer.prepareToSave();
    }

    /**
     * Reconstruye la cancha, las reglas, los jugadores y el balón de un partido abierto.
     * @param catalog catálogo del juego
     * @param loader cargador de imágenes
     * @param period período del juego en milisegundos
     * @throws POOBSoccerException si algo guardado ya no está en el catálogo
     */
    public void restore(Catalog catalog, ImagesLoader loader, int period) throws POOBSoccerException{
        field = catalog.getField(fieldId);
        rules = catalog.getRules(rulesId);
        elements = catalog.getElements();
        this.loader = loader;
        observers = new ArrayList<MatchObserver>();
        leftPlayer.restore(catalog, field, loader, period);
        rightPlayer.restore(catalog, field, loader, period);
        ball = new BallSprite(field, loader, leftPlayer.getSprite(), rightPlayer.getSprite(), updatesPerSecond);
        ball.setMotion(ballMotion);
        if (ballPowerOwner != null){
            ball.charge(getPlayer(ballPowerOwner).getCountry().getAbility(), ballPowerOwner);
        }
    }

    /** @param o objeto que recibe los avisos de goles y del final */
    public void addObserver(MatchObserver o){
        if (!observers.contains(o)){
            observers.add(o);
        }
    }

    private void notifyGoal(GoalEvent e){
        for (int i = 0; i < observers.size(); i++){
            observers.get(i).goalScored(e);
        }
    }

    private void notifyEnd(GoalEvent e){
        for (int i = 0; i < observers.size(); i++){
            observers.get(i).matchEnded(e);
        }
    }

    /** Avanza el partido un ciclo. */
    public void update(){
        if (over || paused){
            return;
        }
        elapsedUpdates++;
        leftPlayer.think(this);
        rightPlayer.think(this);
        ball.updateSprite();
        leftPlayer.update(updatesPerSecond);
        rightPlayer.update(updatesPerSecond);
        leftPlayer.tryKick(rightPlayer);
        rightPlayer.tryKick(leftPlayer);
        checkPowerShot(leftPlayer);
        checkPowerShot(rightPlayer);
        checkPowerHit();
        checkElements();
        if (rules.isOver(this)){
            finish();
        }
    }

    /**
     * Si el jugador tiene el aura y toca el balón, dispara su tiro de poder.
     * @param p jugador
     */
    private void checkPowerShot(Player p){
        if (!p.isAuraActive()){
            return;
        }
        if (!ball.getMyRectangle().intersects(p.getBounds())){
            return;
        }
        p.shootPower(ball);
    }

    /** Si el balón con poder toca al rival de quien disparó, el rival recibe el tiro. */
    private void checkPowerHit(){
        AbilityStrategy power = ball.getPower();
        if (power == null){
            return;
        }
        Player shooter = getPlayer(ball.getPowerOwner());
        Player rival = getOpponent(shooter);
        if (!ball.getMyRectangle().intersects(rival.getBounds())){
            return;
        }
        rival.receivePowerShot(power, shooter);
        ball.discharge();
    }

    private void checkElements(){
        for (int i = 0; i < elements.size(); i++){
            elements.get(i).interact(ball, field, this);
        }
    }

    /** Termina el partido, reparte los puntos y avisa que terminó. */
    private void finish(){
        over = true;
        winner = rules.winner(this);
        Player winnerPlayer = getPlayer(winner);
        winnerPlayer.addPoints(WINNER_POINTS);
        getOpponent(winnerPlayer).addPoints(LOSER_POINTS);
        notifyEnd(new GoalEvent(winner));
    }

    /**
     * Suma un gol y deja todo listo para seguir desde el centro.
     * @param scorer lado que marcó
     */
    public void goal(Side scorer){
        Player p = getPlayer(scorer);
        p.scoreGoal();
        getOpponent(p).concede();
        ball.initPosition();
        leftPlayer.returnToStart();
        rightPlayer.returnToStart();
        notifyGoal(new GoalEvent(scorer));
    }

    /** Pausa el partido. */
    public void pause(){
        paused = true;
    }

    /** Reanuda el partido. */
    public void resume(){
        paused = false;
    }

    /** @return true si el partido está en pausa */
    public boolean isPaused(){
        return paused;
    }

    /** Termina el partido antes de tiempo, sin ganador. */
    public void abandon(){
        over = true;
        winner = null;
    }

    /**
     * Dibuja la cancha, el balón, los jugadores y los elementos de la cancha.
     * @param g donde se dibuja
     */
    public void draw(Graphics g){
        g.drawImage(loader.getImage(BACKGROUND), 0, 0, field.getWidth(), field.getHeight(), null);
        ball.drawSprite(g);
        leftPlayer.draw(g);
        rightPlayer.draw(g);
        for (int i = 0; i < elements.size(); i++){
            elements.get(i).draw(g, field, loader);
        }
    }

    /**
     * @param s lado
     * @return jugador de ese lado
     */
    public Player getPlayer(Side s){
        if (s == Side.LEFT){
            return leftPlayer;
        }
        return rightPlayer;
    }

    /**
     * @param p un jugador del partido
     * @return el otro jugador
     */
    public Player getOpponent(Player p){
        if (p == leftPlayer){
            return rightPlayer;
        }
        return leftPlayer;
    }

    /**
     * @param s lado
     * @return goles del jugador de ese lado
     */
    public int getGoals(Side s){
        return getPlayer(s).getGoals();
    }

    /** @return lado que va ganando, o null si van empatados */
    public Side getLeader(){
        if (leftPlayer.getGoals() > rightPlayer.getGoals()){
            return Side.LEFT;
        }
        if (rightPlayer.getGoals() > leftPlayer.getGoals()){
            return Side.RIGHT;
        }
        return null;
    }

    /** @return true si el partido ya terminó */
    public boolean isOver(){
        return over;
    }

    /** @return lado ganador, o null si no hubo */
    public Side getWinner(){
        return winner;
    }

    /** @return cancha del partido */
    public Field getField(){
        return field;
    }

    /** @return minutos o goles del límite */
    public int getLimit(){
        return limit;
    }

    /** @return segundos jugados */
    public int getElapsedSeconds(){
        return elapsedUpdates / updatesPerSecond;
    }

    /** @return texto del tiempo según el tipo de partido */
    public String getStatusText(){
        return rules.getStatusText(this);
    }

    /**
     * @param seconds segundos
     * @return el tiempo como mm:ss
     */
    public String formatTime(int seconds){
        return String.format(TIME_FORMAT, seconds / SECONDS_PER_MINUTE, seconds % SECONDS_PER_MINUTE);
    }

    /** @return rectángulo del balón */
    public Rectangle getBallBounds(){
        return ball.getMyRectangle();
    }

    /** @return copia de la velocidad del balón */
    public PVector getBallVelocity(){
        return ball.velocity.get();
    }

    /**
     * @param s lado del arco
     * @return x de la línea de gol de ese lado
     */
    public int getGoalLineX(Side s){
        return field.getGoalLineX(s);
    }
}
