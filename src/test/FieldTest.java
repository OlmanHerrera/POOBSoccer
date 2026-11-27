package src.test;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;

/**
 * Pruebas de la geometría de la cancha en cada resolución.
 */
public class FieldTest{

    /** A 500x400 la cancha tiene las medidas de referencia del juego. */
    @Test
    public void shouldKeepTheReferenceGeometryAt500x400(){
        Field field = new Field("500x400", 500, 400);
        assertEquals(46, field.getGoalLineX(Side.LEFT));
        assertEquals(454, field.getGoalLineX(Side.RIGHT));
        assertEquals(221, field.getCrossbarY());
        assertEquals(341, field.getFloorY());
    }

    /** A 750x600 todas las medidas crecen en la misma proporción. */
    @Test
    public void shouldScaleTheGeometryAt750x600(){
        Field field = new Field("750x600", 750, 600);
        assertEquals(69, field.getGoalLineX(Side.LEFT));
        assertEquals(681, field.getGoalLineX(Side.RIGHT));
        assertEquals(331, field.getCrossbarY());
        assertEquals(511, field.getFloorY());
    }

    /** Cada jugador arranca en su mitad de la cancha. */
    @Test
    public void shouldStartEachPlayerOnItsHalf(){
        Field field = new Field("625x500", 625, 500);
        assertEquals(187, field.getStartX(Side.LEFT));
        assertEquals(437, field.getStartX(Side.RIGHT));
        assertTrue(field.getStartX(Side.LEFT) < field.getWidth() / 2);
        assertTrue(field.getStartX(Side.RIGHT) > field.getWidth() / 2);
    }

    /** El travesaño queda por encima del suelo y los arcos crecen con la cancha. */
    @Test
    public void shouldPutTheCrossbarAboveTheFloor(){
        Field small = new Field("500x400", 500, 400);
        Field big = new Field("750x600", 750, 600);
        assertTrue(small.getCrossbarY() < small.getFloorY());
        assertEquals(51, small.getGoalWidth());
        assertEquals(76, big.getGoalWidth());
    }
}