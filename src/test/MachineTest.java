
package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MachineTest {

    private Machine machine;
    private Player rival;
    private Match match;
    private Stadium stadium;

    @BeforeEach
    public void setUp() throws DopoSoccerException {
        stadium = new Stadium(Stadium.MEDIUM);

        machine = new Machine(
            "Machine", new Cameroon(),
            true, new OffensiveStrategy()
        );

        rival = new Player(
            "Human", new USA(), false
        );

        match = new Match(
            machine, rival, stadium,
            new GoalCondition(3)
        );
    }


    @Test
    public void shouldGetMachineStrategy() {
        assertTrue(
            machine.getStrategy() instanceof OffensiveStrategy
        );
    }

    @Test
    public void shouldChangeMachineStrategy() {
        MachineStrategy strategy = new DefensiveStrategy();

        machine.setStrategy(strategy);

        assertSame(strategy, machine.getStrategy());
    }


    @Test
    public void shouldMoveRightWhenBallIsOnRight() {
        match.getBall().reset(
            machine.getCenterX() + 100, 100
        );

        machine.decide(match);

        assertTrue(machine.getVelX() > 0);
    }

    @Test
    public void shouldMoveLeftWhenBallIsOnLeft() {
        match.getBall().reset(
            machine.getCenterX() - 100, 100
        );

        machine.decide(match);

        assertTrue(machine.getVelX() < 0);
    }

    @Test
    public void shouldNotMoveWhenBallIsAligned() {
        match.getBall().reset(
            machine.getCenterX(), 100
        );

        machine.decide(match);

        assertEquals(0.0, machine.getVelX(), 0.001);
    }

    @Test
    public void shouldActivateOffensivePower() {
        machine.chargePowerBar(Player.POWER_CHARGE_TIME);

        match.getBall().reset(
            machine.getCenterX() + 100, 100
        );

        machine.decide(match);

        assertTrue(machine.hasAura());
        assertEquals(0.0, machine.getPowerBar(), 0.001);
    }

    @Test
    public void shouldKickWhenBallIsNear() {
        match.getBall().reset(
            machine.getCenterX() + 30,
            machine.getCenterY()
        );

        machine.decide(match);

        assertTrue(machine.isKicking());
    }


    @Test
    public void shouldDefendLeftGoal() {
        machine.setStrategy(new DefensiveStrategy());

        machine.decide(match);

        assertTrue(machine.getVelX() < 0);
    }

    @Test
    public void shouldReturnToDefensivePosition() {
        machine.setStrategy(new DefensiveStrategy());

        machine.setPosX(10);

        machine.decide(match);

        assertTrue(machine.getVelX() > 0);
    }

    @Test
    public void shouldDefendRightGoal() {
        Machine rightMachine = new Machine(
            "Machine", new USA(),
            false, new DefensiveStrategy()
        );

        Player leftPlayer = new Player(
            "Human", new Cameroon(), true
        );

        Match rightMatch = new Match(
            leftPlayer, rightMachine, stadium,
            new GoalCondition(3)
        );

        rightMachine.decide(rightMatch);

        assertTrue(rightMachine.getVelX() > 0);
    }

    @Test
    public void shouldJumpWhenBallIsNearDefender() {
        machine.setStrategy(new DefensiveStrategy());

        match.getBall().reset(
            machine.getCenterX() + 30,
            machine.getCenterY()
        );

        machine.decide(match);

        assertTrue(machine.isJumping());
        assertTrue(machine.isKicking());
    }

    @Test
    public void shouldActivatePowerWhenGoalIsInDanger() {
        machine.setStrategy(new DefensiveStrategy());

        machine.chargePowerBar(Player.POWER_CHARGE_TIME);

        match.getBall().reset(20, 100);

        machine.decide(match);

        assertTrue(machine.hasAura());
    }


    @Test
    public void shouldDefendWhenBallIsNearOwnGoal() {
        machine.setStrategy(new ExpertStrategy());

        match.getBall().reset(20, 100);

        machine.decide(match);

        assertTrue(machine.getVelX() < 0);
    }

    @Test
    public void shouldAttackWhenBallIsFarFromOwnGoal() {
        machine.setStrategy(new ExpertStrategy());

        match.getBall().reset(
            stadium.getWidth() / 2, 100
        );

        machine.decide(match);

        assertTrue(machine.getVelX() > 0);
    }

    @Test
    public void shouldDefendAdvantageWhenWinning() {
        machine.setStrategy(new ExpertStrategy());

        machine.addGoal();

        match.getBall().reset(
            stadium.getWidth() * 5 / 12, 100
        );

        machine.decide(match);

        assertTrue(machine.getVelX() < 0);
    }

    @Test
    public void shouldJumpAndKickWhenBallIsNear() {
        machine.setStrategy(new ExpertStrategy());

        match.getBall().reset(
            machine.getCenterX() + 30,
            machine.getCenterY()
        );

        machine.decide(match);

        assertTrue(machine.isJumping());
        assertTrue(machine.isKicking());
    }

    @Test
    public void shouldActivateExpertPower() {
        machine.setStrategy(new ExpertStrategy());

        machine.chargePowerBar(Player.POWER_CHARGE_TIME);

        match.getBall().reset(
            machine.getCenterX() + 120, 100
        );

        machine.decide(match);

        assertTrue(machine.hasAura());
    }


    @Test
    public void shouldExecuteClumsyDecisions() {
        machine.setStrategy(new ClumsyStrategy());

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1800; i++) {
                machine.decide(match);
            }
        });
    }

    @Test
    public void shouldExecuteMachineDuringMatch() {
        int initialTime = match.getTimer();
        int initialX = machine.getPosX();

        match.getBall().reset(
            machine.getCenterX() + 100, 100
        );

        match.update();

        assertEquals(initialTime + 1, match.getTimer());
        assertTrue(machine.getPosX() > initialX);
    }
}
