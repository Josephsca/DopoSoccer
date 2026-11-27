
package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

public class PowerShotTest {

    private Ball ball;
    private Player rival;

    @BeforeEach
    public void setUp() {
        ball = new Ball(400, 200, 14, 0.45);
        rival = new Player("Rival", new USA(), false);
    }

    // BLUE AURA

    @Test
    public void shouldLaunchBlueAura() {
        BlueAura power = new BlueAura();

        power.launch(ball);

        assertEquals(22, ball.getVelX(), 0.001);
        assertEquals(-2, ball.getVelY(), 0.001);
    }

    @Test
    public void shouldPushRivalWithBlueAura() {
        SouthKorea country = new SouthKorea();
        int initialX = rival.getPosX();

        country.applyEffect(rival);

        assertFalse(rival.canMove());
        assertEquals(
            initialX - BlueAura.PUSH_DISTANCE
                * rival.getAttackDirection(),
            rival.getPosX()
        );
        assertSame(country, rival.getAffectedBy());
    }

    // LIGHTNING SHOT

    @Test
    public void shouldLaunchLightningShot() {
        LightningShot power = new LightningShot();

        power.launch(ball);

        assertEquals(18, ball.getVelX(), 0.001);
        assertEquals(-4, ball.getVelY(), 0.001);
    }

    @Test
    public void shouldApplyLightningEffect() {
        Cameroon country = new Cameroon();

        country.applyEffect(rival);

        assertFalse(rival.canJump());
        assertEquals(0.5, rival.getSpeedFactor(), 0.001);
        assertSame(country, rival.getAffectedBy());
    }

    // TORNADO SHOT

    @Test
    public void shouldLaunchTornadoShot() {
        TornadoShot power = new TornadoShot();

        power.launch(ball);

        assertEquals(15, ball.getVelX(), 0.001);
        assertEquals(8, ball.getVelY(), 0.001);
    }

    @Test
    public void shouldApplyTornadoEffect() {
        Nigeria country = new Nigeria();

        country.applyEffect(rival);

        assertFalse(rival.canMove());
        assertFalse(rival.canJump());
        assertEquals(-14, rival.getVelY(), 0.001);
        assertSame(country, rival.getAffectedBy());
    }

    // ILLUSION SHOT

    @Test
    public void shouldLaunchIllusionShot() {
        IllusionShot power = new IllusionShot();

        power.launch(ball);

        assertTrue(ball.hasIllusion());
        assertEquals(14, ball.getVelX(), 0.001);
        assertEquals(-7, ball.getVelY(), 0.001);
    }

    @Test
    public void shouldApplyIllusionEffect() {
        USA country = new USA();

        country.applyEffect(rival);

        assertSame(country, rival.getAffectedBy());
        assertEquals(2.5, rival.getEffectTime(), 0.001);
    }

    @Test
    public void shouldFinishBallIllusion() {
        ball.activateIllusion(1);

        assertTrue(ball.hasIllusion());

        for (int i = 0; i < 120; i++) {
            ball.update();
        }

        assertFalse(ball.hasIllusion());
    }

    @Test
    public void shouldIncreaseIllusionElapsedTime() {
        ball.activateIllusion(2);

        ball.update();

        assertTrue(ball.getIllusionElapsed() > 0);
    }

    // SHOT DIRECTION

    @Test
    public void shouldLaunchPowerToTheLeft() {
        ball.setPowerShooter(rival);

        new LightningShot().launch(ball);

        assertEquals(-18, ball.getVelX(), 0.001);
    }

    @Test
    public void shouldLaunchPowerToTheRight() {
        Player shooter = new Player(
            "Shooter", new Cameroon(), true
        );

        ball.setPowerShooter(shooter);

        new LightningShot().launch(ball);

        assertEquals(18, ball.getVelX(), 0.001);
    }

    // CHARACTER AND POWER SHOT

    @Test
    public void shouldGetPowerProperties() {
        BlueAura power = new BlueAura();

        assertEquals("Aura Azul", power.getName());
        assertEquals(40, power.getDamage());
        assertEquals(1.5, power.getDuration(), 0.001);
    }

    @Test
    public void shouldGetCharacterPowerProperties() {
        Cameroon country = new Cameroon();

        assertEquals(60, country.getShotDamage());
        assertEquals(3, country.getEffectDuration(), 0.001);
        assertTrue(
            country.getPowerShot() instanceof LightningShot
        );
    }

    @Test
    public void shouldLaunchPowerThroughCharacter() {
        Nigeria country = new Nigeria();

        country.powerShot(ball);

        assertEquals(15, ball.getVelX(), 0.001);
        assertEquals(8, ball.getVelY(), 0.001);
    }

    // PLAYER EFFECTS

    @Test
    public void shouldClearPlayerEffect() {
        new Cameroon().applyEffect(rival);

        rival.clearEffect();

        assertTrue(rival.canMove());
        assertTrue(rival.canJump());
        assertEquals(1, rival.getSpeedFactor(), 0.001);
        assertEquals(0, rival.getEffectTime(), 0.001);
        assertNull(rival.getAffectedBy());
    }

    @Test
    public void shouldReplacePreviousEffect() {
        new Nigeria().applyEffect(rival);

        Cameroon country = new Cameroon();
        country.applyEffect(rival);

        assertTrue(rival.canMove());
        assertFalse(rival.canJump());
        assertEquals(0.5, rival.getSpeedFactor(), 0.001);
        assertSame(country, rival.getAffectedBy());
    }

    @Test
    public void shouldRecoverAfterEffectDuration() {
        new Cameroon().applyEffect(rival);

        for (int i = 0; i < 240; i++) {
            rival.update();
        }

        assertTrue(rival.canJump());
        assertTrue(rival.canMove());
        assertEquals(1, rival.getSpeedFactor(), 0.001);
        assertNull(rival.getAffectedBy());
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
