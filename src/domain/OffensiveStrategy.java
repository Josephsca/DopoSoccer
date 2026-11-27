package domain;

/**
 * Represents an offensive strategy for a machine player that constantly chases
 * the ball, activates its power shot when ready, and kicks frequently.
 */
public class OffensiveStrategy implements MachineStrategy {

    // Identificador de versión para la serialización de la estrategia.
    private static final long serialVersionUID = 1L;

    // Contador cíclico de cuadros para controlar la frecuencia de los disparos.
    private int frame = 0;

    /**
     * Creates a new OffensiveStrategy with default initial values.
     */
    // Crea una instancia de estrategia ofensiva con su contador de cuadros en cero.
    public OffensiveStrategy() {
    }

    /**
     * Moves the machine player toward the ball, activates its power shot as
     * soon as the bar is full, and kicks when the ball is close.
     *
     * @param machine the machine player controlled by this strategy
     * @param match   the current match state
     */
    // Persigue el balón en el eje X, activa el tiro especial al llenarse la barra y patea si está cerca.
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