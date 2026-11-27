package domain;

/**
 * Represents a player controlled by artificial intelligence whose actions are
 * defined by a MachineStrategy.
 */
public class Machine extends Player {

    // Identificador de versión para la serialización del jugador controlado por IA.
    private static final long serialVersionUID = 1L;

    // Estrategia actual que determina las decisiones de movimiento y acción de la máquina.
    private MachineStrategy strategy;

    /**
     * Creates a new machine player with the given name, character, side, and strategy.
     *
     * @param name      the name of the machine player
     * @param character the selected country character
     * @param leftSide  true if the machine plays on the left side, false for the right side
     * @param strategy  the strategy that controls the machine behavior
     */
    // Inicializa el jugador máquina con su personaje, lado de la cancha y estrategia asignada.
    public Machine(String name, Character character, boolean leftSide, MachineStrategy strategy) {

        super(name, character, leftSide);
        this.strategy = strategy;
    }

    /**
     * Executes the next decision of the current strategy for this machine player.
     *
     * @param match the current match state
     */
    // Delega a la estrategia activa la decisión del jugador máquina en el estado actual del partido.
    public void decide(Match match) {
        strategy.decide(this, match);
    }

    /**
     * Sets a new strategy to control the machine behavior.
     *
     * @param strategy the new machine strategy
     */
    // Cambia la estrategia de decisión asignada a la máquina.
    public void setStrategy(MachineStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Returns the strategy that currently controls the machine.
     *
     * @return the current machine strategy
     */
    // Devuelve la estrategia que controla actualmente el comportamiento de la máquina.
    public MachineStrategy getStrategy() {
        return strategy;
    }
}