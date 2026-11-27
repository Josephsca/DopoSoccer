package domain;

import java.io.Serializable;

/**
 * Defines the behavior of a machine player. Each difficulty level implements
 * its own strategy.
 */
public interface MachineStrategy extends Serializable {

    /**
     * Decides and performs the actions of the machine player during a single
     * match frame.
     *
     * @param machine the machine player to control
     * @param match   the current match state
     */
    // Determina y ejecuta las acciones del jugador máquina durante un cuadro del partido.
    public abstract void decide(Machine machine, Match match);
}