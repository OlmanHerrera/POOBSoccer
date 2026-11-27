package src.domain;
import java.awt.Rectangle;

/**
 * Cuerpo de un jugador en la cancha: imagen, movimiento, salto, patada y zonas de choque.
 */
public class PlayerSprite extends Sprite implements ImagesPlayerWatcher{
    private static final double RUN_DURATION = 0.9;
    private static final double KICK_DURATION = 0.5;
    private static final int XSTEP = 1;
    private static final float GRAVITY = 0.1f;
    private static final float JUMP = 4;
    private static final int KICK_FIRST_ACTIVE_FRAME = 2;
    private static final int KICK_LAST_ACTIVE_FRAME = 3;
    private static final int BODY_THIRDS = 3;
    private static final int BOOT_EXTRA_WIDTH = 4;
    private static final int KICK_REACH = 6;

    private int period;
    private String runImage;
    private String kickImage;
    private Side side;
    private Action lastAction = Action.STAY_STILL;
    private PVector acceleration = new PVector(0, 0);
    private float mass = 1;
    private int startX;
    private int groundY;
    private float speedFactor = 1;
    private float jumpFactor = 1;
    private boolean kicking = false;
    private boolean kickHitDone = false;

    /**
     * Crea el cuerpo de un jugador parado en el suelo, en su posición inicial.
     * @param field cancha
     * @param loader cargador de imágenes
     * @param period período del juego en milisegundos
     * @param runImage nombre de la animación de correr
     * @param side lado de la cancha
     */
    public PlayerSprite(Field field, ImagesLoader loader, int period, String runImage, Side side){
        super(field.getStartX(side), 0, field.getWidth(), loader, runImage);
        this.runImage = runImage;
        kickImage = runImage.replace("_run", "_kick");
        this.period = period;
        setVelocity(0, 0);
        this.side = side;
        startX = field.getStartX(side) - getWidth() / 2;
        location.x = startX;
        groundY = field.getFloorY() - getHeight();
        location.y = groundY;
    }

    /** Camina hacia la izquierda. */
    public void moveLeft(){
        velocity.x = -XSTEP * speedFactor;
        if (!kicking){
            setImage(runImage);
            loopImage(period, RUN_DURATION);
        }
    }

    /** Se queda quieto en x. */
    public void stayStill(){
        velocity.x = 0;
        if (!kicking){
            stopLooping();
        }
    }

    /** Camina hacia la derecha. */
    public void moveRight(){
        velocity.x = XSTEP * speedFactor;
        if (!kicking){
            setImage(runImage);
            loopImage(period, RUN_DURATION);
        }
    }

    /**
     * Cambia qué tan rápido camina; si ya va caminando, cambia de una vez.
     * @param factor 1 es la velocidad base
     */
    public void setSpeedFactor(float factor){
        speedFactor = factor;
        if (velocity.x > 0){
            velocity.x = XSTEP * speedFactor;
        }
        else if (velocity.x < 0){
            velocity.x = -XSTEP * speedFactor;
        }
    }

    /** Empieza una patada si no está pateando. */
    public void kick(){
        if (kicking){
            return;
        }
        kicking = true;
        kickHitDone = false;
        setImage(kickImage);
        playImageOnce(period, KICK_DURATION, this);
    }

    /**
     * Al terminar la patada vuelve a la animación de correr.
     * @param name nombre de la secuencia que terminó
     */
    public void sequenceEnded(String name){
        kicking = false;
        setImage(runImage);
        if (velocity.x != 0){
            loopImage(period, RUN_DURATION);
        }
    }

    /** @return true si el pie está golpeando y todavía no golpeó nada en esta patada */
    public boolean isKickActive(){
        if (!kicking){
            return false;
        }
        if (kickHitDone){
            return false;
        }
        int frame = getImagePosition();
        if (frame >= KICK_FIRST_ACTIVE_FRAME && frame <= KICK_LAST_ACTIVE_FRAME){
            return true;
        }
        return false;
    }

    /** Marca que la patada ya golpeó algo. */
    public void markKickHit(){
        kickHitDone = true;
    }

