
package domain;

public class OffensiveStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    public OffensiveStrategy() {
    }
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
