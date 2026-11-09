package test;

import domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class StadiumTest {

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
    public void shouldHaveThreeResolutions() throws DopoSoccerException {
        assertEquals(800, new Stadium(Stadium.SMALL).getWidth());
        assertEquals(1024, new Stadium(Stadium.MEDIUM).getWidth());
        assertEquals(1280, new Stadium(Stadium.LARGE).getWidth());
    }

    @Test
    public void shouldKeepPlayerInsideField() throws DopoSoccerException {
        Stadium stadium = new Stadium(Stadium.SMALL);
        Player player = new Player("J1", new USA(), true);
        player.setPosX(-50);
        player.setPosY(1000);
        stadium.keepInBounds(player);
        assertEquals(0, player.getPosX());
        assertEquals(stadium.getGroundY() - player.getHeight(), player.getPosY());
    }

    @Test
    public void shouldMakeBallBounceOnGround() throws DopoSoccerException {
        Stadium stadium = new Stadium(Stadium.SMALL);
        Ball ball = new Ball(400, 100, 14, 0.45);
        ball.setPosY(stadium.getGroundY());
        ball.setVelY(10);
        stadium.keepInBounds(ball);
        assertTrue(ball.getVelY() < 0);   
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
