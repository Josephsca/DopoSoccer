package domain;

/**
 * Represents the match ball affected by gravity and capable of bouncing off the ground, boundaries, and goals.
 */
public class Ball extends Entity {

    public static final double BOUNCE_LOSS = 0.72;  
    public static final double AIR_FRICTION = 0.97;

    private double radius;
    private double gravity;
    private Player powerShooter;

    /**
     * Constructs a new ball with its center positioned at (centerX, centerY).
     * @param centerX the initial X coordinate of the ball's center
     * @param centerY the initial Y coordinate of the ball's center
     * @param radius the radius of the ball
     * @param gravity the gravity force applied to the ball
     */
    public Ball(int centerX, int centerY, double radius, double gravity) {
        super((int) (centerX - radius), (int) (centerY - radius), (int) (radius * 2), (int) (radius * 2));
        this.radius = radius;
        this.gravity = gravity;
        this.powerShooter = null;
    }

    /**
     * Applies gravity and air friction to move the ball according to its velocity.
     */
    @Override
    public void update() {
        velY += gravity;
        velX *= AIR_FRICTION;
        posX += (int) Math.round(velX);
        posY += (int) Math.round(velY);
    }

    /**
     * Bounces the ball off a surface based on the surface normal direction.
     * @param normalX the X component of the surface normal direction (-1, 0, or 1)
     * @param normalY the Y component of the surface normal direction (-1, 0, or 1)
     */
    public void bounce(double normalX, double normalY) {
        if (normalX != 0) {
            velX = Math.abs(velX) * BOUNCE_LOSS * Math.signum(normalX);
        }
        if (normalY != 0) {
            velY = Math.abs(velY) * BOUNCE_LOSS * Math.signum(normalY);
            if (Math.abs(velY) < 1.5) velY = 0;  
        }
    }

    /**
     * Handles boundary collisions by making the ball bounce instead of stopping.
     * @param normalX the X component of the boundary normal direction (-1, 0, or 1)
     * @param normalY the Y component of the boundary normal direction (-1, 0, or 1)
     */
    @Override
    public void hitBoundary(double normalX, double normalY) {
        bounce(normalX, normalY);
    }

    /**
     * Resets the ball to a stationary state at a specific center position.
     * @param x the new X coordinate for the center of the ball
     * @param y the new Y coordinate for the center of the ball
     */
    public void reset(int x, int y) {
        posX = (int) (x - radius);
        posY = (int) (y - radius);
        velX = 0;
        velY = 0;
        powerShooter = null;
    }

    /**
     * Gets the radius of the ball.
     * @return the radius value
     */
    public double getRadius() { return radius; }
    
    /**
     * Gets the gravity value affecting the ball.
     * @return the gravity value
     */
    public double getGravity() { return gravity; }
    
    /**
     * Gets the player who performed the active power shot with this ball.
     * @return the Player object, or null if no power shot is active
     */
    public Player getPowerShooter() { return powerShooter; }
    
    /**
     * Sets the player responsible for the active power shot on this ball.
     * @param powerShooter the Player object who executed the power shot
     */
    public void setPowerShooter(Player powerShooter) { this.powerShooter = powerShooter; }
}
