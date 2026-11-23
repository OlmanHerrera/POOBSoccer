package src.test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;

/**
 * Pruebas de vida, daño, barra de poder, efectos, aturdimiento y precios de mejora.
 */
public class PlayerTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final int TEN_SECONDS = 1000;
    private static final int THREE_SECONDS = 300;
    private static final int KICK_CYCLES = 60;
    private static final int KICK_REACH = 40;
    private static final double DELTA = 0.0001;

    private Catalog catalog;
    private Field field;
    private ImagesLoader loader;
    private Player cameroon;
    private Player rival;

    /** Crea un jugador de Camerún a la izquierda y su rival a la derecha. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        field = catalog.getField("500x400");
        loader = new ImagesLoader("imsInfo.txt");
        cameroon = new Player(Side.LEFT);
        rival = new Player(Side.RIGHT);
    }

    private void raise(Player p, String stat, int level) throws POOBSoccerException{
        while (p.getLevel(stat) < level){
            p.addPoints(p.upgradePrice(stat));
            p.upgrade(stat);
        }
    }

    private void prepare() throws POOBSoccerException{
        cameroon.prepare(catalog.getCountry("Camerún"), null, field, loader, PERIOD, catalog.getStats());
        rival.prepare(catalog.getCountry("Corea del Sur"), null, field, loader, PERIOD, catalog.getStats());
    }

    private Match newMatch() throws POOBSoccerException{
        return new Match(field, catalog.getRules("Por goles"), 5, catalog.getElements(), cameroon, rival,
                         loader, UPDATES_PER_SECOND);
    }

    private void tick(Player p, int updates){
        for (int i = 0; i < updates; i++){
            p.update(UPDATES_PER_SECOND);
        }
    }

    /** Con Survival 3 la vida máxima es 120. */
    @Test
    public void shouldHaveMaxLife120WithSurvival3() throws POOBSoccerException{
        raise(rival, "Survival", 3);
        prepare();
        assertEquals(120, rival.getMaxLife(), DELTA);
    }

    /** Con Kick 5 el multiplicador de daño es 1.4. */
    @Test
    public void shouldHaveKickMultiplier1Point4WithKick5() throws POOBSoccerException{
        raise(cameroon, "Kick", 5);
        prepare();
        assertEquals(1.4, cameroon.getKickMultiplier(), DELTA);
    }

    /** Una patada con Kick 5 le quita 14 a un rival de 120 vidas. */
    @Test
    public void shouldLose14FromAKickWithKick5() throws POOBSoccerException{
        raise(cameroon, "Kick", 5);
        raise(rival, "Survival", 3);
        prepare();
        Match match = newMatch();
        rival.getSprite().setPosition(cameroon.getBounds().x + KICK_REACH, rival.getBounds().y);
        cameroon.perform(Action.KICK);
        for (int i = 0; i < KICK_CYCLES; i++){
            match.update();
        }
        assertEquals(106, rival.getLife(), DELTA);
    }

    /** El tiro de Camerún con Kick 5 le quita 84 a un rival de 120 vidas. */
    @Test
    public void shouldLose84FromCameroonPowerShotWithKick5() throws POOBSoccerException{
        raise(cameroon, "Kick", 5);
        raise(rival, "Survival", 3);
        prepare();
        rival.receivePowerShot(cameroon.getCountry().getAbility(), cameroon);
        assertEquals(36, rival.getLife(), DELTA);
    }

    /** La barra de poder se llena justo a los 10 segundos. */
    @Test
    public void shouldFillThePowerBarInTenSeconds() throws POOBSoccerException{
        prepare();
        tick(cameroon, TEN_SECONDS - 1);
        assertTrue(cameroon.getPowerCharge() < 1);
        tick(cameroon, 1);
        assertEquals(1, cameroon.getPowerCharge(), DELTA);
    }

    /** Quien recibe un gol gana un cuarto de barra. */
    @Test
    public void shouldAddAQuarterBarWhenConcedingAGoal() throws POOBSoccerException{
        prepare();
        newMatch().goal(Side.RIGHT);
        assertEquals(0.25, cameroon.getPowerCharge(), DELTA);
    }

    /** Con el aura activa no se activa otro poder aunque la barra se llene otra vez. */
    @Test
    public void shouldNotActivateASecondPowerWhileTheAuraIsActive() throws POOBSoccerException{
        prepare();
        tick(cameroon, TEN_SECONDS);
        cameroon.perform(Action.POWER);
        tick(cameroon, TEN_SECONDS);
        cameroon.perform(Action.POWER);
        assertTrue(cameroon.isAuraActive());
        assertEquals(1, cameroon.getPowerCharge(), DELTA);
    }

    /** Un efecto nuevo reemplaza al anterior. */
    @Test
    public void shouldReplaceThePreviousEffect() throws POOBSoccerException{
        prepare();
        rival.applyEffect(new ElectrocutedState());
        rival.applyEffect(new ConfusedState());
        assertEquals("Confusión", rival.getEffectName());
    }

    /** Con la vida en cero queda aturdido 3 segundos y luego vuelve con la vida máxima. */
    @Test
    public void shouldBeStunnedThreeSecondsAndRecoverMaxLife() throws POOBSoccerException{
        prepare();
        rival.takeDamage(500);
        assertEquals(0, rival.getLife(), DELTA);
        tick(rival, THREE_SECONDS - 1);
        assertEquals("Aturdido", rival.getEffectName());
        tick(rival, 1);
        assertEquals("Normal", rival.getEffectName());
        assertEquals(100, rival.getLife(), DELTA);
    }

    /** El precio de cada mejora se duplica: 500, 1000, 2000. */
    @Test
    public void shouldDoubleTheUpgradePrices() throws POOBSoccerException{
        assertEquals(500, cameroon.upgradePrice("Kick"));
        raise(cameroon, "Kick", 2);
        assertEquals(1000, cameroon.upgradePrice("Kick"));
        raise(cameroon, "Kick", 3);
        assertEquals(2000, cameroon.upgradePrice("Kick"));
    }
}
