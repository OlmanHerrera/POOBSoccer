package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas de la Ilusión de Estados Unidos: daño 30, 8 balones iguales (7 falsos)
 * que desaparecen al terminar la Confusión de 2.5 s.
 */
public class DeceptiveShotTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int UPDATES_PER_SECOND = 100;
    private static final int CONFUSION_CYCLES = 250;
    private POOBSoccerGame game;
    private DeceptiveShot shot;
    private BallSprite ball;

    /** Partido con el balón listo para recibir el tiro. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Estados Unidos", "Corea del Sur", null, "Por goles", 5, "500x400");
        shot = new DeceptiveShot();
        ball = game.getMatch().getBall();
    }

    private void tick(Player p, int updates)
    {
        for (int i = 0; i < updates; i++){
            p.update(UPDATES_PER_SECOND);
        }
    }

    private void moveBall(int updates)
    {
        for (int i = 0; i < updates; i++){
            ball.updateSprite();
        }
    }

    /** Su daño base es 30. */
    @Test
    public void shouldHaveBaseDamage30()
    {
        assertEquals(30, shot.getBaseDamage());
    }

    /** Su efecto es Confusión. */
    @Test
    public void shouldConfuse()
    {
        assertEquals("Confusión", shot.createEffect().getName());
    }

    /** La Confusión sigue un ciclo antes de 2.5 s. */
    @Test
    public void shouldConfuseUntilTwoAndAHalfSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, CONFUSION_CYCLES - 1);
        assertEquals("Confusión", p.getEffectName());
    }

    /** La Confusión termina a los 2.5 s. */
    @Test
    public void shouldStopConfusingAfterTwoAndAHalfSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, CONFUSION_CYCLES);
        assertEquals("Normal", p.getEffectName());
    }

    /** Al salir el tiro aparecen 7 balones falsos. */
    @Test
    public void shouldCreateSevenFakeBalls()
    {
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        assertEquals(7, ball.getDecoyCount());
    }

    /** Los balones falsos siguen un ciclo antes de 2.5 s. */
    @Test
    public void shouldKeepFakeBallsUntilTwoAndAHalfSeconds()
    {
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        moveBall(CONFUSION_CYCLES - 1);
        assertEquals(7, ball.getDecoyCount());
    }

    /** Los balones falsos desaparecen a los 2.5 s. */
    @Test
    public void shouldRemoveFakeBallsAfterTwoAndAHalfSeconds()
    {
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        moveBall(CONFUSION_CYCLES);
        assertEquals(0, ball.getDecoyCount());
    }
}
