package domain;

/**
 * Represents the Cameroon character with its default special shot.
 */
public class Cameroon extends Character {

    /**
     * Creates a new Cameroon character with its name and default power shot.
     */
    public Cameroon() { 
        super("Cameroon", new LightningShot()); 
    }
}