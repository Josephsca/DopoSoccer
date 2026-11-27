package domain;

import java.util.Random;

/**
 * Represents a basic strategy for a machine player that chooses random actions
 * every 60 frames.
 */
public class ClumsyStrategy implements MachineStrategy {

    // Identificador de versión para la serialización de la estrategia.
    private static final long serialVersionUID = 1L;

    // Generador aleatorio usado para elegir la acción de cada ciclo.
    private Random random = new Random();
    // Contador de cuadros dentro del ciclo actual de 60 cuadros.
    private int frame = 0;
    // Índice de la acción aleatoria elegida para el ciclo activo.
    private int movimiento = 0;

    /**
     * Creates a new ClumsyStrategy with default initial values.
     */
    // Crea una instancia de estrategia torpe con sus contadores en estado inicial.
    public ClumsyStrategy() {
    }
    
    /**
     * Selects and executes a random action for the machine player during the
     * current match frame.
     *
     * @param machine the machine player controlled by this strategy
     * @param match   the current match state
     */
    // Elige una acción al inicio del ciclo y la ejecuta según el cuadro actual del partido.
    @Override
    public void decide(Machine machine, Match match) {

        if (frame == 0) {
            movimiento = random.nextInt(5);
        }

        switch (movimiento) {
            case 0:
                if (frame < 20) {
                    match.moveLeft(machine);
                }
                break;

            case 1:
                if (frame < 20) {
                    match.moveRight(machine);
                }
                break;

            case 2:
                if (frame == 0) {
                    machine.jump();
                }
                break;

            case 3:
                if (frame == 0) {
                    machine.kick(
                        match.getRival(machine),
                        match.getBall()
                    );
                }
                break;

            case 4:
                if (frame == 0) {
                    machine.activatePower();
                }
                break;
        }

        frame = (frame + 1) % 60;
    }
}