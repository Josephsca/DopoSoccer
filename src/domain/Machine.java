
package domain;

/**
 * Represents a player controlled by artificial intelligence.
 * Its behavior is defined by a MachineStrategy.
 */
public class Machine extends Player {

    private static final long serialVersionUID = 1L;

    private MachineStrategy strategy;

    /**
     * Creates a machine player with a specific strategy.
     *
     * @param name machine name
     * @param character selected country character
     * @param leftSide whether the machine plays on the left
     * @param strategy behavior used by the machine
     */
    public Machine(String name, Character character, boolean leftSide, MachineStrategy strategy) {

        super(name, character, leftSide);
        this.strategy = strategy;
    }

    /**
     * Executes the decisions of the current strategy.
     */
    public void decide(Match match) {
        strategy.decide(this, match);
    }

    /**
     * Changes the machine behavior.
     */
    public void setStrategy(MachineStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Gets the strategy that controls the machine.
     * @return the machine strategy
     */
    public MachineStrategy getStrategy() {
        return strategy;
    }
}
