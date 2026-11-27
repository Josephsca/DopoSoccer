package domain;

import java.io.Serializable;

/**
 * Represents a playable country character that has a replaceable special shot.
 */
public abstract class Character implements Serializable {
    private final String country;
    private PowerShot powerShot;

    /**
     * Creates a new character with the given country name and special shot.
     *
     * @param country   the name of the country
     * @param powerShot the special shot assigned to this character
     */
    protected Character(String country, PowerShot powerShot) {
        this.country = country;
        this.powerShot = powerShot;
    }
    
    /**
     * Returns the country name of the character.
     *
     * @return the country name
     */
    public String getCountry() { return country; }
    
    /**
     * Returns the special shot of the character.
     *
     * @return the power shot
     */
    public PowerShot getPowerShot() { return powerShot; }
    
    /**
     * Returns the base damage of the character's power shot.
     *
     * @return the shot damage value
     */
    public int getShotDamage() { return powerShot.getDamage(); }
    
    /**
     * Returns how many seconds the power shot effect lasts on the rival.
     *
     * @return the effect duration in seconds
     */
    public double getEffectDuration() { return powerShot.getDuration(); }
    
    /**
     * Launches the character's power shot using the given ball.
     *
     * @param ball the match ball
     */
    public void powerShot(Ball ball) { powerShot.launch(ball); }
    
    /**
     * Applies the effect of the power shot to the rival player that was hit.
     *
     * @param rival the player hit by the shot
     */
    public void applyEffect(Player rival) { powerShot.onHit(rival, this); }
}