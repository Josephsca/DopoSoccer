
package domain;

import java.io.Serializable;

/**
 * Defines the behavior of a machine player.
 * Each difficulty implements its own strategy.
 */
public interface MachineStrategy extends Serializable {

    /**
     * Decides the actions the machine performs during a frame.
     *
     * @param machine the machine player
     * @param match the current match
     */
    void decide(Machine machine, Match match);
}
