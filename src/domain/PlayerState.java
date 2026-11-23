package src.domain;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Efecto activo sobre un jugador, con su duración contada en ciclos del juego.
 */
public abstract class PlayerState implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);
    private static final int FRAME_MARGIN = 3;
    private static final int LABEL_GAP = 6;

    private int elapsedUpdates;

    /**
     * Crea el efecto con su contador en cero.
     */
    public PlayerState(){
        elapsedUpdates = 0;
    }

    /**
     * Cuenta un ciclo y, si el efecto venció, llama a onExpire.
     * @param p jugador que tiene el efecto
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public void update(Player p, int updatesPerSecond){
        elapsedUpdates++;
        if (elapsedUpdates >= getDurationSeconds() * updatesPerSecond){
            onExpire(p);
        }
    }

    /**
     * Al vencer, el jugador vuelve a la normalidad.
     * @param p jugador que tiene el efecto
     */
    protected void onExpire(Player p){
        p.applyEffect(new NormalState());
    }

    /**
     * Hace visible el efecto: un marco de su color alrededor del jugador
     * y su nombre encima de la cabeza.
     * @param g donde se dibuja
     * @param body rectángulo del jugador
     */
    public void draw(Graphics g, Rectangle body){
        g.setColor(getColor());
        g.drawRect(body.x - FRAME_MARGIN, body.y - FRAME_MARGIN,
                   body.width + FRAME_MARGIN + FRAME_MARGIN, body.height + FRAME_MARGIN + FRAME_MARGIN);
        g.setFont(LABEL_FONT);
        g.drawString(getName(), body.x, body.y - LABEL_GAP);
    }

    /** @return color con el que se ve el efecto */
    protected abstract Color getColor();

    /** @return nombre del efecto para mostrar en pantalla */
    public abstract String getName();

    /** @return duración del efecto en segundos */
    protected abstract double getDurationSeconds();

    /**
     * Se ejecuta cuando el efecto empieza.
     * @param p jugador que recibe el efecto
     */
    protected abstract void enter(Player p);

    /**
     * Se ejecuta cuando el efecto termina o es reemplazado por otro.
     * @param p jugador que tenía el efecto
     */
    protected abstract void exit(Player p);

    /**
     * Decide qué acción se ejecuta realmente mientras dura el efecto.
     * @param a acción pedida
     * @return acción permitida, o null si no puede hacerla
     */
    protected abstract Action filter(Action a);
}
