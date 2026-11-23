package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Pruebas del jugador: vida, daño, aturdimiento, barra de poder, aura,
 * efectos y mejoras. Los tiempos se cuentan en ciclos (100 por segundo).
 */
public class PlayerTest
{
    private static final long PERIOD = 10000000L;
    private static final File PROGRESS = new File(System.getProperty("java.io.tmpdir"), "poobsoccer_prueba_progreso.txt");
    private static final int UPDATES_PER_SECOND = 100;
    private static final int TEN_SECONDS = 1000;
    private static final int THREE_SECONDS = 300;
    private static final int KICK_CYCLES = 60;
    private static final int KICK_REACH = 40;
    private static final int MANY_POINTS = 1000000;
    private static final double DELTA = 0.0001;
    private POOBSoccerGame game;

    /** Partido de Camerún (izquierda) contra Corea del Sur (derecha). */
    @BeforeEach
    public void setUp() throws POOBSoccerException
    {
        game = new POOBSoccerGame(PERIOD, PROGRESS);
        restart();
    }

    private void restart() throws POOBSoccerException
    {
        game.startMatch("Camerún", "Corea del Sur", null, "Por goles", 5, "500x400");
    }

    private void raise(Side s, String stat, int level) throws POOBSoccerException
    {
        game.getPlayer(s).addPoints(MANY_POINTS);
        while (game.getStatLevel(s, stat) < level){
            game.upgrade(s, stat);
        }
    }

    private void tick(Player p, int updates)
    {
        for (int i = 0; i < updates; i++){
            p.update(UPDATES_PER_SECOND);
        }
    }

    /** Con Survival 1 la vida máxima es 100. */
    @Test
    public void shouldHaveMaxLife100WithSurvival1()
    {
        assertEquals(100, game.getMaxLife(Side.LEFT), DELTA);
    }

    /** Con Survival 3 la vida máxima es 120 (ejemplo del enunciado). */
    @Test
    public void shouldHaveMaxLife120WithSurvival3() throws POOBSoccerException
    {
        raise(Side.RIGHT, "Survival", 3);
        restart();
        assertEquals(120, game.getLife(Side.RIGHT), DELTA);
    }

    /** Con Kick 5 el multiplicadorKick es 1.4 (ejemplo del enunciado). */
    @Test
    public void shouldHaveKickMultiplier1Point4WithKick5() throws POOBSoccerException
    {
        raise(Side.LEFT, "Kick", 5);
        restart();
        assertEquals(1.4, game.getPlayer(Side.LEFT).getKickMultiplier(), DELTA);
    }

    /** Una patada de Kick 5 le quita 14 a un rival de 120: le quedan 106 (ejemplo del enunciado). */
    @Test
    public void shouldLoseTenTimesKickMultiplierWhenKicked() throws POOBSoccerException
    {
        raise(Side.LEFT, "Kick", 5);
        raise(Side.RIGHT, "Survival", 3);
        restart();
        Player kicker = game.getPlayer(Side.LEFT);
        Player rival = game.getPlayer(Side.RIGHT);
        rival.getSprite().setPosition(kicker.getBounds().x + KICK_REACH, rival.getBounds().y);
        game.perform(Side.LEFT, Action.KICK);
        for (int i = 0; i < KICK_CYCLES; i++){
            game.update();
        }
        assertEquals(106, rival.getLife(), DELTA);
    }

    /** El Rayo de Camerún con Kick 5 le quita 84 a un rival de 120: le quedan 36 (ejemplo del enunciado). */
    @Test
    public void shouldLosePowerDamageTimesKickMultiplier() throws POOBSoccerException
    {
        raise(Side.LEFT, "Kick", 5);
        raise(Side.RIGHT, "Survival", 3);
        restart();
        Player shooter = game.getPlayer(Side.LEFT);
        Player rival = game.getPlayer(Side.RIGHT);
        rival.receivePowerShot(shooter.getCountry().getAbility(), shooter);
        assertEquals(36, rival.getLife(), DELTA);
    }

