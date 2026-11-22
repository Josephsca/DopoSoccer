package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
