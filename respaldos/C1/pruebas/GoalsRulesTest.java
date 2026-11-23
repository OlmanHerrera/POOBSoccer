package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del partido por goles: gana el primero que llega al número de goles.
 */
public class GoalsRulesTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private POOBSoccerGame game;
    private Match match;

    /** Partido por goles a 2. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Corea del Sur", "Camerún", null, "Por goles", 2, "500x400");
        match = game.getMatch();
    }

    /** Con un gol de dos el partido sigue. */
    @Test
    public void shouldNotEndBeforeReachingTheGoals()
    {
        match.goal(Side.LEFT);
        match.update();
        assertFalse(game.isMatchOver());
    }

    /** Al llegar a los goles del límite el partido termina. */
    @Test
    public void shouldEndWhenAPlayerReachesTheGoals()
    {
        match.goal(Side.LEFT);
        match.goal(Side.LEFT);
        match.update();
        assertTrue(game.isMatchOver());
    }

    /** Gana el que llegó a los goles. */
    @Test
    public void shouldBeWonByWhoReachedTheGoals()
    {
        match.goal(Side.LEFT);
        match.goal(Side.LEFT);
        match.update();
        assertEquals(Side.LEFT, game.getWinner());
    }

    /** El texto del tiempo muestra la meta de goles. */
    @Test
    public void shouldShowTheGoalTarget()
    {
        assertTrue(game.getTimeText().startsWith("Meta de goles: 2"));
    }
}