    /** La vida nunca baja de cero. */
    @Test
    public void shouldNotLetLifeGoBelowZero()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.takeDamage(500);
        assertEquals(0, p.getLife(), DELTA);
    }

    /** Con la vida en cero el jugador queda aturdido. */
    @Test
    public void shouldBeStunnedWhenLifeReachesZero()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.takeDamage(500);
        assertEquals("Aturdido", p.getEffectName());
    }

    /** El aturdimiento sigue un ciclo antes de cumplir 3 segundos. */
    @Test
    public void shouldStayStunnedUntilThreeSeconds()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.takeDamage(500);
        tick(p, THREE_SECONDS - 1);
        assertEquals("Aturdido", p.getEffectName());
    }

    /** A los 3 segundos termina el aturdimiento y vuelve la vida máxima. */
    @Test
    public void shouldRecoverMaxLifeAfterThreeSecondsStunned()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.takeDamage(500);
        tick(p, THREE_SECONDS);
        assertEquals("Normal", p.getEffectName());
        assertEquals(100, p.getLife(), DELTA);
    }

    /** Aturdido no puede moverse. */
    @Test
    public void shouldNotMoveWhileStunned()
    {
        Player p = game.getPlayer(Side.RIGHT);
        int x = p.getBounds().x;
        p.takeDamage(500);
        p.perform(Action.MOVE_LEFT);
        tick(p, KICK_CYCLES);
        assertEquals(x, p.getBounds().x);
    }

    /** La barra no está llena un ciclo antes de los 10 segundos. */
    @Test
    public void shouldNotFillPowerBarBeforeTenSeconds()
    {
        Player p = game.getPlayer(Side.LEFT);
        tick(p, TEN_SECONDS - 1);
        assertTrue(p.getPowerCharge() < 1);
    }

    /** La barra se llena en 10 segundos. */
    @Test
    public void shouldFillPowerBarInTenSeconds()
    {
        Player p = game.getPlayer(Side.LEFT);
        tick(p, TEN_SECONDS);
        assertEquals(1, p.getPowerCharge(), DELTA);
    }

    /** Al recibir un gol la barra sube un cuarto. */
    @Test
    public void shouldAddAQuarterWhenConcedingAGoal()
    {
        Player p = game.getPlayer(Side.LEFT);
        p.concede();
        assertEquals(0.25, p.getPowerCharge(), DELTA);
    }

    /** Sin la barra llena no se activa el poder. */
    @Test
    public void shouldNotActivatePowerWithoutFullBar()
    {
        game.perform(Side.LEFT, Action.POWER);
        assertFalse(game.isAuraActive(Side.LEFT));
    }

    /** Al activar el poder aparece el aura y la barra se vacía. */
    @Test
    public void shouldEmptyBarWhenActivatingPower()
    {
        Player p = game.getPlayer(Side.LEFT);
        tick(p, TEN_SECONDS);
        p.perform(Action.POWER);
        assertTrue(p.isAuraActive());
        assertEquals(0, p.getPowerCharge(), DELTA);
    }

    /** Con el aura activa no se activa otro poder aunque la barra se vuelva a llenar. */
    @Test
    public void shouldNotActivateSecondPowerWhileAuraIsActive()
    {
        Player p = game.getPlayer(Side.LEFT);
        tick(p, TEN_SECONDS);
        p.perform(Action.POWER);
        tick(p, TEN_SECONDS);
        p.perform(Action.POWER);
        assertEquals(1, p.getPowerCharge(), DELTA);
    }

    /** Un efecto nuevo reemplaza al anterior. */
    @Test
    public void shouldReplacePreviousEffectWithNewOne()
    {
        Player p = game.getPlayer(Side.RIGHT);
        p.applyEffect(new ElectrocutedState());
        p.applyEffect(new ConfusedState());
        assertEquals("Confusión", p.getEffectName());
    }

    /** Las estadísticas empiezan en 1. */
    @Test
    public void shouldStartStatsAtLevel1()
    {
        assertEquals(1, game.getStatLevel(Side.LEFT, "Speed"));
    }

    /** Una estadística no pasa de 10. */
    @Test
    public void shouldNotRaiseStatAbove10() throws POOBSoccerException
    {
        raise(Side.LEFT, "Jump", 10);
        try{
            game.upgrade(Side.LEFT, "Jump");
            fail("Debió rechazar la mejora por encima de 10");
        }
        catch (POOBSoccerException e){
            assertEquals(10, game.getStatLevel(Side.LEFT, "Jump"));
        }
    }

    /** Los precios de mejora van 500, 1000, 2000. */
    @Test
    public void shouldDoubleUpgradePrices() throws POOBSoccerException
    {
        assertEquals(500, game.getUpgradePrice(Side.LEFT, "Kick"));
        raise(Side.LEFT, "Kick", 2);
        assertEquals(1000, game.getUpgradePrice(Side.LEFT, "Kick"));
        raise(Side.LEFT, "Kick", 3);
        assertEquals(2000, game.getUpgradePrice(Side.LEFT, "Kick"));
    }

    /** Sin puntos suficientes no se puede mejorar. */
    @Test
    public void shouldRejectUpgradeWithoutEnoughPoints()
    {
        try{
            game.upgrade(Side.LEFT, "Speed");
            fail("Debió rechazar la mejora sin puntos");
        }
        catch (POOBSoccerException e){
            assertEquals(1, game.getStatLevel(Side.LEFT, "Speed"));
        }
    }

    /** Mejorar descuenta el precio de los puntos. */
    @Test
    public void shouldSpendPointsWhenUpgrading() throws POOBSoccerException
    {
        game.getPlayer(Side.LEFT).addPoints(600);
        game.upgrade(Side.LEFT, "Speed");
        assertEquals(100, game.getPoints(Side.LEFT));
    }
}
