package domain;

/**
 * Represents the match ball. It is affected by gravity and can bounce off the
 * ground, boundaries, and goals.
 */
public class Ball extends Entity {

    /**
     * Energy kept after a bounce.
     */
    public static final double BOUNCE_LOSS = 0.72;

    /**
     * Speed reduction factor caused by air friction.
     */
    public static final double AIR_FRICTION = 0.97;

    private double radius;
    private double gravity;
    private Player powerShooter;
    private double illusionRemaining;
    private double illusionElapsed;

    /**
     * Creates a new ball with its center at the given coordinates.
     *
     * @param centerX the initial X coordinate of the ball center
     * @param centerY the initial Y coordinate of the ball center
     * @param radius  the radius of the ball
     * @param gravity the gravity force applied to the ball
     */
    public Ball(int centerX, int centerY, double radius, double gravity) {
        super((int) (centerX - radius), (int) (centerY - radius), (int) (radius * 2), (int) (radius * 2));
        this.radius = radius;
        this.gravity = gravity;
        this.powerShooter = null;
    }

    /**
     * Moves the ball using gravity and air friction, and updates any active
     * illusion timer.
     */
    @Override
    public void update() {
        if (illusionRemaining > 0) {
            illusionRemaining = Math.max(0, illusionRemaining - Match.DT);
            illusionElapsed += Match.DT;
        }
        velY += gravity;
        velX *= AIR_FRICTION;
        posX += (int) Math.round(velX);
        posY += (int) Math.round(velY);
    }

    /**
     * Bounces the ball off a surface using the surface normal direction.
     *
     * @param normalX the X direction of the surface (-1, 0, or 1)
     * @param normalY the Y direction of the surface (-1, 0, or 1)
     */
    public void bounce(double normalX, double normalY) {
        if (normalX != 0) {
            velX = Math.abs(velX) * BOUNCE_LOSS * Math.signum(normalX);
        }
        if (normalY != 0) {
            velY = Math.abs(velY) * BOUNCE_LOSS * Math.signum(normalY);
            if (Math.abs(velY) < 1.5) {
                velY = 0;
            }
        }
    }

    /**
     * Bounces the ball when it hits a field boundary and cancels any active
     * horizontal power shot.
     *
     * @param normalX the X direction of the boundary (-1, 0, or 1)
     * @param normalY the Y direction of the boundary (-1, 0, or 1)
     */
    @Override
    public void hitBoundary(double normalX, double normalY) {
        bounce(normalX, normalY);
        if (normalX != 0) {
            powerShooter = null;
        }
    }

    /**
     * Stops the ball and moves its center to a new position.
     *
     * @param x the new X coordinate of the ball center
     * @param y the new Y coordinate of the ball center
     */
    public void reset(int x, int y) {
        posX = (int) (x - radius);
        posY = (int) (y - radius);
        velX = 0;
        velY = 0;
        powerShooter = null;
        illusionRemaining = 0;
        illusionElapsed = 0;
    }

    /**
     * Starts the illusion effect where 8 balls are shown on screen.
     *
     * @param seconds the duration of the illusion in seconds
     */
    public void activateIllusion(double seconds) {
        illusionRemaining = seconds;
        illusionElapsed = 0;
    }

    /**
     * Checks if the ball illusion effect is currently active.
     *
     * @return true if the illusion is active, false otherwise
     */
    public boolean hasIllusion() {
        return illusionRemaining > 0;
    }

    /**
     * Returns the time passed since the illusion effect started.
     *
     * @return the elapsed time in seconds
     */
    public double getIllusionElapsed() {
        return illusionElapsed;
    }

    /**
     * Returns the radius of the ball.
     *
     * @return the radius value
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Returns the gravity force applied to the ball.
     *
     * @return the gravity value
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * Returns the player who used the active power shot on this ball.
     *
     * @return the player who made the power shot, or null if none is active
     */
    public Player getPowerShooter() {
        return powerShooter;
    }

    /**
     * Sets the player who used a power shot on this ball.
     *
     * @param powerShooter the player who made the power shot
     */
    public void setPowerShooter(Player powerShooter) {
        this.powerShooter = powerShooter;
    }
}