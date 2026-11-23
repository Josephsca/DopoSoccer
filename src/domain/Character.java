package domain;

import java.io.Serializable;

/** Country character: owns a replaceable special shot. */
public abstract class Character implements Serializable {
    private final String country;
    private PowerShot powerShot;

    protected Character(String country, PowerShot powerShot) {
        this.country = country;
        this.powerShot = powerShot;
    }
    
    /**
     * Gets the country of the character.
     * @return the country name
     */
    public String getCountry() { return country; }
    
    /**
     * Gets the special shot of the character.
     * @return the power shot
     */
    public PowerShot getPowerShot() { return powerShot; }
    
    /**
     * Gets the base damage of the power shot (DañoTiro in the statement).
     * @return the shot damage
     */
    public int getShotDamage() { return powerShot.getDamage(); }
    
    /**
     * Gets how many seconds the effect of the power lasts on the rival.
     * @return the effect duration in seconds
     */
    public double getEffectDuration() { return powerShot.getDuration(); }
    
    /**
     * Launches the power shot with the ball.
     * @param ball the match ball
     */
    public void powerShot(Ball ball) { powerShot.launch(ball); }
    
    /**
     * Applies the effect of the power shot to the rival that was hit.
     * @param rival the player that received the shot
     */
    public void applyEffect(Player rival) { powerShot.onHit(rival, this); }
}