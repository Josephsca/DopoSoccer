package domain;

public class OffensiveStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    @Override
    public void decide(Machine machine, Match match) {

        int ballX = match.getBall().getPosX();
        int machineX = machine.getPosX();

        if (frame < 20) {

            // Perseguir el balón
            if (ballX < machineX) {
                machine.moveLeft();
            } else if (ballX > machineX) {
                machine.moveRight();
            }
        }

        if (frame % 30 == 0) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 60;
    }
}