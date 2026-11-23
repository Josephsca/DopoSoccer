
package domain;

public class ExpertStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    public ExpertStrategy() {
    }

    @Override
    public void decide(Machine machine, Match match) {

        boolean isLeft = machine.isLeftSide();

        int width = match.getStadium().getWidth();

        int ballX = match.getBall().getCenterX();
        int machineX = machine.getCenterX();

        int distancia = Math.abs(ballX - machineX);

        Player rival = match.getRival(machine);
        boolean ganando = machine.getScore() > rival.getScore();

        // Decide según el estado del partido
        boolean defender;
        if (rival.isStunned()) {
            // el rival no se puede mover: es el mejor momento para atacar
            defender = false;
        } else if (ganando) {
            // va ganando: cuida la ventaja y defiende toda su mitad de cancha
            defender = isLeft ? ballX < width / 2 : ballX > width / 2;
        } else {
            // va empatando o perdiendo: solo defiende cuando el balón está muy cerca de su arco
            defender = isLeft ? ballX < width / 3 : ballX > width * 2 / 3;
        }

        if (defender) {

            int posicionDefensa = isLeft
                    ? width / 12
                    : width - width / 12 - machine.getWidth();

            if (machine.getPosX() < posicionDefensa - 5) {
                match.moveRight(machine);
            } else if (machine.getPosX() > posicionDefensa + 5) {
                match.moveLeft(machine);
            }

        } else {
            if (ballX < machineX - 10) {
                match.moveLeft(machine);
            } else if (ballX > machineX + 10) {
                match.moveRight(machine);
            }
        }

        if (distancia < 100 && frame == 0) {
            machine.jump();
        }

        boolean poderDisponible =
                machine.getPowerBar() >= Player.POWER_FULL;

        if (poderDisponible &&
                !machine.hasAura() &&
                !machine.isStunned() &&
                distancia < 150) {

            machine.activatePower();
        }

        if (distancia < 70 && frame % 10 == 0) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 30;
    }
}
