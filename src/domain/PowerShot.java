package domain;

import java.io.Serializable;

/**
 * Represents a special shot that can be used by a character.
 */
public abstract class PowerShot implements Serializable{
    private final String name;
    private final int damage;
    private final double duration;

    protected PowerShot(String name, int damage, double duration) {
        this.name = name;
        this.damage = damage;
        this.duration = duration;
    }

    /**
     * Gets the name of the shot.
     * @return the shot name
     */
    public final String getName() { return name; }
    
    /**
     * Gets the base damage of the shot.
     * @return the damage
     */
    public final int getDamage() { return damage; }
    
    /**
     * Gets the duration of the effect.
     * @return the duration in seconds
     */
    public final double getDuration() { return duration; }
    
    /**
     * Gets the direction of the shot, towards the rival goal.
     * @param ball the ball with the shooter
     * @return 1 to the right, -1 to the left
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
     * Gives the ball the movement of this shot.
     * @param ball the match ball
     */
    public abstract void launch(Ball ball);
    
    /**
     * Applies the effect of this shot when it hits the rival.
     * @param rival the player hit by the shot
     * @param source the character that launched the shot
     */
    public abstract void onHit(Player rival, Character source);
}
