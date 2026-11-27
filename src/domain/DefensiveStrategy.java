package domain;

/**
 * Represents a defensive strategy for a machine player that stays near its own
 * goal and clears the ball when it gets close.
 */
public class DefensiveStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    /**
     * Creates a new DefensiveStrategy with default initial values.
     */
    public DefensiveStrategy() {
    }
    
    /**
     * Moves the machine player near its goal, jumps or kicks when the ball is
     * close, and activates its power shot when the ball enters the danger zone.
     *
     * @param machine the machine player controlled by this strategy
     * @param match   the current match state
     */
    @Override
    public void decide(Machine machine, Match match) {

        boolean isLeft = machine.isLeftSide();

        int width = match.getStadium().getWidth();
        int margen = width / 12;

        int ballX = match.getBall().getCenterX();
        int machineX = machine.getCenterX();

        int posicionArco = isLeft
                ? margen
                : width - margen - machine.getWidth();

        if (machine.getPosX() < posicionArco - 5) {
            match.moveRight(machine);
        } else if (machine.getPosX() > posicionArco + 5) {
            match.moveLeft(machine);
        }

        int distancia = Math.abs(ballX - machineX);

        if (distancia < 80 && frame == 0) {
            machine.jump();
        }

        boolean peligro = isLeft
                ? ballX < width / 3
                : ballX > width * 2 / 3;

        if (peligro &&
                machine.getPowerBar() >= Player.POWER_FULL) {

            machine.activatePower();
        }

        if (distancia < 70 && frame % 15 == 0) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 60;
    }
}