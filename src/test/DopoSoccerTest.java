package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

import org.junit.jupiter.api.AfterEach;

public class DopoSoccerTest {


    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
    }

    @Test
    public void shouldStartNewMatch() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.SMALL);
        Match match = game.getCurrentMatch();
        assertNotNull(match);
        assertEquals("Ana", match.getLeftPlayer().getName());
        assertEquals("USA", match.getRightPlayer().getCharacter().getCountry());
        assertEquals(800, match.getStadium().getWidth());
    }

    @Test
    public void shouldUseDefaultNames() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("", "Nigeria", null, "USA", DopoSoccer.TIME, 2, Stadium.LARGE);
        assertEquals("J1", game.getCurrentMatch().getLeftPlayer().getName());
        assertEquals("J2", game.getCurrentMatch().getRightPlayer().getName());
    }

    @Test
    public void shouldNotAcceptInvalidCharacter() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startNewMatch("Ana", "Colombia", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.SMALL);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_CHARACTER, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptInvalidMatchType() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startNewMatch("Ana", "Nigeria", "Luis", "USA", "LEAGUE", 3, Stadium.SMALL);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_MATCH_TYPE, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptZeroLimit() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.TIME, 0, Stadium.SMALL);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_MATCH_TYPE, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptInvalidResolution() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.TIME, 2, "4K");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_RESOLUTION, e.getMessage());
        }
    }

    @Test
    public void shouldMovePlayer() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM);
        Player p1 = game.getCurrentMatch().getLeftPlayer();
        int x = p1.getPosX();
        game.moveRight(1);
        game.update();
        assertTrue(p1.getPosX() > x);
    }

    @Test
    public void shouldPauseAndResume() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM);
        game.pauseMatch();
        assertTrue(game.getCurrentMatch().isPaused());
        game.resumeMatch();
        assertFalse(game.getCurrentMatch().isPaused());
    }

    @Test
    public void shouldToggleSound() {
        DopoSoccer game = new DopoSoccer();
        assertTrue(game.isSound());
        game.toggleSound();
        assertFalse(game.isSound());
    }




        /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }

    @Test
    public void shouldNotMoveWhilePaused() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("J1", "Cameroon", "J2", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM);
        int x = game.getCurrentMatch().getLeftPlayer().getPosX();
        game.pauseMatch();
        game.moveRight(1);
        game.resumeMatch();
        game.update();
        assertEquals(x, game.getCurrentMatch().getLeftPlayer().getPosX());
    }

    @Test
    public void shouldNotGetProfileWithoutName() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.getProfile("  ");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_NAME, e.getMessage());
        }
    }

    @Test
    public void shouldNotUpgradeProfileWithoutPoints() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.upgradeStat("JugadorSinPuntosDePrueba", "kick");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
    }

    @Test
    public void shouldHaveAPausedMatchFromTheStart() {
        DopoSoccer game = new DopoSoccer();
        assertNotNull(game.getCurrentMatch());
        assertTrue(game.getCurrentMatch().isPaused());
        assertFalse(game.getCurrentMatch().isGameOver());
    }

    // ---------- Casos extremos ----------

    @Test
    public void shouldNotControlMachineFromKeyboard() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startMachineMatch("Ana", "Cameroon", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM, false, "Torpe");
        assertTrue(game.getCurrentMatch().getRightPlayer() instanceof Machine);
        game.getCurrentMatch().getRightPlayer().chargePowerBar(10);   // barra llena
        game.activatePower(2);                        // jugador 2 es la máquina
        assertFalse(game.getCurrentMatch().getRightPlayer().hasAura());
    }

    @Test
    public void shouldNotAcceptInvalidMachineStrategy() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.startMachineMatch("Ana", "Cameroon", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM, false, "Imposible");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_STRATEGY + "Imposible", e.getMessage());
        }
    }

    @Test
    public void shouldIgnoreInvalidPlayerNumber() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM);
        game.moveLeft(3);
        game.kick(0);
        game.jump(-1);
        game.update();
        assertEquals(1, game.getCurrentMatch().getTimer());   // no falló: el partido siguió normal
    }

    @Test
    public void shouldNotAcceptNegativeLimit() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, -3, Stadium.SMALL);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_MATCH_TYPE, e.getMessage());
        }
    }

    @Test
    public void shouldSaveAndLoadTheSameMatch(@TempDir Path folder) throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startNewMatch("Ana", "Nigeria", "Luis", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM);
        game.getCurrentMatch().getLeftPlayer().addGoal();
        for (int i = 0; i < 30; i++) game.update();
        String file = folder.resolve("partida.dat").toString();
        game.saveGame(file);

        DopoSoccer other = new DopoSoccer();
        other.loadGame(file);
        assertEquals(1, other.getCurrentMatch().getLeftPlayer().getScore());
        assertEquals(30, other.getCurrentMatch().getTimer());
        assertEquals("Luis", other.getCurrentMatch().getRightPlayer().getName());
    }

    @Test
    public void shouldFailLoadingAFileThatIsNotAMatch(@TempDir Path folder) throws Exception {
        Path file = folder.resolve("basura.dat");
        Files.writeString(file, "esto no es una partida");
        DopoSoccer game = new DopoSoccer();
        try {
            game.loadGame(file.toString());
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertTrue(e.getMessage().startsWith(DopoSoccerException.LOAD_ERROR_MESSAGE));
        }
        assertNotNull(game.getCurrentMatch());   // aunque falle, sigue teniendo su partido
    }

    @Test
    public void shouldFailLoadingAFileThatDoesNotExist() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.loadGame("no/existe/partida.dat");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertTrue(e.getMessage().startsWith(DopoSoccerException.LOAD_ERROR_MESSAGE));
        }
    }

    @Test
    public void shouldNotAcceptSameNameForBothPlayers() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.startNewMatch("Ana", "Nigeria", " Ana ", "USA", DopoSoccer.GOALS, 3, Stadium.SMALL);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.SAME_NAMES, e.getMessage());
        }
    }

    @Test
    public void shouldNotAcceptNullMachineStrategy() {
        DopoSoccer game = new DopoSoccer();
        try {
            game.startMachineMatch("Ana", "Cameroon", "USA", DopoSoccer.GOALS, 3, Stadium.MEDIUM, false, null);
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertTrue(e.getMessage().startsWith(DopoSoccerException.INVALID_STRATEGY));
        }
    }
}
