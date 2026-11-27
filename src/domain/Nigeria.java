package domain;

/**
 * Represents the Nigeria country character with its default TornadoShot special
 * power.
 */
public class Nigeria extends Character {

    /**
     * Creates a new Nigeria character with its default TornadoShot power shot.
     */
    // Inicializa el personaje de Nigeria asignándole el tiro especial TornadoShot por defecto.
    public Nigeria() { super("Nigeria", new TornadoShot()); }
}