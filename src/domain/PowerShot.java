package domain;

import java.io.Serializable;

/**
 * Represents a special shot that can be launched by a character during a match.
 */
public abstract class PowerShot implements Serializable{
    // Nombre identificador del tiro especial.
    private final String name;
    // Daño base que provoca el tiro al impactar al jugador rival.
    private final int damage;
    // Duración en segundos del efecto aplicado al rival.
    private final double duration;

    /**
     * Creates a new PowerShot with the given name, base damage, and effect duration.
     *
     * @param name     the name of the special shot
     * @param damage   the base damage dealt when hitting the rival
     * @param duration the duration of the effect in seconds
     */
    // Inicializa las propiedades base del tiro especial para las subclases que lo extienden.
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
    // Devuelve el nombre del tiro especial.
    public final String getName() { return name; }
    
    /**
     * Returns the base damage of the special shot.
     *
     * @return the base damage value
     */
    // Devuelve el daño base del tiro especial.
    public final int getDamage() { return damage; }
    
    /**
     * Returns the duration of the special shot effect.
     *
     * @return the effect duration in seconds
     */
    // Devuelve la duración del efecto en segundos.
    public final double getDuration() { return duration; }
    
    /**
     * Returns the horizontal attack direction of the player who fired the shot.
     *
     * @param ball the match ball holding the shooter reference
     * @return 1 if aiming to the right, or -1 if aiming to the left
     */
    // Obtiene la dirección horizontal de ataque (1 o -1) a partir del lanzador asociado al balón.
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
    // Define cómo se impulsa el balón y qué efecto especial se activa al lanzar el tiro.
    public abstract void launch(Ball ball);
    
    /**
     * Applies this power shot's special effect when it hits the opposing player.
     *
     * @param rival  the opposing player hit by the shot
     * @param source the character that launched the shot
     */
    // Aplica el efecto del tiro especial cuando el balón golpea al jugador rival.
    public abstract void onHit(Player rival, Character source);
}