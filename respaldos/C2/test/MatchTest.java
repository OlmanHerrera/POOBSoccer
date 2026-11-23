package src.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;

/**
 * Pruebas del partido por goles, por tiempo, la muerte súbita y los puntos.
 */
public class MatchTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final int ONE_MINUTE = 6000;
    private Catalog catalog;
    private Field field;
    private ImagesLoader loader;
    private Player left;
    private Player right;

    /** Prepara un jugador de Nigeria y uno de Estados Unidos. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        field = catalog.getField("500x400");
        loader = new ImagesLoader("imsInfo.txt");
        left = new Player(Side.LEFT);
        right = new Player(Side.RIGHT);
        left.prepare(catalog.getCountry("Nigeria"), null, field, loader, PERIOD, catalog.getStats());
        right.prepare(catalog.getCountry("Estados Unidos"), null, field, loader, PERIOD, catalog.getStats());
    }

    private Match newMatch(String rules, int limit) throws POOBSoccerException{
        return new Match(field, catalog.getRules(rules), limit, catalog.getElements(), left, right,
                         loader, UPDATES_PER_SECOND);
    }

    private void play(Match match, int updates){
        for (int i = 0; i < updates; i++){
            match.update();
        }
    }

    /** El partido por goles termina cuando alguien llega a los goles del límite. */
    @Test
    public void shouldEndAGoalsMatchAtTheGoalLimit() throws POOBSoccerException{
        Match match = newMatch("Por goles", 2);
        match.goal(Side.LEFT);
        match.update();
        assertFalse(match.isOver());
        match.goal(Side.LEFT);
        match.update();
        assertTrue(match.isOver());
        assertEquals(Side.LEFT, match.getWinner());
    }

    /** El partido por tiempo termina al cumplirse el tiempo y gana quien tiene más goles. */
    @Test
    public void shouldEndATimedMatchWithTheLeader() throws POOBSoccerException{
        Match match = newMatch("Por tiempo", 1);
        match.goal(Side.LEFT);
        play(match, ONE_MINUTE - 1);
        assertFalse(match.isOver());
        play(match, 1);
        assertTrue(match.isOver());
        assertEquals(Side.LEFT, match.getWinner());
    }

    /** Con empate al final del tiempo sigue en muerte súbita hasta el siguiente gol. */
    @Test
    public void shouldPlaySuddenDeathWhenTied() throws POOBSoccerException{
        Match match = newMatch("Por tiempo", 1);
        play(match, ONE_MINUTE);
        assertFalse(match.isOver());
        assertEquals("Muerte súbita", match.getStatusText());
        match.goal(Side.RIGHT);
        match.update();
        assertTrue(match.isOver());
        assertEquals(Side.RIGHT, match.getWinner());
    }

    /** El ganador recibe 100 puntos y el perdedor 25. */
    @Test
    public void shouldGive100PointsToTheWinnerAnd25ToTheLoser() throws POOBSoccerException{
        Match match = newMatch("Por goles", 1);
        match.goal(Side.RIGHT);
        match.update();
        assertEquals(100, right.getPoints());
        assertEquals(25, left.getPoints());
    }
}
