
package src.domain;

import java.awt.Rectangle;

/**
 * Cuerpo de un jugador en la cancha (KGPJ cap. 11): imagen, animación de correr
 * y de patear, movimiento, salto con gravedad y cajas de choque de cabeza y pie.
 * No tiene reglas del juego: solo ejecuta lo que decide su jugador.
 */
public  class PlayerSprite extends Sprite implements ImagesPlayerWatcher{

    private static final double  DURATION = 0.9;

    private int period;
    /*in ms. The game's animation period used by the image cycling */


    // global
    private String imageName;

    private static final int XSTEP = 1;
        //step distance for moving along x-axis

    private Side side;
    private Action lastAction = Action.STAY_STILL;

    private PVector acceleration = new PVector(0, 0);
    private float mass = 1;
    private int startX;
    private int groundY;
    private float speedFactor = 1;
    private static final float GRAVITY = 0.1f;
    private static final float JUMP = 4;

    private String kickName;
    private boolean kicking = false;
    private boolean kickHitDone = false;
    private static final double KICK_DURATION = 0.5;
    private static final int KICK_FIRST_ACTIVE_FRAME = 2;
    private static final int KICK_LAST_ACTIVE_FRAME = 3;
    private static final int BODY_THIRDS = 3;      // la cabeza es el tercio de arriba y el pie el de abajo
    private static final int BOOT_EXTRA_WIDTH = 4;



    /**
     * Crea el cuerpo de un jugador parado en el suelo, en su posición inicial.
     * @param field cancha
     * @param imsLd cargador de imágenes
     * @param p período del bucle en milisegundos, para las animaciones
     * @param fnm nombre de la animación de correr
     * @param side lado de la cancha
     */
    public PlayerSprite(Field field, ImagesLoader imsLd, int p, String fnm, Side side)
    {

        super(field.getStartX(side), 0, field.getWidth(), field.getHeight(), imsLd, fnm);
        this.imageName = fnm;
        this.kickName = fnm.replace("_run", "_kick");
        period = p;
        setVelocity(0,0); // no movement
        this.side = side;
        startX = field.getStartX(side);
        groundY = field.getFloorY() - getHeight(); // los pies quedan sobre el suelo
        location.y = groundY;


    }



    /** Camina hacia la izquierda. */
    public void moveLeft()
    {
        velocity.x = -XSTEP * speedFactor;
        if (!kicking){
            setImage(imageName);
            loopImage(period,DURATION);
        }
    }

    /** Se queda quieto en x. */
    public void stayStill()
    {
        velocity.x = 0;
        if (!kicking){
            stopLooping();
        }
    }

    /** Camina hacia la derecha. */
    public void moveRight()
    {
        velocity.x = XSTEP * speedFactor;
        if (!kicking){
            setImage(imageName);
            loopImage(period, DURATION);
        }
    }

    /**
     * Cambia qué tan rápido camina; si ya iba caminando, cambia de una vez.
     * @param factor 1 es la velocidad base
     */
    public void setSpeedFactor(float factor)
    {
        speedFactor = factor;
        if (velocity.x > 0){
            velocity.x = XSTEP * speedFactor;
        }
        else if (velocity.x < 0){
            velocity.x = -XSTEP * speedFactor;
        }
    }

    /** Empieza una patada, si no está pateando ya. */
    public void kick()
    {
        if (kicking){
            return;
        }
        kicking = true;
        kickHitDone = false;
        setImage(kickName);
        playImageOnce(period, KICK_DURATION, this);
    }

    /**
     * Terminó la animación de la patada: vuelve a la de correr.
     * @param name nombre de la secuencia que terminó
     */
    public void sequenceEnded(String name)
    {
        kicking = false;
        setImage(imageName);
        if (velocity.x != 0){
            loopImage(period, DURATION);
        }
    }

    /** @return true si la patada está en los cuadros en que el pie golpea y aún no golpeó */
    public boolean isKickActive()
    {
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

    /** La patada ya golpeó algo: no vuelve a golpear en esta patada. */
    public void markKickHit()
    {
        kickHitDone = true;
    }

    /** @return centro de la cabeza (círculo de choque con el balón) */
    public PVector getHeadCenter()
    {
        Rectangle r = getMyRectangle();
        return new PVector(r.x + r.width/2, r.y + r.height/BODY_THIRDS);
    }

    /** @return radio de la cabeza */
    public float getHeadRadius()
    {
        return getMyRectangle().width/2;
    }

    /** @return caja del pie que patea, adelante según el lado */
    public Rectangle getBootBox()
    {
        Rectangle r = getMyRectangle();
        int w = r.width/2 + BOOT_EXTRA_WIDTH;
        int h = r.height/BODY_THIRDS;
        if (side == Side.LEFT){
            return new Rectangle(r.x + r.width - w, r.y + r.height - h, w, h);
        }
        return new Rectangle(r.x, r.y + r.height - h, w, h);
    }

    /** Salta si está en el suelo. */
    public void jump()
    {
        if (isOnGround()){
            applyForce(new PVector(0, -JUMP));
        }
    }

    /**
     * Suma una fuerza dividida por la masa (Nature of Code, cap. 2).
     * @param force fuerza
     */
    public void applyForce(PVector force)
    {
        PVector f = force.get();
        f.div(mass);
        acceleration.add(f);
    }

    private boolean isOnGround()
    {
        return location.y >= groundY;
    }

    /** Un ciclo del cuerpo: gravedad, bordes, movimiento y suelo. */
    public void updateSprite()
    {
        applyForce(new PVector(0, GRAVITY * mass));
        velocity.add(acceleration);
        acceleration.mult(0);

        checkEdges();
        super.updateSprite();
        checkGround();
    }



    private void checkGround()
    {
        if (location.y >= groundY){
            location.y = groundY;
            if (velocity.y > 0){
                velocity.y = 0;
            }
        }
    }

    private void checkEdges()
    {
        if ((location.x + velocity.x < 0) && (velocity.x < 0)){
            location.x = 0;
            stayStill();
        }
        else if ((location.x + getWidth() + velocity.x > getPWidth()) && (velocity.x > 0)){
            location.x = getPWidth() - getWidth();
            stayStill();
        }
    }
    /**
     * Se detiene aunque la última acción pedida haya sido moverse.
     */
    public void stop()
    {
        lastAction = Action.STAY_STILL;
        stayStill();
    }

    /**
     * Vuelve quieto a su posición inicial, parado en el suelo (después de un gol).
     */
    public void returnToStart()
    {
        setPosition(startX, groundY);
        setVelocity(0, 0);
        stop();
    }

    /** @return lado de la cancha */
    public Side getSide()
    {
        return side;
    }

    /**
     * Ejecuta una acción. Saltar y patear siempre se ejecutan; moverse o quedarse
     * quieto solo si cambia respecto a la última acción.
     * @param a acción pedida
     */
    public void perform(Action a)

    {
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
            this.moveLeft();
        }
        else if (a == Action.MOVE_RIGHT){
            this.moveRight();
        }
        else if (a == Action.STAY_STILL){
            this.stayStill();
        }
    }
}
