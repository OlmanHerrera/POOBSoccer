package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del multiplicador de las estadísticas: 1 + 0.10 x (nivel - 1).
 */
public class StatTest
{
    private static final double DELTA = 0.0001;

    /** En el nivel 1 el multiplicador es 1. */
    @Test
    public void shouldBeOneAtLevel1()
    {
        assertEquals(1.0, new SpeedStat().multiplier(1), DELTA);
    }

    /** En el nivel 5 el multiplicador es 1.4. */
    @Test
    public void shouldBe1Point4AtLevel5()
    {
        assertEquals(1.4, new KickStat().multiplier(5), DELTA);
    }

    /** En el nivel 10 el multiplicador es 1.9. */
    @Test
    public void shouldBe1Point9AtLevel10()
    {
        assertEquals(1.9, new SurvivalStat().multiplier(10), DELTA);
    }
}
