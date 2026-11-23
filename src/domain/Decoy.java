package src.domain;

/**
 * Balón falso del tiro Ilusión, que desaparece cuando se acaba su tiempo.
 */
public class Decoy extends Sprite{
    private static final String IMAGE = "ball";
    private static final float GRAVITY = 0.05f;
    private static final float BOUNCE = 0.7f;

    private Field field;
    private int updatesLeft;

    /**
     * Crea un balón falso.
     * @param x x inicial
     * @param y y inicial
     * @param vx velocidad horizontal
     * @param vy velocidad vertical, negativa hacia arriba
     * @param lifeUpdates ciclos que dura antes de desaparecer
     * @param field cancha
     * @param loader cargador de imágenes
     */
    public Decoy(int x, int y, float vx, float vy, int lifeUpdates, Field field, ImagesLoader loader){
        super(x, y, field.getWidth(), loader, IMAGE);
        this.field = field;
        velocity.x = vx;
        velocity.y = vy;
        updatesLeft = lifeUpdates;
    }

    /** Un ciclo: cuenta su tiempo, cae, rebota y se mueve. */
    public void updateSprite(){
        updatesLeft--;
        velocity.y = velocity.y + GRAVITY;
        bounce();
        super.updateSprite();
    }

    /** Rebota en los bordes laterales y en el suelo. */
    private void bounce(){
        if (location.x <= 0 && velocity.x < 0){
            velocity.x = -velocity.x * BOUNCE;
        }
        else if (location.x + getWidth() >= getPWidth() && velocity.x > 0){
            velocity.x = -velocity.x * BOUNCE;
        }
        if (location.y + getHeight() >= field.getFloorY() && velocity.y > 0){
            location.y = field.getFloorY() - getHeight();
            velocity.y = -velocity.y * BOUNCE;
        }
    }

    /** @return true mientras no se le acabe el tiempo */
    public boolean isAlive(){
        return updatesLeft > 0;
    }
}
