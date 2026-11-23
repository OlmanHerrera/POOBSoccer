package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del balón con tiro de poder.
 */
public class BallSpriteTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int FEW_CYCLES = 10;
    private POOBSoccerGame game;
    private BallSprite ball;

    /** Partido con su balón. */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        game.startMatch("Corea del Sur", "Camerún", null, "Por goles", 5, "500x400");
        ball = game.getMatch().getBall();
    }

    /** Un tiro de poder que entra al arco sin tocar al rival pierde el poder: no hay daño ni efecto. */
    @Test
    public void shouldLosePowerWhenTheGoalSendsItToTheCenter()
    {
        ball.charge(new BlueAuraShot(), Side.LEFT);
        game.getMatch().goal(Side.LEFT);
        assertNull(ball.getPower());
    }

    /** Un tiro de poder que llega a un borde lateral pierde el poder. */
    @Test
    public void shouldLosePowerAtTheSideWall()
    {
        BlueAuraShot shot = new BlueAuraShot();
        ball.setPosition(440, 150);
        ball.charge(shot, Side.LEFT);
        shot.launch(ball);
        for (int i = 0; i < FEW_CYCLES; i++){
            ball.updateSprite();
        }
        assertNull(ball.getPower());
    }

    /** El poder va hacia el arco del rival de quien disparó. */
    @Test
    public void shouldGoTowardsTheRivalGoal()
    {
        ball.charge(new BlueAuraShot(), Side.RIGHT);
        assertEquals(-1, ball.getPowerDirection());
    }
}
