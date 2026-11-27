package domain;

import java.io.Serializable;

/**
 * Represents a special shot that can be launched by a character during a match.
 */
public abstract class PowerShot implements Serializable{
    private final String name;
    private final int damage;
    private final double duration;

    /**
     * Creates a new PowerShot with the given name, base damage, and effect duration.
     *
     * @param name     the name of the special shot
     * @param damage   the base damage dealt when hitting the rival
     * @param duration the duration of the effect in seconds
     */
    protected PowerShot(String name, int damage, double duration) {
        this.name = name;
        this.damage = damage;
        this.duration = duration;
    }

    /**
     * Returns the name of the special shot.
     *
     * @return the shot name
     */
    public final String getName() { return name; }
    
    /**
     * Returns the base damage of the special shot.
     *
     * @return the base damage value
     */
    public final int getDamage() { return damage; }
    
    /**
     * Returns the duration of the special shot effect.
     *
     * @return the effect duration in seconds
     */
    public final double getDuration() { return duration; }
    
    /**
     * Returns the horizontal attack direction of the player who fired the shot.
     *
     * @param ball the match ball holding the shooter reference
     * @return 1 if aiming to the right, or -1 if aiming to the left
     */
    protected final int direction(Ball ball) {
        Player shooter = ball.getPowerShooter();
        if (shooter == null) {
            return 1;
        } else {
            return shooter.getAttackDirection();
        }
    }
    
    /**
     * Sets the ball's movement and special behavior when this power shot is launched.
     *
     * @param ball the match ball
     */
    public abstract void launch(Ball ball);
    
    /**
     * Applies this power shot's special effect when it hits the opposing player.
     *
     * @param rival  the opposing player hit by the shot
     * @param source the character that launched the shot
     */
    public abstract void onHit(Player rival, Character source);
}