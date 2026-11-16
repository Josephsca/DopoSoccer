package domain;

/**
 * Represents a special shot that can be used by a character.
 */
public abstract class PowerShot {
    private final String name;
    private final int damage;
    private final double duration;

    protected PowerShot(String name, int damage, double duration) {
        this.name = name;
        this.damage = damage;
        this.duration = duration;
    }

    public final String getName() { return name; }
    
    public final int getDamage() { return damage; }
    
    public final double getDuration() { return duration; }
    
    protected final int direction(Ball ball) {
        Player shooter = ball.getPowerShooter();
        if (shooter == null) {
            return 1;
        } else {
            return shooter.getAttackDirection();
        }
    }
    
    public abstract void launch(Ball ball);
    
    public abstract void onHit(Player rival, Character source);
}
