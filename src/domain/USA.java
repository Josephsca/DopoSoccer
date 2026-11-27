package domain;

/**
 * Represents the USA country character with its default IllusionShot special
 * power.
 */
public class USA extends Character {

    /**
     * Creates a new USA character with its default IllusionShot power shot.
     */
    // Inicializa el personaje de Estados Unidos asignándole el tiro especial IllusionShot por defecto.
    public USA() { 
        super("USA", new IllusionShot()); 
    }
}