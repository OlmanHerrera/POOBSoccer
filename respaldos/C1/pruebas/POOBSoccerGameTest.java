package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas de la fachada: validación de la configuración, modalidad
 * contra la máquina y progreso entre partidos.
 */
public class POOBSoccerGameTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int TEN_SECONDS = 1000;
    private POOBSoccerGame game;

    /** Juego sin partido. */
    @BeforeEach
    public void setUp()
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
    }

    /** Cero minutos o goles no es un límite válido. */
    @Test
    public void shouldRejectALimitOfZero()
    {
        try{
            game.startMatch("Nigeria", "Camerún", null, "Por goles", 0, "500x400");
            fail("Debió rechazar el límite 0");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("0"));
        }
    }

    /** Una resolución que no existe no se acepta. */
    @Test
    public void shouldRejectAnUnknownResolution()
    {
        try{
            game.startMatch("Nigeria", "Camerún", null, "Por goles", 3, "640x480");
            fail("Debió rechazar la resolución 640x480");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("640x480"));
        }
    }

    /** Contra la máquina, el jugador 2 es la máquina y el 1 no. */
    @Test
    public void shouldPutTheMachineOnTheRight() throws POOBSoccerException
    {
        game.startMatch("Nigeria", "Camerún", "Defensiva", "Por goles", 3, "500x400");
        assertTrue(game.isMachine(Side.RIGHT));
        assertFalse(game.isMachine(Side.LEFT));
    }

    /** El teclado no mueve a la máquina: el perfil defensivo nunca usa el poder. */
    @Test
    public void shouldIgnoreTheKeyboardForTheMachine() throws POOBSoccerException
    {
        game.startMatch("Nigeria", "Camerún", "Defensiva", "Por goles", 3, "500x400");
        for (int i = 0; i < TEN_SECONDS; i++){
            game.update();
        }
        game.perform(Side.RIGHT, Action.POWER);
        assertFalse(game.isAuraActive(Side.RIGHT));
    }

    /** Los puntos se conservan para el siguiente partido. */
    @Test
    public void shouldKeepPointsForTheNextMatch() throws POOBSoccerException
    {
        game.startMatch("Nigeria", "Camerún", null, "Por goles", 1, "500x400");
        game.getMatch().goal(Side.LEFT);
        game.update();
        game.startMatch("Nigeria", "Camerún", null, "Por goles", 1, "500x400");
        assertEquals(100, game.getPoints(Side.LEFT));
        assertEquals(0, game.getGoals(Side.LEFT));
    }
}
