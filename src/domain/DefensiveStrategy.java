package domain;

public class DefensiveStrategy implements MachineStrategy {

    private static final long serialVersionUID = 1L;

    private int frame = 0;

    @Override
    public void decide(Machine machine, Match match) {

        boolean isLeft = match.getLeftPlayer() == machine;

        int width = match.getStadium().getWidth();

        int margen = width / 12;

        int posicionArco = isLeft ? margen : width - margen - machine.getWidth();

        if (machine.getPosX() < posicionArco - 5) {
            machine.moveRight();
        } else if (machine.getPosX() > posicionArco + 5) {
            machine.moveLeft();
        }

        int distancia = Math.abs(
            match.getBall().getPosX() - machine.getPosX()
        );

        if (distancia < 80 && frame == 0) {
            machine.jump();
        }

        if (distancia < 70 && frame == 30) {
            machine.kick(
                match.getRival(machine),
                match.getBall()
            );
        }

        frame = (frame + 1) % 60;
    }
}