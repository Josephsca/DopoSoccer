package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

public class PlayerTest {


    @BeforeEach
    public void setUp() {
    }

    @Test
    public void shouldStartWithFullLifeAndEmptyBar() {
        Player player = new Player("J1", new Cameroon(), true);
        assertEquals(100, player.getLife(), 0.001);
        assertEquals(0, player.getPowerBar(), 0.001);
        assertEquals(0, player.getScore());
        assertFalse(player.hasAura());
    }

    @Test
    public void shouldCalculateMaxLifeWithSurvival() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(500);
        player.upgradeStat("survival");   // survival = 2
        assertEquals(110, player.getMaxLife(), 0.001);
    }

    @Test
    public void shouldCalculateKickMultiplier() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        assertEquals(1.0, player.getKickMultiplier(), 0.001);
        player.addPoints(500);
        player.upgradeStat("kick");       // kick = 2
        assertEquals(1.1, player.getKickMultiplier(), 0.001);
    }

    @Test
    public void shouldNotHaveNegativeLife() {
        Player player = new Player("J1", new Cameroon(), true);
        player.receiveDamage(500);
        assertEquals(0, player.getLife(), 0.001);
    }

    @Test
    public void shouldBeStunnedWhenLifeReachesZero() {
        Player player = new Player("J1", new Cameroon(), true);
        player.receiveDamage(100);
        assertTrue(player.isStunned());
    }

    @Test
    public void shouldRecoverLifeAfterStun() {
        Player player = new Player("J1", new Cameroon(), true);
        player.receiveDamage(100);
        for (int i = 0; i < 3 * Match.FPS + 1; i++) player.update();
        assertFalse(player.isStunned());
        assertEquals(player.getMaxLife(), player.getLife(), 0.001);
    }

    @Test
    public void shouldNotMoveWhileStunned() {
        Player player = new Player("J1", new Cameroon(), true);
        player.receiveDamage(100);
        int x = player.getPosX();
        player.moveRight();
        player.update();
        assertEquals(x, player.getPosX());
    }

    @Test
    public void shouldFillPowerBarInTenSeconds() {
        Player player = new Player("J1", new Cameroon(), true);
        player.chargePowerBar(5);
        assertEquals(50, player.getPowerBar(), 0.001);
        player.chargePowerBar(10);
        assertEquals(100, player.getPowerBar(), 0.001);
    }

    @Test
    public void shouldActivatePowerOnlyWithFullBar() {
        Player player = new Player("J1", new Cameroon(), true);
        player.chargePowerBar(9);
        player.activatePower();
        assertFalse(player.hasAura());
        player.chargePowerBar(1);
        player.activatePower();
        assertTrue(player.hasAura());
        assertEquals(0, player.getPowerBar(), 0.001);
    }

    @Test
    public void shouldNotActivateAnotherPowerWhileHavingAura() {
        Player player = new Player("J1", new Cameroon(), true);
        player.chargePowerBar(10);
        player.activatePower();
        player.chargePowerBar(10);
        player.activatePower();
        assertTrue(player.hasAura());
        assertEquals(100, player.getPowerBar(), 0.001);   
    }

    @Test
    public void shouldDoubleUpgradeCost() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        assertEquals(500, player.getUpgradeCost("speed"));
        player.addPoints(500);
        player.upgradeStat("speed");
        assertEquals(1000, player.getUpgradeCost("speed"));
        assertEquals(0, player.getPoints());
    }

    @Test
    public void shouldNotUpgradeWithoutPoints() {
        try {
            Player player = new Player("J1", new Cameroon(), true);
            player.upgradeStat("speed");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotUpgradeInvalidStat() {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(1000);
        try {
            player.upgradeStat("fly");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.INVALID_STAT, e.getMessage());
        }
    }

    @Test
    public void shouldNotUpgradeAboveTen() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(1_000_000);
        for (int i = 1; i < Player.MAX_STAT; i++) player.upgradeStat("jump");
        assertEquals(10, player.getStat("jump"));
        try {
            player.upgradeStat("jump");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.MAX_STAT_REACHED, e.getMessage());
        }
    }

    @Test
    public void shouldRemoveEffectWhenTimeEnds() {
        Player player = new Player("J1", new Cameroon(), true);
        new Nigeria().applyEffect(player);
        assertFalse(player.canMove());
        for (int i = 0; i < 3 * Match.FPS + 1; i++) player.update();
        assertTrue(player.canMove());
        assertNull(player.getAffectedBy());
    }

    @Test
    public void shouldCopyProgressFromSavedPlayer() throws DopoSoccerException {
        Player saved = new Player("J1", new Cameroon(), true);
        saved.addPoints(500);
        saved.upgradeStat("survival");
        saved.addPoints(300);
        Player newPlayer = new Player("J1", new USA(), true);
        newPlayer.copyProgress(saved);
        assertEquals(300, newPlayer.getPoints());
        assertEquals(2, newPlayer.getStat("survival"));
        assertEquals(110, newPlayer.getMaxLife(), 0.001);
    }

    @Test
    public void shouldCountOnlyPointsOfThisMatch() {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(100);
        player.startMatch();
        player.addPoints(25);
        assertEquals(25, player.getMatchPoints());
        assertEquals(125, player.getPoints());
    }

    // Casos extremos 

    @Test
    public void shouldIgnoreDamageWhileStunned() {
        Player player = new Player("J1", new Cameroon(), true);
        player.receiveDamage(100);
        for (int i = 0; i < 2 * Match.FPS; i++) player.update();  
        player.receiveDamage(50);                             
        for (int i = 0; i < Match.FPS + 1; i++) player.update();   
        assertFalse(player.isStunned());
        assertEquals(100, player.getLife(), 0.001);
    }

    @Test
    public void shouldBeStunnedWithManySmallHitsOnlyWhenLifeIsExactlyZero() {
        Player player = new Player("J1", new Cameroon(), true);
        for (int i = 0; i < 9; i++) player.receiveDamage(11);   
        assertFalse(player.isStunned());
        assertEquals(1, player.getLife(), 0.001);
        player.receiveDamage(11);
        assertTrue(player.isStunned());
        assertEquals(0, player.getLife(), 0.001);
    }

    @Test
    public void shouldNotActivatePowerWhileStunned() {
        Player player = new Player("J1", new Cameroon(), true);
        player.chargePowerBar(10);
        player.receiveDamage(100);
        player.activatePower();
        assertFalse(player.hasAura());
        assertEquals(100, player.getPowerBar(), 0.001); 
    }

    @Test
    public void shouldNotKickOrJumpWhileStunned() {
        Player player = new Player("J1", new Cameroon(), true);
        Player rival = new Player("J2", new USA(), false);
        rival.setPosX(player.getPosX() + player.getWidth() + 5);
        player.receiveDamage(100);
        player.kick(rival, new Ball(2000, 2000, 14, 0.45));
        player.jump();
        assertEquals(100, rival.getLife(), 0.001);
        assertFalse(player.isJumping());
    }

    @Test
    public void shouldReplaceEffectInsteadOfStacking() {
        Player player = new Player("J1", new SouthKorea(), true);
        new Cameroon().applyEffect(player);          
        new USA().applyEffect(player);                
        assertTrue(player.canJump());
        assertEquals(1, player.getSpeedFactor(), 0.001);
        assertEquals("USA", player.getAffectedBy().getCountry());
        assertEquals(2.5, player.getEffectTime(), 0.001);
    }

    @Test
    public void shouldNotPassFullPowerBar() {
        Player player = new Player("J1", new Cameroon(), true);
        player.chargePowerBar(1000);
        assertEquals(Player.POWER_FULL, player.getPowerBar(), 0.001);
    }

    @Test
    public void shouldReachMaximumValuesOfTheStatement() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(1_000_000);
        for (int i = 1; i < Player.MAX_STAT; i++) {
            player.upgradeStat("survival");
            player.upgradeStat("kick");
        }
        assertEquals(190, player.getMaxLife(), 0.001);     
        assertEquals(1.9, player.getKickMultiplier(), 0.001); 
    }

    @Test
    public void shouldChargeExactCostForLastUpgrade() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(1_000_000);
        for (int i = 1; i < Player.MAX_STAT - 1; i++) player.upgradeStat("speed");
        assertEquals(128000, player.getUpgradeCost("speed"));   
        int before = player.getPoints();
        player.upgradeStat("speed");
        assertEquals(before - 128000, player.getPoints());
    }

    @Test
    public void shouldNotLosePointsWhenUpgradeFails() throws DopoSoccerException {
        Player player = new Player("J1", new Cameroon(), true);
        player.addPoints(499);
        try {
            player.upgradeStat("kick");
            fail("Debió lanzar excepción");
        } catch (DopoSoccerException e) {
            assertEquals(499, player.getPoints());
            assertEquals(1, player.getStat("kick"));
        }
    }

    @Test
    public void shouldNotKickAgainUntilKickEnds() {
        Player player = new Player("J1", new Cameroon(), true);
        Player rival = new Player("J2", new USA(), false);
        rival.setPosX(player.getPosX() + player.getWidth() + 5);
        Ball far = new Ball(3000, 3000, 14, 0.45);
        player.kick(rival, far);
        assertTrue(player.isKicking());
        for (int i = 0; i < 5; i++) player.kick(rival, far);   
        assertEquals(90, rival.getLife(), 0.001);
        for (int i = 0; i < Player.KICK_TIME * Match.FPS + 1; i++) player.update();
        assertFalse(player.isKicking());
        rival.setPosX(player.getPosX() + player.getWidth() + 5);  
        rival.setPosY(player.getPosY());                           
        player.kick(rival, far);
        assertEquals(80, rival.getLife(), 0.001);
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
