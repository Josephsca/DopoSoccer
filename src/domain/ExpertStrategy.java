
package domain;

public class ExpertStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    @Override
    public void decide(Machine machine, Match match) {

        boolean isLeft = match.getLeftPlayer() == machine;

        int width = match.getStadium().getWidth();
        int ballX = match.getBall().getPosX();
        int machineX = machine.getPosX();

        int distancia = Math.abs(ballX - machineX);

        boolean defender = isLeft ? ballX < width / 3 : ballX > width * 2 / 3;

        if (defender) {

            int posicionDefensa = isLeft
                    ? width / 12
                    : width - width / 12 - machine.getWidth();

            if (machineX < posicionDefensa - 5) {
                machine.moveRight();
            } else if (machineX > posicionDefensa + 5) {
                machine.moveLeft();
            }

        } else {

            if (ballX < machineX - 10) {
                machine.moveLeft();
            } else if (ballX > machineX + 10) {
                machine.moveRight();
            }
        }

        if (distancia < 100 && frame == 0) {
            machine.jump();
        }

        if (distancia < 70 && frame == 15) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 30;
    }
}
