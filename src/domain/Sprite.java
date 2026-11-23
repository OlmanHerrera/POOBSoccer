package src.domain;
import java.awt.*;
import java.awt.image.*;

/**
 * Objeto que se mueve y se dibuja en la cancha con una imagen o una secuencia de imágenes.
 */
public class Sprite{
    private static final int INITIAL_SPEED_X = 5;
    private static final int INITIAL_SPEED_Y = 0;
    public static final int SIZE = 12;

    protected PVector location;
    protected PVector velocity;
    private int panelWidth;
    private ImagesLoader loader;
    private String imageName;
    private BufferedImage image;
    private int width;
    private int height;
    private ImagesPlayer player;
    private boolean isLooping;

    /**
     * Crea el sprite con su imagen inicial.
     * @param x posición inicial en x
     * @param y posición inicial en y
     * @param panelWidth ancho de la cancha
     * @param loader cargador de imágenes
     * @param name nombre de la imagen
     */
    public Sprite(int x, int y, int panelWidth, ImagesLoader loader, String name){
        location = new PVector(x, y);
        this.panelWidth = panelWidth;
        velocity = new PVector(INITIAL_SPEED_X, INITIAL_SPEED_Y);
        this.loader = loader;
        setImage(name);
    }

    /**
     * Cambia la imagen del sprite; si no existe, se dibuja un círculo.
     * @param name nombre de la imagen
     */
    public void setImage(String name){
        imageName = name;
        image = loader.getImage(imageName);
        if (image == null){
            System.out.println("No sprite image for " + imageName);
            width = SIZE;
            height = SIZE;
        }
        else{
            width = image.getWidth();
            height = image.getHeight();
        }
        player = null;
        isLooping = false;
    }

    /**
     * Repite la secuencia de imágenes actual.
     * @param animPeriod período del juego en milisegundos
     * @param seqDuration duración de la secuencia en segundos
     */
    public void loopImage(int animPeriod, double seqDuration){
        if (loader.numImages(imageName) > 1){
            player = new ImagesPlayer(imageName, animPeriod, seqDuration, true, loader);
            isLooping = true;
        }
        else{
            System.out.println(imageName + " is not a sequence of images");
        }
    }

    /**
     * Muestra una vez la secuencia de imágenes actual y avisa al terminar.
     * @param animPeriod período del juego en milisegundos
     * @param seqDuration duración de la secuencia en segundos
     * @param w quien recibe el aviso
     */
    public void playImageOnce(int animPeriod, double seqDuration, ImagesPlayerWatcher w){
        if (loader.numImages(imageName) > 1){
            player = new ImagesPlayer(imageName, animPeriod, seqDuration, false, loader);
            player.setWatcher(w);
            isLooping = true;
        }
    }

    /** @return posición de la imagen actual en la secuencia, o -1 si no hay secuencia */
    public int getImagePosition(){
        if (isLooping){
            return player.getCurrentPosition();
        }
        return -1;
    }

    /** Detiene la secuencia de imágenes. */
    public void stopLooping(){
        if (isLooping){
            player.stop();
            isLooping = false;
        }
    }

    /** @return rectángulo que ocupa el sprite */
    public Rectangle getMyRectangle(){
        return new Rectangle((int) location.x, (int) location.y, width, height);
    }

    /** Mueve el sprite con su velocidad y avanza su secuencia de imágenes. */
    public void updateSprite(){
        location.add(velocity);
        if (isLooping){
            player.updateTick();
        }
    }

    /**
     * Dibuja el sprite.
     * @param g donde se dibuja
     */
    public void drawSprite(Graphics g){
        if (image == null){
            g.setColor(Color.yellow);
            g.fillOval((int) location.x, (int) location.y, SIZE, SIZE);
            g.setColor(Color.black);
            return;
        }
        if (isLooping){
            image = player.getCurrentImage();
        }
        g.drawImage(image, (int) location.x, (int) location.y, null);
    }

    /** @return ancho de la imagen */
    public int getWidth(){
        return width;
    }

    /** @return alto de la imagen */
    public int getHeight(){
        return height;
    }

    /** @return ancho de la cancha */
    public int getPWidth(){
        return panelWidth;
    }

    /**
     * @param x nueva posición en x
     * @param y nueva posición en y
     */
    public void setPosition(int x, int y){
        location.x = x;
        location.y = y;
    }

    /**
     * @param dx nueva velocidad en x
     * @param dy nueva velocidad en y
     */
    public void setVelocity(int dx, int dy){
        velocity.x = dx;
        velocity.y = dy;
    }

    /** @return posición y velocidad actuales */
    public Motion getMotion(){
        return new Motion(location.x, location.y, velocity.x, velocity.y);
    }

    /** @param m posición y velocidad guardadas */
    public void setMotion(Motion m){
        location.x = m.getX();
        location.y = m.getY();
        velocity.x = m.getVx();
        velocity.y = m.getVy();
    }
}
