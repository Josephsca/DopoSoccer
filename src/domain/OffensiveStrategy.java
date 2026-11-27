package domain;

/**
 * Represents an offensive strategy for a machine player that constantly chases
 * the ball, activates its power shot when ready, and kicks frequently.
 */
public class OffensiveStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    /**
     * Creates a new OffensiveStrategy with default initial values.
     */
    public OffensiveStrategy() {
    }

    /**
     * Moves the machine player toward the ball, activates its power shot as
     * soon as the bar is full, and kicks when the ball is close.
     *
     * @param machine the machine player controlled by this strategy
     * @param match   the current match state
     */
    @Override
    public void decide(Machine machine, Match match) {

        int ballX = match.getBall().getCenterX();
        int machineX = machine.getCenterX();

        int distancia = Math.abs(ballX - machineX);

        if (ballX < machineX - 10) {
            match.moveLeft(machine);
        } else if (ballX > machineX + 10) {
            match.moveRight(machine);
        }

        if (machine.getPowerBar() >= Player.POWER_FULL) {
            machine.activatePower();
        }

        if (distancia < 80 && frame % 10 == 0) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 60;
    }
}