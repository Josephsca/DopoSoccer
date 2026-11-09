package domain;

/**
 * Represents a country character. Each country has its own power shot
 * and a distinct effect on the rival.
 * To add a new character, simply create another subclass.
 */
public abstract class Character {

    private String country;
    private int shotDamage;
    private double effectDuration;

    /**
     * Constructs a new character with the specified country name, shot damage, and effect duration.
     * 
     * @param country the name of the country
     * @param shotDamage the base damage of the power shot
     * @param effectDuration the duration in seconds that the effect lasts on the rival
     */
    protected Character(String country, int shotDamage, double effectDuration) {
        this.country = country;
        this.shotDamage = shotDamage;
        this.effectDuration = effectDuration;
    }

    /**
     * Launches the ball with the power shot trajectory of this country.
     * The player who launches the shot is already stored in ball.getPowerShooter().
     * @param ball the ball object
     */
    public abstract void powerShot(Ball ball);

    /**
     * Applies the effect of this power shot to the rival player.
     * @param rival the rival player hit by the shot
     */
    public abstract void applyEffect(Player rival);

    /**
     * Determines the direction the shooter is attacking (1 for right, -1 for left).
     * @param ball the ball object used to check the shooter
     * @return the attack direction value
     */
    protected int shotDirection(Ball ball) {
        Player shooter = ball.getPowerShooter();
        return shooter == null ? 1 : shooter.getAttackDirection();
    }

    /**
     * Gets the country name of the character.
     * @return the country string
     */
    public String getCountry() { return country; }
    
    /**
     * Gets the shot damage of the character's power shot.
     * @return the shot damage value
     */
    public int getShotDamage() { return shotDamage; }
    
    /**
     * Gets the effect duration in seconds applied to the rival.
     * @return the effect duration value
     */
    public double getEffectDuration() { return effectDuration; }
}
