package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del Aura Azul de Corea del Sur: daño 40, sale recto y horizontal,
 * Empuje hacia su propio arco durante 1.5 s.
 */
public class BlueAuraShotTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int UPDATES_PER_SECOND = 100;
    private static final int PUSH_CYCLES = 150;
    private static final int FLIGHT_CYCLES = 10;
    private POOBSoccerGame game;
    private BlueAuraShot shot;

    /** Partido con un jugador de cada lado y el poder listo para usar. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Corea del Sur", "Camerún", null, "Por goles", 5, "500x400");
        shot = new BlueAuraShot();
    }

    private void tick(Player p, int updates)
    {
        for (int i = 0; i < updates; i++){
            p.update(UPDATES_PER_SECOND);
        }
    }

    /** Su daño base es 40. */
    @Test
    public void shouldHaveBaseDamage40()
    {
        assertEquals(40, shot.getBaseDamage());
    }

    /** Su efecto es el Empuje. */
    @Test
    public void shouldCausePush()
    {
        assertEquals("Empuje", shot.createEffect().getName());
    }

    /** El Empuje sigue un ciclo antes de 1.5 s. */
    @Test
    public void shouldPushUntilOneAndAHalfSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, PUSH_CYCLES - 1);
        assertEquals("Empuje", p.getEffectName());
    }

    /** El Empuje termina a los 1.5 s. */
    @Test
    public void shouldStopPushingAfterOneAndAHalfSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, PUSH_CYCLES);
        assertEquals("Normal", p.getEffectName());
    }

    /** El Empuje lanza al jugador izquierdo hacia la izquierda, donde está su arco. */
    @Test
    public void shouldPushPlayerTowardsOwnGoal()
    {
        Player p = game.getPlayer(Side.LEFT);
        int x = p.getBounds().x;
        p.applyEffect(shot.createEffect());
        tick(p, FLIGHT_CYCLES);
        assertTrue(p.getBounds().x < x);
    }

    /** El tiro va recto y horizontal: el balón no baja. */
    @Test
    public void shouldGoStraightAndHorizontal()
    {
        BallSprite ball = game.getMatch().getBall();
        ball.setPosition(200, 150);
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        for (int i = 0; i < FLIGHT_CYCLES; i++){
            ball.updateSprite();
        }
        assertEquals(150, ball.getMyRectangle().y);
        assertTrue(ball.getMyRectangle().x > 200);
    }
}
