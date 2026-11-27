package src.test;


import src.domain.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

 
/**
 * Pruebas de los cuatro perfiles de máquina al comenzar un partido.
 */

public class ProfileTest
{

    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final int TEN_SECONDS = 1000;

    private Catalog catalog;
    private Player left;
    private Player right;
    private Match match;

    /** Partido por goles con los dos jugadores en su posición inicial y el balón arriba en el centro. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        Field field = catalog.getField("500x400");
        ImagesLoader loader = new ImagesLoader("imsInfo.txt");
        left = new Player(Side.LEFT);
        right = new Player(Side.RIGHT);
        left.prepare(catalog.getCountry("Nigeria"), null, field, loader, PERIOD, catalog.getStats());
        right.prepare(catalog.getCountry("Estados Unidos"), null, field, loader, PERIOD, catalog.getStats());
        match = new Match(field, catalog.getRules("Por goles"), 5, catalog.getElements(), left, right,
                          loader, UPDATES_PER_SECOND);
    }

    /** El perfil torpe empieza caminando a la izquierda hasta su primer cambio al azar. */
    @Test
    public void shouldStartMovingLeftWhenClumsy(){
        assertEquals(Action.MOVE_LEFT, new ClumsyProfile().decide(left, match));
    }

    /** El perfil ofensivo de la izquierda camina hacia el balón para quedar detrás de él. */
    @Test
    public void shouldGoBehindTheBallWhenOffensive(){
        assertEquals(Action.MOVE_RIGHT, new OffensiveProfile().decide(left, match));
    }

    /** El perfil defensivo de la derecha se acerca a quedar entre el rival y su arco. */
    @Test
    public void shouldStandBetweenRivalAndGoalWhenDefensive(){
        assertEquals(Action.MOVE_LEFT, new DefensiveProfile().decide(right, match));
    }

    /** El perfil experto activa el poder apenas tiene la barra llena. */
    @Test
    public void shouldActivateThePowerWhenExpertHasAFullBar(){
        for (int i = 0; i < TEN_SECONDS; i++){
            left.update(UPDATES_PER_SECOND);
        }
        assertEquals(Action.POWER, new ExpertProfile().decide(left, match));
    }

    /** Los perfiles se ofrecen con el nombre que ve el jugador. */
    @Test
    public void shouldNameTheFourProfiles(){
        assertEquals("Torpe", new ClumsyProfile().getId());
        assertEquals("Ofensiva", new OffensiveProfile().getId());
        assertEquals("Defensiva", new DefensiveProfile().getId());
        assertEquals("Experta", new ExpertProfile().getId());
    }

}