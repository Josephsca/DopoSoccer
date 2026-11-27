package domain;

/**
 * Represents the South Korea country character with its default BlueAura
 * special power.
 */
public class SouthKorea extends Character {

    /**
     * Creates a new SouthKorea character with its default BlueAura power shot.
     */
    // Inicializa el personaje de Corea del Sur asignándole el tiro especial BlueAura por defecto.
    public SouthKorea() { 
        super("SouthKorea", new BlueAura()); 
    }
}