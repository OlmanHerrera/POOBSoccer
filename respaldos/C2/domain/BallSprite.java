package src.domain;
import java.awt.*;
import java.util.ArrayList;

/**
 * Balón: gravedad, fricción, rebotes, choques con los jugadores y,
 * cuando lleva un tiro de poder, el poder y quién lo disparó.
 */
public class BallSprite extends Sprite
{
    //images used for the balls
    private static final String[] ballNames = {"ball"};



    private int nameIndex;

    private PVector acceleration = new PVector(0, 0);
    private float mass = 1;
    private static final float GRAVITY = 0.05f;
    private static final float TOP_SPEED = 8;
    private static final float FRICTION_C = 0.02f;
    private static final float NORMAL = 1;
    private static final float BOUNCE = 0.7f;
    private static final float MIN_BOUNCE = 0.5f;
    private static final float K = 0.15f;
    private static final float KICK_X = 4;
    private static final float KICK_Y = 3.5f;
    private static final int RIGHTWARD = 1;
    private static final int LEFTWARD = -1;
    private static final float DECOY_FIRST_SPEED_Y = -5.5f;
    private static final float DECOY_STEP_SPEED_Y = 1;

    private Field field;
    private ImagesLoader loader;
    private int updatesPerSecond;
    private PlayerSprite leftPlayer;
    private PlayerSprite rightPlayer;
    private Rectangle previousRectangle;
    private AbilityStrategy power;
    private Side powerOwner;
    private ArrayList<Decoy> decoys;


    /**
     * Crea el balón en el centro de la cancha, arriba.
     * @param field cancha
     * @param imsLd cargador de imágenes
     * @param l cuerpo del jugador izquierdo
     * @param r cuerpo del jugador derecho
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public BallSprite(Field field, ImagesLoader imsLd, PlayerSprite l, PlayerSprite r, int updatesPerSecond)
    {   super(field.getWidth()/2, 0, field.getWidth(), field.getHeight(), imsLd, ballNames[0]);
            // the ball is positioned in the middle at the top of the field
        this.field = field;
        loader = imsLd;
        this.updatesPerSecond = updatesPerSecond;
        decoys = new ArrayList<Decoy>();
        leftPlayer = l;
        rightPlayer = r;

        nameIndex = 0;

        initPosition();
        previousRectangle = getMyRectangle();
    }



    /**
     * Vuelve al centro de la cancha, arriba, quieto y sin poder (después de un gol).
     */
    public void initPosition(){
        setImage(ballNames[nameIndex]);
        // set a diferent nameIndex

        setPosition((int) (getPWidth()/2), 0);
                        //half of the top
        setVelocity(0,0);
        discharge();

    }

    /**
     * El balón recibe un tiro de poder.
     * @param p poder que lleva
     * @param owner lado del jugador que lo disparó
     */
    public void charge(AbilityStrategy p, Side owner)
    {
        power = p;
        powerOwner = owner;
    }

    /** El balón deja de llevar el poder. */
    public void discharge()
    {
        power = null;
    }

    /** @return poder que lleva, o null si no lleva ninguno */
    public AbilityStrategy getPower()
    {
        return power;
    }

    /** @return lado del jugador que disparó el poder */
    public Side getPowerOwner()
    {
        return powerOwner;
    }

    /** @return 1 si el poder va hacia la derecha, -1 si va hacia la izquierda */
    public int getPowerDirection()
    {
        if (powerOwner == Side.LEFT){
            return RIGHTWARD;
        }
        return LEFTWARD;
    }

    /**
     * Cambia la velocidad del balón de golpe (salida de un tiro de poder).
     * @param vx velocidad horizontal
     * @param vy velocidad vertical (negativa hacia arriba)
     */
    public void shoot(float vx, float vy)
    {
        velocity.x = vx;
        velocity.y = vy;
        acceleration.mult(0);
    }

    /**
     * Mantiene un tiro recto y horizontal hacia el arco rival.
     * @param speed rapidez del tiro
     */
    public void keepStraight(float speed)
    {
        velocity.x = getPowerDirection() * speed;
        velocity.y = 0;
    }

    /**
     * Mantiene un tiro a ras de suelo hacia el arco rival, con saltos cortos.
     * @param speed rapidez horizontal
     * @param hop rapidez de cada salto al tocar el suelo
     */
    public void rollOnFloor(float speed, float hop)
    {
        velocity.x = getPowerDirection() * speed;
        if (isOnFloor()){
            velocity.y = -hop;
        }
    }

