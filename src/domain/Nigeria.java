package domain;

/**
 * Represents the Nigeria country character with its default TornadoShot special
 * power.
 */
public class Nigeria extends Character {

    /**
     * Creates a new Nigeria character with its default TornadoShot power shot.
     */
    public Nigeria() { super("Nigeria", new TornadoShot()); }
}