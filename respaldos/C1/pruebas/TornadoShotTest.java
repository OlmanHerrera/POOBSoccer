package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del Tornado de Nigeria: daño 40, Inconsciente durante 3 s
 * (lanzado al aire, no puede moverse, saltar ni patear).
 */
public class TornadoShotTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int UPDATES_PER_SECOND = 100;
    private static final int THREE_SECONDS = 300;
    private static final int FEW_CYCLES = 10;
    private POOBSoccerGame game;
    private TornadoShot shot;

    /** Partido con un jugador de cada lado. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Nigeria", "Camerún", null, "Por goles", 5, "500x400");
        shot = new TornadoShot();
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

    /** Su efecto es Inconsciente. */
    @Test
    public void shouldKnockOut()
    {
        assertEquals("Inconsciente", shot.createEffect().getName());
    }

    /** Inconsciente sigue un ciclo antes de 3 s. */
    @Test
    public void shouldKnockOutUntilThreeSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, THREE_SECONDS - 1);
        assertEquals("Inconsciente", p.getEffectName());
    }

    /** Inconsciente termina a los 3 s. */
    @Test
    public void shouldWakeUpAfterThreeSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, THREE_SECONDS);
        assertEquals("Normal", p.getEffectName());
    }

    /** Inconsciente es lanzado al aire. */
    @Test
    public void shouldLaunchPlayerIntoTheAir()
    {
        Player p = game.getPlayer(Side.RIGHT);
        int y = p.getBounds().y;
        p.applyEffect(shot.createEffect());
        tick(p, FEW_CYCLES);
        assertTrue(p.getBounds().y < y);
    }

    /** Inconsciente no puede moverse. */
    @Test
    public void shouldNotMoveWhileKnockedOut()
    {
        Player p = game.getPlayer(Side.RIGHT);
        int x = p.getBounds().x;
        p.applyEffect(shot.createEffect());
        p.perform(Action.MOVE_LEFT);
        tick(p, FEW_CYCLES);
        assertEquals(x, p.getBounds().x);
    }

    /** El tornado va a ras de suelo: después de salir no sube más allá de sus saltos cortos. */
    @Test
    public void shouldStayCloseToTheFloor()
    {
        BallSprite ball = game.getMatch().getBall();
        ball.setPosition(200, 250);
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        for (int i = 0; i < THREE_SECONDS / FEW_CYCLES; i++){
            ball.updateSprite();
        }
        assertTrue(ball.getMyRectangle().y > 250);
    }
}
