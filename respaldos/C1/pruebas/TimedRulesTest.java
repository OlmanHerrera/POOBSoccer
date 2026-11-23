package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del partido por tiempo y de la muerte súbita. Un minuto son 6000 ciclos.
 */
public class TimedRulesTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int ONE_MINUTE = 6000;
    private POOBSoccerGame game;
    private Match match;

    /** Partido por tiempo de 1 minuto. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Corea del Sur", "Camerún", null, "Por tiempo", 1, "500x400");
        match = game.getMatch();
    }

    private void play(int updates)
    {
        for (int i = 0; i < updates; i++){
            game.update();
        }
    }

    /** Al empezar muestra el minuto completo. */
    @Test
    public void shouldShowTheFullMinuteAtStart()
    {
        assertEquals("01:00", game.getTimeText());
    }

    /** Un ciclo antes del minuto el partido sigue. */
    @Test
    public void shouldNotEndBeforeTheTime()
    {
        match.goal(Side.LEFT);
        play(ONE_MINUTE - 1);
        assertFalse(game.isMatchOver());
    }

    /** Al cumplirse el tiempo gana el que tiene más goles. */
    @Test
    public void shouldEndWithTheLeaderWhenTimeIsUp()
    {
        match.goal(Side.LEFT);
        play(ONE_MINUTE);
        assertTrue(game.isMatchOver());
        assertEquals(Side.LEFT, game.getWinner());
    }

    /** Con empate al cumplirse el tiempo el partido sigue en muerte súbita. */
    @Test
    public void shouldGoToSuddenDeathWhenTied()
    {
        play(ONE_MINUTE);
        assertFalse(game.isMatchOver());
        assertEquals("Muerte súbita", game.getTimeText());
    }

    /** En muerte súbita gana el primero que marque. */
    @Test
    public void shouldEndSuddenDeathWithTheNextGoal()
    {
        play(ONE_MINUTE);
        match.goal(Side.RIGHT);
        match.update();
        assertTrue(game.isMatchOver());
        assertEquals(Side.RIGHT, game.getWinner());
    }
}
