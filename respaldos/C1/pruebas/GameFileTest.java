package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Pruebas de la persistencia: guardar y abrir un partido, el progreso entre
 * ejecuciones, e importar y exportar el progreso. Los archivos de prueba van a
 * la carpeta temporal del sistema y se borran al terminar cada prueba.
 */
public class GameFileTest
{
    private static final long PERIOD = 10000000L;
    private static final int TEN_SECONDS = 1000;
    private static final int HALF_SECOND = 50;
    private static final int FIVE_SECONDS = 500;
    private static final double DELTA = 0.0001;
    private static final String TEMP = System.getProperty("java.io.tmpdir");
    private File matchFile;
    private File progressFile;
    private File textFile;
    private POOBSoccerGame game;

    /** Juego nuevo con archivos de prueba vacíos. */
    @BeforeEach
    public void setUp()
    {
        matchFile = new File(TEMP, "poobsoccer_prueba_partido.dat");
        progressFile = new File(TEMP, "poobsoccer_prueba_progreso.txt");
        textFile = new File(TEMP, "poobsoccer_prueba_exportado.txt");
        tearDown();
        game = new POOBSoccerGame(PERIOD, progressFile);
    }

    /** Borra los archivos de prueba. */
    @AfterEach
    public void tearDown()
    {
        matchFile.delete();
        progressFile.delete();
        textFile.delete();
    }

    private void play(POOBSoccerGame g, int updates)
    {
        for (int i = 0; i < updates; i++){
            g.update();
        }
    }

