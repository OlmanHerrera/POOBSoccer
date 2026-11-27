package src.test;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;
import java.util.ArrayList;

/**
 * Pruebas de las estadísticas y sus niveles.
 */
public class StatTest{
    private static final int PERIOD = 10;
    private static final int MANY_POINTS = 100000;
    private static final double DELTA = 0.0001;

    private Catalog catalog;
    private Player player;

    /** Un jugador sin partido preparado todavía. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        player = new Player(Side.LEFT);
    }

    /** Cada nivel por encima de 1 suma un 10 % al multiplicador. */
    @Test
    public void shouldGrowTenPercentPerLevel(){
        Stat kick = new KickStat();
        assertEquals(1.0, kick.multiplier(1), DELTA);
        assertEquals(1.4, kick.multiplier(5), DELTA);
        assertEquals(1.9, kick.multiplier(10), DELTA);
    }

    /** Las cuatro estadísticas del enunciado están registradas. */
    @Test
    public void shouldOfferTheFourStats(){
        ArrayList<String> names = catalog.getStatNames();
        assertEquals(4, names.size());
        assertTrue(names.contains("Speed"));
        assertTrue(names.contains("Kick"));
        assertTrue(names.contains("Jump"));
        assertTrue(names.contains("Survival"));
    }

    /** Con Survival en 10 la vida máxima es 190. */
    @Test
    public void shouldGive190LifeWithSurvival10() throws POOBSoccerException{
        player.setLevel("Survival", 10);
        Field field = catalog.getField("500x400");
        ImagesLoader loader = new ImagesLoader("imsInfo.txt");
        player.prepare(catalog.getCountry("Nigeria"), null, field, loader, PERIOD, catalog.getStats());
        assertEquals(190, player.getMaxLife(), DELTA);
    }

    /** Una estadística en nivel 10 ya no se puede mejorar. */
    @Test
    public void shouldNotUpgradePastLevelTen() throws POOBSoccerException{
        player.setLevel("Kick", 10);
        player.addPoints(MANY_POINTS);
        assertTrue(player.isMaxLevel("Kick"));
        try{
            player.upgrade("Kick");
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertEquals(10, player.getLevel("Kick"));
        }
    }
}