    /** @return centro de la cabeza */
    public PVector getHeadCenter(){
        Rectangle r = getMyRectangle();
        return new PVector(r.x + r.width / 2, r.y + r.height / BODY_THIRDS);
    }

    /** @return radio de la cabeza */
    public float getHeadRadius(){
        return getMyRectangle().width / 2;
    }

    /** @return zona del pie que patea, adelante según el lado */
    public Rectangle getBootBox(){
        Rectangle r = getMyRectangle();
        int w = r.width / 2 + BOOT_EXTRA_WIDTH;
        int h = r.height / BODY_THIRDS;
        if (side == Side.LEFT){
            return new Rectangle(r.x + r.width - w, r.y + r.height - h, w, h);
        }
        return new Rectangle(r.x, r.y + r.height - h, w, h);
    }

    /** @return zona que alcanza la patada: el guayo y un poco más adelante */
    public Rectangle getKickBox(){
        Rectangle boot = getBootBox();
        if (side == Side.LEFT){
            return new Rectangle(boot.x, boot.y, boot.width + KICK_REACH, boot.height);
        }
        return new Rectangle(boot.x - KICK_REACH, boot.y, boot.width + KICK_REACH, boot.height);
    }

    /** @return true mientras dura la animación de la patada */
    public boolean isKicking(){
        return kicking;
    }

    /** @return velocidad horizontal, positiva hacia la derecha */
    public float getSpeedX(){
        return velocity.x;
    }

    /** Salta si está en el suelo. */
    public void jump(){
        if (isOnGround()){
            applyForce(new PVector(0, -JUMP * jumpFactor));
        }
    }

    /**
     * Cambia la fuerza del salto.
     * @param factor 1 es el salto base
     */
    public void setJumpFactor(float factor){
        jumpFactor = factor;
    }

    /**
     * Lanza el cuerpo con una velocidad.
     * @param vx velocidad horizontal
     * @param vy velocidad vertical, negativa hacia arriba
     */
    public void launch(float vx, float vy){
        velocity.x = vx;
        velocity.y = vy;
    }

    /**
     * Suma una fuerza dividida por la masa.
     * @param force fuerza
     */
    public void applyForce(PVector force){
        PVector f = force.get();
        f.div(mass);
        acceleration.add(f);
    }

    private boolean isOnGround(){
        return location.y >= groundY;
    }

    /** Avanza el cuerpo un ciclo: gravedad, bordes, movimiento y suelo. */
    public void updateSprite(){
        applyForce(new PVector(0, GRAVITY * mass));
        velocity.add(acceleration);
        acceleration.mult(0);
        checkEdges();
        super.updateSprite();
        checkGround();
    }

    private void checkGround(){
        if (location.y >= groundY){
            location.y = groundY;
            if (velocity.y > 0){
                velocity.y = 0;
            }
        }
    }

    private void checkEdges(){
        if ((location.x + velocity.x < 0) && (velocity.x < 0)){
            location.x = 0;
            stayStill();
        }
        else if ((location.x + getWidth() + velocity.x > getPWidth()) && (velocity.x > 0)){
            location.x = getPWidth() - getWidth();
            stayStill();
        }
    }

    /** Se detiene aunque la última acción haya sido moverse. */
    public void stop(){
        lastAction = Action.STAY_STILL;
        stayStill();
    }

    /** Vuelve quieto a su posición inicial. */
    public void returnToStart(){
        setPosition(startX, groundY);
        setVelocity(0, 0);
        stop();
    }

    /** @return lado de la cancha */
    public Side getSide(){
        return side;
    }

    /**
     * Ejecuta una acción; moverse o quedarse quieto solo si cambia respecto a la anterior.
     * @param a acción pedida
     */
    public void perform(Action a){
        if (a == Action.JUMP){
            jump();
            return;
        }
        if (a == Action.KICK){
            kick();
            return;
        }
        if ((a == null) || (a == lastAction)){
            return;
        }
        lastAction = a;
        if (a == Action.MOVE_LEFT){
            moveLeft();
        }
        else if (a == Action.MOVE_RIGHT){
            moveRight();
        }
        else if (a == Action.STAY_STILL){
            stayStill();
        }
    }
}