    private void write(File file, String text) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.print(text);
        out.close();
    }

    /** Partido a mitad: un gol, la máquina electrocutada, el aura activa y una mejora comprada. */
    private void playHalfMatch() throws POOBSoccerException
    {
        game.getPlayer(Side.LEFT).addPoints(700);
        game.upgrade(Side.LEFT, "Kick");
        game.startMatch("Corea del Sur", "Nigeria", "Defensiva", "Por tiempo", 2, "500x400");
        game.getMatch().goal(Side.LEFT);
        game.perform(Side.LEFT, Action.MOVE_RIGHT);
        play(game, TEN_SECONDS);
        game.perform(Side.LEFT, Action.POWER);
        game.getPlayer(Side.RIGHT).applyEffect(new ElectrocutedState());
        play(game, HALF_SECOND);
    }

    private POOBSoccerGame savedAndOpened() throws POOBSoccerException
    {
        playHalfMatch();
        game.saveAs(matchFile);
        POOBSoccerGame other = new POOBSoccerGame(PERIOD, progressFile);
        other.open(matchFile);
        return other;
    }

    /** Al abrir, el marcador es el mismo. */
    @Test
    public void shouldKeepTheScore() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals(1, other.getGoals(Side.LEFT));
        assertEquals(0, other.getGoals(Side.RIGHT));
    }

    /** Al abrir, el reloj es el mismo. */
    @Test
    public void shouldKeepTheClock() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals(game.getTimeText(), other.getTimeText());
    }

    /** Al abrir, la vida y la barra de poder son las mismas. */
    @Test
    public void shouldKeepLifeAndPowerBar() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals(game.getLife(Side.RIGHT), other.getLife(Side.RIGHT), DELTA);
        assertEquals(game.getPowerCharge(Side.LEFT), other.getPowerCharge(Side.LEFT), DELTA);
    }

    /** Al abrir, el aura y el efecto activo siguen. */
    @Test
    public void shouldKeepAuraAndEffect() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertTrue(other.isAuraActive(Side.LEFT));
        assertEquals("Electrocutado", other.getEffectName(Side.RIGHT));
    }

    /** Al abrir, los personajes, la modalidad y el perfil de la máquina son los mismos. */
    @Test
    public void shouldKeepCountriesAndMachineProfile() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals("Corea del Sur", other.getCountryName(Side.LEFT));
        assertEquals("Nigeria", other.getCountryName(Side.RIGHT));
        assertTrue(other.isMachine(Side.RIGHT));
    }

    /** Al abrir, las estadísticas y los puntos son los mismos. */
    @Test
    public void shouldKeepStatsAndPoints() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals(2, other.getStatLevel(Side.LEFT, "Kick"));
        assertEquals(200, other.getPoints(Side.LEFT));
    }

    /** Al abrir, los jugadores y el balón están donde estaban. */
    @Test
    public void shouldKeepPositions() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        assertEquals(game.getPlayer(Side.LEFT).getBounds(), other.getPlayer(Side.LEFT).getBounds());
        assertEquals(game.getPlayer(Side.RIGHT).getBounds(), other.getPlayer(Side.RIGHT).getBounds());
        assertEquals(game.getMatch().getBallBounds(), other.getMatch().getBallBounds());
    }

    /** El partido abierto continúa igual que el original durante cinco segundos más. */
    @Test
    public void shouldContinueIdentically() throws POOBSoccerException
    {
        POOBSoccerGame other = savedAndOpened();
        play(game, FIVE_SECONDS);
        play(other, FIVE_SECONDS);
        assertEquals(game.getTimeText(), other.getTimeText());
        assertEquals(game.getPlayer(Side.LEFT).getBounds(), other.getPlayer(Side.LEFT).getBounds());
        assertEquals(game.getPlayer(Side.RIGHT).getBounds(), other.getPlayer(Side.RIGHT).getBounds());
        assertEquals(game.getMatch().getBallBounds(), other.getMatch().getBallBounds());
        assertEquals(game.getEffectName(Side.RIGHT), other.getEffectName(Side.RIGHT));
    }

    /** Sin partido en curso no hay nada que guardar. */
    @Test
    public void shouldNotSaveWithoutAMatch()
    {
        try{
            game.saveAs(matchFile);
            fail("Debió rechazar guardar sin partido");
        }
        catch (POOBSoccerException e){
            assertEquals(POOBSoccerException.NO_MATCH_TO_SAVE, e.getMessage());
        }
    }

    /** Abrir un archivo que no existe lanza la excepción del juego. */
    @Test
    public void shouldRejectAMissingFile()
    {
        try{
            game.open(matchFile);
            fail("Debió rechazar un archivo que no existe");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().startsWith(POOBSoccerException.FILE_NOT_FOUND));
        }
    }

    /** Abrir un archivo que no es un partido lanza la excepción del juego. */
    @Test
    public void shouldRejectADamagedFile() throws IOException
    {
        write(matchFile, "esto no es un partido");
        try{
            game.open(matchFile);
            fail("Debió rechazar un archivo dañado");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().startsWith(POOBSoccerException.DAMAGED_MATCH));
        }
    }

    /** Un partido solo se abre desde un archivo .dat. */
    @Test
    public void shouldRejectTheWrongExtension()
    {
        try{
            game.open(textFile);
            fail("Debió rechazar un archivo .txt");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().startsWith(POOBSoccerException.WRONG_EXTENSION));
        }
    }

    /** Lo que se exporta se puede importar y queda igual. */
    @Test
    public void shouldImportWhatWasExported() throws POOBSoccerException
    {
        game.getPlayer(Side.RIGHT).addPoints(1600);
        game.upgrade(Side.RIGHT, "Survival");
        game.upgrade(Side.RIGHT, "Survival");
        game.exportAs(textFile);
        POOBSoccerGame other = new POOBSoccerGame(PERIOD, progressFile);
        other.import_(textFile);
        assertEquals(100, other.getPoints(Side.RIGHT));
        assertEquals(3, other.getStatLevel(Side.RIGHT, "Survival"));
    }

    /** El progreso guardado al cerrar se recupera en la siguiente ejecución. */
    @Test
    public void shouldKeepProgressBetweenRuns() throws POOBSoccerException
    {
        game.getPlayer(Side.LEFT).addPoints(900);
        game.upgrade(Side.LEFT, "Speed");
        game.saveProgress();
        POOBSoccerGame nextRun = new POOBSoccerGame(PERIOD, progressFile);
        nextRun.loadProgress();
        assertEquals(400, nextRun.getPoints(Side.LEFT));
        assertEquals(2, nextRun.getStatLevel(Side.LEFT, "Speed"));
    }

    /** La primera vez no hay progreso guardado y no es un error. */
    @Test
    public void shouldStartWithoutProgressTheFirstTime() throws POOBSoccerException
    {
        game.loadProgress();
        assertEquals(0, game.getPoints(Side.LEFT));
    }

    /** Al terminar un partido el progreso queda guardado. */
    @Test
    public void shouldSaveProgressWhenAMatchEnds() throws POOBSoccerException
    {
        game.startMatch("Nigeria", "Camerún", null, "Por goles", 1, "500x400");
        game.getMatch().goal(Side.LEFT);
        game.update();
        POOBSoccerGame nextRun = new POOBSoccerGame(PERIOD, progressFile);
        nextRun.loadProgress();
        assertEquals(100, nextRun.getPoints(Side.LEFT));
        assertEquals(25, nextRun.getPoints(Side.RIGHT));
    }

    /** Un jugador que no existe se rechaza indicando la línea. */
    @Test
    public void shouldRejectAnUnknownPlayerWithItsLine() throws IOException
    {
        write(textFile, "Jugador3, Speed, 2\n");
        try{
            game.import_(textFile);
            fail("Debió rechazar Jugador3");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().startsWith(POOBSoccerException.BAD_LINE + "1"));
        }
    }

    /** Una estadística que no existe se rechaza. */
    @Test
    public void shouldRejectAnUnknownStat() throws IOException
    {
        write(textFile, "Jugador1, Suerte, 2\n");
        try{
            game.import_(textFile);
            fail("Debió rechazar la estadística Suerte");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("Suerte"));
        }
    }

    /** Un nivel mayor que 10 se rechaza. */
    @Test
    public void shouldRejectALevelAbove10() throws IOException
    {
        write(textFile, "Jugador1, Kick, 11\n");
        try{
            game.import_(textFile);
            fail("Debió rechazar el nivel 11");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("11"));
        }
    }

    /** Si una línea está mal, el progreso no cambia en nada. */
    @Test
    public void shouldNotChangeProgressWhenALineIsWrong() throws IOException
    {
        game.getPlayer(Side.LEFT).addPoints(300);
        write(textFile, "Jugador1, Puntos, 5000\nJugador1, Kick, tres\n");
        try{
            game.import_(textFile);
            fail("Debió rechazar el valor tres");
        }
        catch (POOBSoccerException e){
            assertEquals(300, game.getPoints(Side.LEFT));
        }
    }
}
