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


    @Test
    public void shouldNotMoveIntoRival() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        // ponemos al jugador 1 pegado al jugador 2
        left.setPosX(right.getPosX() - left.getWidth() + Match.ALLOWED_OVERLAP);
        int x = left.getPosX();
        match.moveRight(left);
        left.update();
        assertEquals(x, left.getPosX());
    }

    @Test
    public void shouldMoveAwayFromRival() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        left.setPosX(right.getPosX() - left.getWidth() + Match.ALLOWED_OVERLAP);
        int x = left.getPosX();
        match.moveLeft(left);
        left.update();
        assertTrue(left.getPosX() < x);
    }

    @Test
    public void shouldMoveWhenRivalIsFar() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        int x = left.getPosX();
        assertFalse(match.willCollide(left, 1));
        match.moveRight(left);
        left.update();
        assertTrue(left.getPosX() > x);
    }

    // Casos extremos

    @Test
    public void shouldNotScoreWhenBallIsOnlyHalfInsideTheGoal() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        Goal goal = match.getLeftGoal();
        Ball ball = match.getBall();
        ball.reset(goal.getPosX() + goal.getWidth(), goal.getPosY() + goal.getHeight() / 2);
        match.checkGoal();
        assertEquals(0, right.getScore());
    }

    @Test
    public void shouldCountPowerShotGoalWithoutDamageIfItDoesNotTouchRival() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        Ball ball = match.getBall();
        Goal goal = match.getRightGoal();
        ball.setPowerShooter(left);
        ball.setPosX(goal.getPosX() + 10);     
        ball.setPosY(goal.getPosY() + 20);
        match.checkCollisions();
        match.checkGoal();
        assertEquals(1, left.getScore());
        assertEquals(right.getMaxLife(), right.getLife(), 0.001);
        assertNull(right.getAffectedBy());
        assertNull(ball.getPowerShooter());   
    }

    @Test
    public void shouldApplyOfficialExampleOfTheStatement() throws DopoSoccerException {
        // Ejemplo del enunciado: J1 Camerún con Kick 5, J2 con Survival 3
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        left.addPoints(1_000_000);
        for (int i = 0; i < 4; i++) left.upgradeStat("kick");
        right.addPoints(1_000_000);
        for (int i = 0; i < 2; i++) right.upgradeStat("survival");
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        assertEquals(120, right.getLife(), 0.001);
        right.setPosX(left.getPosX() + left.getWidth() + 5);
        match.getBall().reset(10, 10);
        left.kick(right, match.getBall());          
        assertEquals(106, right.getLife(), 0.001);
        right.startMatch();                          
        Ball ball = match.getBall();
        ball.setPowerShooter(left);
        ball.setPosX(right.getPosX());
        ball.setPosY(right.getPosY());
        match.checkCollisions();                     
        assertEquals(36, right.getLife(), 0.001);
        assertFalse(right.canJump());
        assertEquals(0.5, right.getSpeedFactor(), 0.001);
    }

    @Test
    public void shouldOnlyPushBallWhenKickReachesBallAndRival() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        right.setPosX(left.getPosX() + left.getWidth() + 5);
        Ball ball = match.getBall();
        ball.setPosX(left.getPosX() + left.getWidth());
        ball.setPosY(left.getPosY() + 20);
        left.kick(right, ball);
        assertEquals(100, right.getLife(), 0.001);   
        assertTrue(ball.getVelX() > 0);               
    }

    @Test
    public void shouldWinByTimeWhenOnePlayerIsAhead() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new TimeCondition(1));
        right.addGoal();
        for (int i = 0; i < 60 * Match.FPS - 1; i++) match.update();
        assertFalse(match.isGameOver());            
        match.update();
        assertTrue(match.isGameOver());
        assertSame(right, match.getWinner());
    }

    @Test
    public void shouldGiveLosePointsToBothWhenFinishedTied() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(5));
        match.finish();
        match.finish();                               
        assertEquals(25, left.getPoints());
        assertEquals(25, right.getPoints());
    }

    @Test
    public void shouldNotCollideWhenJumpingOverRival() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        left.setPosX(right.getPosX() - left.getWidth() + Match.ALLOWED_OVERLAP);
        left.setPosY(right.getPosY() - right.getHeight() - 1);   
        assertFalse(match.willCollide(left, 1));
    }

    @Test
    public void shouldEndPowerShotWhenBallHitsSideWall() throws DopoSoccerException {
        Player left = new Player("J1", new Cameroon(), true);
        Player right = new Player("J2", new SouthKorea(), false);
        Match match = new Match(left, right, new Stadium(Stadium.MEDIUM), new GoalCondition(3));
        Ball ball = match.getBall();
        ball.setPowerShooter(left);
        ball.setPosY(40);            
        ball.setVelX(-25);               
        for (int i = 0; i < 60; i++) match.update();
        assertNull(ball.getPowerShooter());
        assertEquals(right.getMaxLife(), right.getLife(), 0.001);
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
