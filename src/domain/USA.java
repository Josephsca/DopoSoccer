package domain;

/**
 * Represents the USA country character with its default IllusionShot special
 * power.
 */
public class USA extends Character {

    /**
     * Creates a new USA character with its default IllusionShot power shot.
     */
    public USA() { 
        super("USA", new IllusionShot()); 
    }
}