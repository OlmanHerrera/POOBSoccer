package src.test;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;



/**
 * Pruebas de la fachada del juego: menús, inicio de partido y mejoras.
 */
public class POOBSoccerGameTest{
    private static final long PERIOD = 10000000L;

    private POOBSoccerGame game;

    /** Crea el juego con un bucle de 100 cuadros por segundo. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        game = new POOBSoccerGame(PERIOD);
    }

    /** Los menús se llenan con lo que hay en el catálogo. */
    @Test
    public void shouldOfferWhatTheCatalogHas(){
        assertEquals(4, game.getCountryIds().size());
        assertEquals(4, game.getProfileIds().size());
        assertEquals(2, game.getRuleIds().size());
        assertEquals(3, game.getFieldIds().size());
    }

    /** Con un límite de 0 el partido no empieza. */
    @Test
    public void shouldNotStartWithALimitOfZero(){
        try{
            game.startMatch("Corea del Sur", "Nigeria", null, null, "Por goles", 0, "500x400");
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertFalse(game.hasMatch());
        }
    }

    /** Con un país que no existe el partido no empieza. */
    @Test
    public void shouldNotStartWithAnUnknownCountry(){
        try{
            game.startMatch("Japón", "Nigeria", null, null, "Por goles", 3, "500x400");
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertEquals(POOBSoccerException.UNKNOWN_COUNTRY + "Japón", e.getMessage());
            assertFalse(game.hasMatch());
        }
    }

    /** Contra la máquina, el jugador de la derecha es la máquina. */
    @Test
    public void shouldPutTheMachineOnTheRight() throws POOBSoccerException{
        game.startMatch("Corea del Sur", "Nigeria", null, "Defensiva", "Por goles", 3, "500x400");
        assertTrue(game.hasMatch());
        assertFalse(game.isMachine(Side.LEFT));
        assertTrue(game.isMachine(Side.RIGHT));
    }

    /** Sin puntos no se puede comprar una mejora. */
    @Test
    public void shouldNotUpgradeWithoutPoints(){
        try{
            game.upgrade(Side.LEFT, "Kick");
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertEquals(1, game.getStatLevel(Side.LEFT, "Kick"));
        }
    }
}
