package src.test;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;

/**
 * Pruebas de lo que los efectos dejan o no dejan hacer al jugador.
 */
public class EffectTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final int SETTLE = 200;
    private static final int SHORT_TIME = 20;
    private static final int TEN_SECONDS = 1000;
    private static final double LETHAL_DAMAGE = 1000;

    private Player player;

    /** Un jugador de Camerún que ya cayó al suelo. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        Catalog catalog = new Catalog();
        Field field = catalog.getField("500x400");
        ImagesLoader loader = new ImagesLoader("imsInfo.txt");
        player = new Player(Side.LEFT);
        player.prepare(catalog.getCountry("Camerún"), null, field, loader, PERIOD, catalog.getStats());
        play(SETTLE);
    }

    private void play(int updates){
        for (int i = 0; i < updates; i++){
            player.update(UPDATES_PER_SECOND);
        }
    }

    /** Sin efecto, el jugador salta. */
    @Test
    public void shouldJumpWithoutEffect(){
        int floorY = player.getBounds().y;
        player.perform(Action.JUMP);
        play(SHORT_TIME);
        assertTrue(player.getBounds().y < floorY);
    }

    /** Electrocutado, el salto se ignora. */
    @Test
    public void shouldNotJumpWhileElectrocuted(){
        player.applyEffect(new ElectrocutedState());
        int floorY = player.getBounds().y;
        player.perform(Action.JUMP);
        play(SHORT_TIME);
        assertEquals(floorY, player.getBounds().y);
    }

    /** Aturdido, el jugador no camina aunque se lo pidan. */
    @Test
    public void shouldNotWalkWhileStunned(){
        player.takeDamage(LETHAL_DAMAGE);
        assertEquals("Aturdido", player.getEffectName());
        int startX = player.getBounds().x;
        player.perform(Action.MOVE_RIGHT);
        play(SHORT_TIME);
        assertEquals(startX, player.getBounds().x);
    }

    /** El estado normal nunca vence. */
    @Test
    public void shouldNeverExpireTheNormalState(){
        play(TEN_SECONDS);
        assertEquals("Normal", player.getEffectName());
    }
}