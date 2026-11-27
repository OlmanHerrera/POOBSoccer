package src.test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.PrintWriter;
import src.domain.*;

/**
 * Pruebas de guardar y abrir un partido y de exportar e importar el progreso.
 */
public class GameFileTest{
    private static final int PERIOD = 10;
    private static final int UPDATES_PER_SECOND = 100;
    private static final int TEN_SECONDS = 1000;
    private static final int HALF_SECOND = 50;
    private static final int FIVE_SECONDS = 500;
    private static final double DELTA = 0.0001;
    private static final String FOLDER = System.getProperty("java.io.tmpdir");

    private Catalog catalog;
    private ImagesLoader loader;
    private Player left;
    private Player right;
    private Match match;
    private GameFile gameFile;
    private File matchFile;
    private File progressFile;

    /** Partido por tiempo contra la máquina defensiva. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
        Field field = catalog.getField("500x400");
        loader = new ImagesLoader("imsInfo.txt");
        left = new Player(Side.LEFT);
        right = new Player(Side.RIGHT);
        left.prepare(catalog.getCountry("Corea del Sur"), null, field, loader, PERIOD, catalog.getStats());
        right.prepare(catalog.getCountry("Nigeria"), catalog.getProfile("Defensiva"), field, loader, PERIOD,
                      catalog.getStats());
        match = new Match(field, catalog.getRules("Por tiempo"), 2, catalog.getElements(), left, right,
                          loader, UPDATES_PER_SECOND);
        gameFile = new GameFile();
        matchFile = new File(FOLDER, "poobsoccer_prueba.dat");
        progressFile = new File(FOLDER, "poobsoccer_prueba.txt");
    }

    /** Borra los archivos de la prueba. */
    @AfterEach
    public void tearDown(){
        matchFile.delete();
        progressFile.delete();
    }

    private void play(Match m, int updates){
        for (int i = 0; i < updates; i++){
            m.update();
        }
    }

    /** Un partido guardado a mitad y abierto otra vez continúa igual que el original. */
    @Test
    public void shouldContinueTheSameAfterSavingAndOpening() throws POOBSoccerException{
        match.goal(Side.LEFT);
        left.perform(Action.MOVE_RIGHT);
        play(match, TEN_SECONDS);
        left.perform(Action.POWER);
        right.applyEffect(new ElectrocutedState());
        play(match, HALF_SECOND);
        match.prepareToSave();
        gameFile.saveMatch(match, matchFile);
        Match opened = gameFile.openMatch(matchFile);
        opened.restore(catalog, loader, PERIOD);
        play(match, FIVE_SECONDS);
        play(opened, FIVE_SECONDS);
        assertEquals(match.getStatusText(), opened.getStatusText());
        assertEquals(match.getGoals(Side.LEFT), opened.getGoals(Side.LEFT));
        assertEquals(left.getBounds(), opened.getPlayer(Side.LEFT).getBounds());
        assertEquals(right.getBounds(), opened.getPlayer(Side.RIGHT).getBounds());
        assertEquals(match.getBallBounds(), opened.getBallBounds());
        assertEquals(right.getLife(), opened.getPlayer(Side.RIGHT).getLife(), DELTA);
        assertEquals(left.isAuraActive(), opened.getPlayer(Side.LEFT).isAuraActive());
        assertEquals(right.getEffectName(), opened.getPlayer(Side.RIGHT).getEffectName());
    }

    /** Lo que se exporta del progreso se importa igual. */
    @Test
    public void shouldImportTheExportedProgress() throws POOBSoccerException{
        left.addPoints(1600);
        left.upgrade("Survival");
        left.upgrade("Survival");
        gameFile.exportProgress(left, right, catalog.getStatNames(), progressFile);
        Player newLeft = new Player(Side.LEFT);
        Player newRight = new Player(Side.RIGHT);
        gameFile.importProgress(progressFile, newLeft, newRight, catalog);
        assertEquals(100, newLeft.getPoints());
        assertEquals(3, newLeft.getLevel("Survival"));
    }

    /** Abrir un partido que no existe lanza la excepción del juego. */
    @Test
    public void shouldFailToOpenAMissingMatch(){
        File missing = new File(FOLDER, "poobsoccer_no_existe.dat");
        try{
            gameFile.openMatch(missing);
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().startsWith(POOBSoccerException.FILE_NOT_FOUND));
        }
    }

    /** Un progreso con un nivel imposible dice su línea y no cambia a los jugadores. */
    @Test
    public void shouldReportTheLineOfABadProgress() throws Exception{
        PrintWriter out = new PrintWriter(progressFile);
        out.println("Jugador1, Puntos, 100");
        out.println("Jugador1, Kick, 11");
        out.close();
        try{
            gameFile.importProgress(progressFile, left, right, catalog);
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertEquals(POOBSoccerException.BAD_LINE + 2 + ": " + POOBSoccerException.BAD_LEVEL + 11, e.getMessage());
            assertEquals(0, left.getPoints());
            assertEquals(1, left.getLevel("Kick"));
        }
    }
}
