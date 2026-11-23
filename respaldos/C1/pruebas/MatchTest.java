package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del partido: cómo empieza, qué pasa después de un gol, puntos,
 * pausa y terminar antes de tiempo.
 */
public class MatchTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int FIVE_SECONDS = 500;
    private static final double DELTA = 0.0001;
    private POOBSoccerGame game;
    private Match match;

    /** Partido por goles a 2 entre Nigeria y Estados Unidos. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Nigeria", "Estados Unidos", null, "Por goles", 2, "500x400");
        match = game.getMatch();
    }

    private void winRight()
    {
        match.goal(Side.RIGHT);
        match.goal(Side.RIGHT);
        match.update();
    }

    /** El partido empieza 0 - 0. */
    @Test
    public void shouldStartZeroZero()
    {
        assertEquals(0, game.getGoals(Side.LEFT));
        assertEquals(0, game.getGoals(Side.RIGHT));
    }

    /** El partido empieza con las barras de poder vacías. */
    @Test
    public void shouldStartWithEmptyPowerBars()
    {
        assertEquals(0, game.getPowerCharge(Side.LEFT), DELTA);
        assertEquals(0, game.getPowerCharge(Side.RIGHT), DELTA);
    }

    /** El partido empieza con la vida de cada jugador en su máximo. */
    @Test
    public void shouldStartWithFullLife()
    {
        assertEquals(game.getMaxLife(Side.LEFT), game.getLife(Side.LEFT), DELTA);
        assertEquals(game.getMaxLife(Side.RIGHT), game.getLife(Side.RIGHT), DELTA);
    }

    /** Después de un gol los jugadores vuelven a su posición. */
    @Test
    public void shouldReturnPlayersToStartAfterGoal()
    {
        Player left = game.getPlayer(Side.LEFT);
        left.getSprite().setPosition(300, left.getBounds().y);
        match.goal(Side.RIGHT);
        assertEquals(125, left.getBounds().x);
    }

    /** Después de un gol el balón cae desde el centro de la cancha. */
    @Test
    public void shouldDropBallFromCenterAfterGoal()
    {
        match.getBall().setPosition(100, 300);
        match.goal(Side.RIGHT);
        assertEquals(250, match.getBallBounds().x);
        assertEquals(0, match.getBallBounds().y);
    }

    /** Quien recibe el gol gana un cuarto de barra. */
    @Test
    public void shouldAddQuarterBarToPlayerWhoConcedes()
    {
        match.goal(Side.RIGHT);
        assertEquals(0.25, game.getPowerCharge(Side.LEFT), DELTA);
    }

    /** El ganador recibe 100 puntos. */
    @Test
    public void shouldGive100PointsToWinner()
    {
        winRight();
        assertEquals(100, game.getPoints(Side.RIGHT));
    }

    /** El perdedor recibe 25 puntos. */
    @Test
    public void shouldGive25PointsToLoser()
    {
        winRight();
        assertEquals(25, game.getPoints(Side.LEFT));
    }

    /** Si los jugadores terminan el partido antes, no hay ganador ni puntos. */
    @Test
    public void shouldGiveNoPointsWhenAbandoned()
    {
        game.endMatch();
        assertNull(game.getWinner());
        assertEquals(0, game.getPoints(Side.LEFT));
        assertEquals(0, game.getPoints(Side.RIGHT));
    }

    /** En pausa el reloj no avanza. */
    @Test
    public void shouldNotAdvanceClockWhilePaused()
    {
        game.pause();
        for (int i = 0; i < FIVE_SECONDS; i++){
            game.update();
        }
        assertEquals(0, match.getElapsedSeconds());
    }
}
