package src.test;

import src.domain.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Pruebas de los tipos de partido: por goles y por tiempo.
 */
public class RulesTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;

    private Catalog catalog;
    private Field field;
    private ImagesLoader loader;
    private Player left;
    private Player right;

    /** Prepara un jugador de Corea y uno de Camerún. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        field = catalog.getField("500x400");
        loader = new ImagesLoader("imsInfo.txt");
        left = new Player(Side.LEFT);
        right = new Player(Side.RIGHT);
        left.prepare(catalog.getCountry("Corea del Sur"), null, field, loader, PERIOD, catalog.getStats());
        right.prepare(catalog.getCountry("Camerún"), null, field, loader, PERIOD, catalog.getStats());
    }

    private Match newMatch(String rules, int limit) throws POOBSoccerException{
        return new Match(field, catalog.getRules(rules), limit, catalog.getElements(), left, right,
                         loader, UPDATES_PER_SECOND);
    }

    /** Cada tipo de partido dice cómo se llama su límite y cuál propone. */
    @Test
    public void shouldNameTheLimitOfEachRules() throws POOBSoccerException{
        MatchRules timed = catalog.getRules("Por tiempo");
        MatchRules goals = catalog.getRules("Por goles");
        assertEquals("Minutos", timed.getLimitName());
        assertEquals(3, timed.getDefaultLimit());
        assertEquals("Goles", goals.getLimitName());
        assertEquals(5, goals.getDefaultLimit());
    }

    /** Por goles, el partido termina justo cuando alguien llega al límite. */
    @Test
    public void shouldEndAGoalsMatchAtTheLimit() throws POOBSoccerException{
        Match match = newMatch("Por goles", 2);
        match.goal(Side.RIGHT);
        match.update();
        assertFalse(match.isOver());
        match.goal(Side.RIGHT);
        match.update();
        assertTrue(match.isOver());
        assertEquals(Side.RIGHT, match.getWinner());
    }

    /** Por goles, el estado muestra la meta y el tiempo jugado. */
    @Test
    public void shouldShowTheGoalTargetInTheStatus() throws POOBSoccerException{
        Match match = newMatch("Por goles", 3);
        assertEquals("Meta de goles: 3 - 00:00", match.getStatusText());
    }

    /** Por tiempo, el estado muestra el tiempo que falta. */
    @Test
    public void shouldCountDownATimedMatch() throws POOBSoccerException{
        Match match = newMatch("Por tiempo", 1);
        assertEquals("01:00", match.getStatusText());
        for (int i = 0; i < UPDATES_PER_SECOND; i++){
            match.update();
        }
        assertEquals("00:59", match.getStatusText());
    }
}
