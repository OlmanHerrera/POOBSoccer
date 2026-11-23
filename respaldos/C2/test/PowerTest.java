package src.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;

/**
 * Pruebas de los cuatro tiros de poder: daño, efecto y duración del efecto.
 */
public class PowerTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final double FULL_LIFE = 100;
    private static final double DELTA = 0.0001;
    private Player shooter;
    private Player target;

    /** Prepara dos jugadores con todas sus estadísticas en 1. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        Catalog catalog = new Catalog();
        Field field = catalog.getField("500x400");
        ImagesLoader loader = new ImagesLoader("imsInfo.txt");
        shooter = new Player(Side.LEFT);
        target = new Player(Side.RIGHT);
        shooter.prepare(catalog.getCountry("Corea del Sur"), null, field, loader, PERIOD, catalog.getStats());
        target.prepare(catalog.getCountry("Nigeria"), null, field, loader, PERIOD, catalog.getStats());
    }

    private void tick(int updates){
        for (int i = 0; i < updates; i++){
            target.update(UPDATES_PER_SECOND);
        }
    }

    private void checkPower(AbilityStrategy power, int damage, String effect, int effectCycles){
        assertEquals(damage, power.getBaseDamage());
        target.receivePowerShot(power, shooter);
        assertEquals(FULL_LIFE - damage, target.getLife(), DELTA);
        assertEquals(effect, target.getEffectName());
        tick(effectCycles - 1);
        assertEquals(effect, target.getEffectName());
        tick(1);
        assertEquals("Normal", target.getEffectName());
    }

    /** Aura Azul: 40 de daño y Empuje durante 1.5 s. */
    @Test
    public void shouldDo40AndPushForOneAndAHalfSeconds(){
        checkPower(new BlueAuraShot(), 40, "Empuje", 150);
    }

    /** Rayo: 60 de daño y Electrocutado durante 3 s. */
    @Test
    public void shouldDo60AndElectrocuteForThreeSeconds(){
        checkPower(new LightningShot(), 60, "Electrocutado", 300);
    }

    /** Tornado: 40 de daño e Inconsciente durante 3 s. */
    @Test
    public void shouldDo40AndKnockOutForThreeSeconds(){
        checkPower(new TornadoShot(), 40, "Inconsciente", 300);
    }

    /** Ilusión: 30 de daño y Confusión durante 2.5 s. */
    @Test
    public void shouldDo30AndConfuseForTwoAndAHalfSeconds(){
        checkPower(new DeceptiveShot(), 30, "Confusión", 250);
    }
}
