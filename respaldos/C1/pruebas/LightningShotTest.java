package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del Rayo de Camerún: daño 60, Electrocutado durante 3 s
 * (no puede saltar y se mueve al 50 % de su velocidad).
 */
public class LightningShotTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int UPDATES_PER_SECOND = 100;
    private static final int THREE_SECONDS = 300;
    private static final int ONE_SECOND = 100;
    private static final int FEW_CYCLES = 10;
    private POOBSoccerGame game;
    private LightningShot shot;

    /** Partido con un jugador de cada lado. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Camerún", "Nigeria", null, "Por goles", 5, "500x400");
        shot = new LightningShot();
    }

    private void tick(Player p, int updates)
    {
        for (int i = 0; i < updates; i++){
            p.update(UPDATES_PER_SECOND);
        }
    }

    /** Su daño base es 60. */
    @Test
    public void shouldHaveBaseDamage60()
    {
        assertEquals(60, shot.getBaseDamage());
    }

    /** Su efecto es Electrocutado. */
    @Test
    public void shouldElectrocute()
    {
        assertEquals("Electrocutado", shot.createEffect().getName());
    }

    /** Electrocutado sigue un ciclo antes de 3 s. */
    @Test
    public void shouldElectrocuteUntilThreeSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, THREE_SECONDS - 1);
        assertEquals("Electrocutado", p.getEffectName());
    }

    /** Electrocutado termina a los 3 s. */
    @Test
    public void shouldStopElectrocutingAfterThreeSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(shot.createEffect());
        tick(p, THREE_SECONDS);
        assertEquals("Normal", p.getEffectName());
    }

    /** Electrocutado no puede saltar. */
    @Test
    public void shouldNotJumpWhileElectrocuted()
    {
        Player p = game.getPlayer(Side.RIGHT);
        int y = p.getBounds().y;
        p.applyEffect(shot.createEffect());
        p.perform(Action.JUMP);
        tick(p, FEW_CYCLES);
        assertEquals(y, p.getBounds().y);
    }

    /** Electrocutado camina a la mitad: 50 px en un segundo en vez de 100. */
    @Test
    public void shouldMoveAtHalfSpeedWhileElectrocuted()
    {
        Player p = game.getPlayer(Side.RIGHT);
        int x = p.getBounds().x;
        p.applyEffect(shot.createEffect());
        p.perform(Action.MOVE_LEFT);
        tick(p, ONE_SECOND);
        assertEquals(x - 50, p.getBounds().x);
    }
}
