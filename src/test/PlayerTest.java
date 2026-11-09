package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    public void shouldReplaceEffectInsteadOfStacking() {
        Player player = new Player("J1", new Cameroon(), true);
        new Cameroon().applyEffect(player);
        assertEquals(0.5, player.getSpeedFactor(), 0.001);
        assertFalse(player.canJump());
        new USA().applyEffect(player);
        assertEquals(-1, player.getSpeedFactor(), 0.001);
        assertTrue(player.canJump());  
        assertEquals("USA", player.getAffectedBy().getCountry());
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
}
