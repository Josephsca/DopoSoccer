package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

public class MatchTest {

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
    public void shouldStartZeroToZero() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        assertEquals(0, left.getScore());
        assertEquals(0, right.getScore());
        assertFalse(match.isGameOver());
    }

    @Test
    public void shouldScoreWhenBallEntersRightGoal() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        Goal goal = match.getRightGoal();
        match.getBall().reset(goal.getPosX() + goal.getWidth() / 2 + 5, goal.getPosY() + goal.getHeight() / 2);
        match.checkGoal();
        assertEquals(1, left.getScore());
    }

    @Test
    public void shouldChargeQuarterBarToPlayerWhoReceivesGoal() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        Goal goal = match.getLeftGoal();
        match.getBall().reset(goal.getPosX() + goal.getWidth() / 2 - 5, goal.getPosY() + goal.getHeight() / 2);
        match.checkGoal();
        assertEquals(1, right.getScore());
        assertEquals(25, left.getPowerBar(), 0.001);
    }

    @Test
    public void shouldEndGoalMatchWhenReachingGoals() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(2));
        left.addGoal();
        left.addGoal();
        match.update();
        assertTrue(match.isGameOver());
        assertSame(left, match.getWinner());
    }

    @Test
    public void shouldGivePointsToWinnerAndLoser() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(1));
        right.addGoal();
        match.update();
        assertEquals(100, right.getPoints());
        assertEquals(25, left.getPoints());
    }

    @Test
    public void shouldPlaySuddenDeathWhenTimeEndsTied() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        TimeCondition condition = new TimeCondition(1);
        Match match = new Match(left, right, stadium, condition);
        for (int i = 0; i < 60 * Match.FPS + 5; i++) match.update();
        assertFalse(match.isGameOver());
        assertTrue(condition.isSuddenDeath());
        left.addGoal();
        match.update();
        assertTrue(match.isGameOver());
        assertSame(left, match.getWinner());
    }

    @Test
    public void shouldNotAdvanceWhilePaused() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        match.pause();
        match.update();
        assertEquals(0, match.getTimer());
        match.resume();
        match.update();
        assertEquals(1, match.getTimer());
    }

    @Test
    public void shouldDamageRivalWithKick() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        right.setPosX(left.getPosX() + left.getWidth() + 5);
        match.getBall().reset(10, 10);   // balón lejos
        left.kick(right, match.getBall());
        assertEquals(90, right.getLife(), 0.001);
    }

    @Test
    public void shouldApplyPowerShotDamageAndEffect() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(3));
        Ball ball = match.getBall();
        ball.setPowerShooter(left);
        ball.setPosX(right.getPosX());
        ball.setPosY(right.getPosY());
        match.checkCollisions();
        assertEquals(40, right.getLife(), 0.001);         
        assertEquals(0.5, right.getSpeedFactor(), 0.001); 
        assertNull(ball.getPowerShooter());
    }

    @Test
    public void shouldFinishAtAnyMoment() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Stadium stadium = new Stadium(Stadium.MEDIUM);
        Match match = new Match(left, right, stadium, new GoalCondition(5));
        match.finish();
        assertTrue(match.isGameOver());
        assertNull(match.getWinner());  
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