    /**
     * Crea balones falsos que salen desde el balón con distintas alturas.
     * @param count cuántos balones falsos
     * @param seconds cuánto duran
     */
    public void addDecoys(int count, double seconds)
    {
        int lifeUpdates = (int) (seconds * updatesPerSecond);
        Rectangle r = getMyRectangle();
        for (int i = 0; i < count; i++){
            float vy = DECOY_FIRST_SPEED_Y + i * DECOY_STEP_SPEED_Y;
            decoys.add(new Decoy(r.x, r.y, velocity.x, vy, lifeUpdates, field, loader));
        }
    }

    /** Mueve los balones falsos y quita los que ya desaparecieron. */
    private void updateDecoys()
    {
        for (int i = decoys.size() - 1; i >= 0; i--){
            Decoy d = decoys.get(i);
            d.updateSprite();
            if (!d.isAlive()){
                decoys.remove(i);
            }
        }
    }

    /** @return cuántos balones falsos hay en la cancha */
    public int getDecoyCount()
    {
        return decoys.size();
    }

    /**
     * Dibuja el balón y sus balones falsos, que se ven igual.
     * @param g donde se dibuja
     */
    public void drawSprite(Graphics g)
    {
        super.drawSprite(g);
        for (int i = 0; i < decoys.size(); i++){
            decoys.get(i).drawSprite(g);
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

    /**
     * Un ciclo del balón: fuerzas, choques, rebotes, el poder si lo lleva, y se mueve.
     */
    public void updateSprite()
    {
        previousRectangle = getMyRectangle(); // donde estaba antes de moverse
        PVector gravity = new PVector(0, GRAVITY * mass);
        applyForce(gravity);
        if (isOnFloor()){
            applyFriction();
        }
        hasHitPlayer(leftPlayer);
        hasHitPlayer(rightPlayer);

        velocity.add(acceleration);
        velocity.limit(TOP_SPEED);
        acceleration.mult(0);

        hasHitWall();
        if (power != null){
            power.steer(this);
        }
        super.updateSprite();
        updateDecoys();
    }

    /** @return true si el balón está tocando el suelo */
    public boolean isOnFloor()
    {
        return location.y + getHeight() >= field.getFloorY() - 1;
    }

    /**
     * @return rectángulo del balón antes de su último movimiento
     */
    public Rectangle getPreviousRectangle()
    {
        return previousRectangle;
    }

    private void applyFriction()
    {
        PVector friction = velocity.get();
        friction.mult(-1);
        friction.normalize();
        friction.mult(FRICTION_C * NORMAL);
        applyForce(friction);
    }

    private void hasHitPlayer(PlayerSprite player)
    {
        Rectangle b = getMyRectangle();
        if (!b.intersects(player.getMyRectangle())){
            return;
        }

        if (player.isKickActive() && b.intersects(player.getBootBox())){
            kicked(player);
            return;
        }

        PVector ballCenter = new PVector(b.x + b.width/2, b.y + b.height/2);
        PVector dir = ballCenter.get();
        dir.sub(player.getHeadCenter());
        float distance = dir.mag();
        float restLength = player.getHeadRadius() + b.width/2;

        if (distance < restLength){
            float stretch = restLength - distance;
            dir.normalize();
            dir.mult(K * stretch);
            applyForce(dir);
        }
    }

    private void kicked(PlayerSprite player)
    {
        float dir = 1;
        if (player.getSide() == Side.RIGHT){
            dir = -1;
        }
        velocity.mult(0);
        applyForce(new PVector(dir * KICK_X, -KICK_Y));
        player.markKickHit();
    }

    /**
     * Rebota sobre una superficie horizontal (el travesaño), perdiendo energía.
     * @param surfaceY y de la superficie
     */
    public void bounceOn(int surfaceY)
    {
        location.y = surfaceY - getHeight();
        if (velocity.y > 0){
            velocity.y = -velocity.y * BOUNCE;
            if (Math.abs(velocity.y) < MIN_BOUNCE){
                velocity.y = 0;
            }
        }
    }

    /**
     * Rebota en los bordes de la cancha y en el suelo. Si un tiro de poder
     * llega a un borde lateral, se pierde: el balón deja de llevar el poder.
     */
    public void hasHitWall(){
        if ((location.x <= 0)&& (velocity.x < 0)){
            location.x = 0;
            velocity.x = -velocity.x * BOUNCE; // move Right
            discharge();
        }
        else if ((location.x + getWidth() >= getPWidth()) && (velocity.x > 0)){
            location.x = getPWidth() - getWidth();
            velocity.x = -velocity.x * BOUNCE;
            discharge();
        }

        int floorY = field.getFloorY();
        if ((location.y + getHeight() >= floorY) && (velocity.y > 0)){
            location.y = floorY - getHeight();
            velocity.y = -velocity.y * BOUNCE;
            if (Math.abs(velocity.y) < MIN_BOUNCE){
                velocity.y = 0;
            }
        }
        else if((location.y <= 0) && (velocity.y < 0)){
            location.y = 0;
            velocity.y = -velocity.y;
        }

    }





}
