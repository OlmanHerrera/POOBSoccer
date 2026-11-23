package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la geometría de la cancha y de cómo escala con la resolución.
 */
public class FieldTest
{
    /** A 500x400 da las medidas de referencia. */
    @Test
    public void shouldKeepReferenceMeasuresAt500x400()
    {
        Field f = new Field("500x400", 500, 400);
        assertEquals(341, f.getFloorY());
        assertEquals(221, f.getCrossbarY());
        assertEquals(65, f.getGoalLineX(Side.LEFT));
        assertEquals(435, f.getGoalLineX(Side.RIGHT));
        assertEquals(125, f.getStartX(Side.LEFT));
        assertEquals(375, f.getStartX(Side.RIGHT));
    }

    /** A 750x600 todo crece una vez y media. */
    @Test
    public void shouldScaleAt750x600()
    {
        Field f = new Field("750x600", 750, 600);
        assertEquals(511, f.getFloorY());
        assertEquals(331, f.getCrossbarY());
        assertEquals(97, f.getGoalLineX(Side.LEFT));
        assertEquals(652, f.getGoalLineX(Side.RIGHT));
    }
}
