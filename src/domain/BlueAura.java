package domain;

/**
 * Represents the South Korea power shot. It launches the ball in a fast
 * straight line and pushes the rival toward their own goal.
 */
public class BlueAura extends PowerShot {

    /**
     * Distance in pixels that the rival is pushed back on hit.
     */
    public static final int PUSH_DISTANCE = 150;

    /**
     * Creates a new BlueAura power shot with its default name, power, and duration.
     */
    public BlueAura() { 
        super("Aura Azul", 40, 1.5);
    }

    /**
     * Launches the ball horizontally at high speed with a slight upward angle.
     *
     * @param ball the match ball to launch
     */
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(22 * direction(ball)); 
        ball.setVelY(-2);
    }

    /**
     * Freezes the rival player and pushes them back toward their own goal.
     *
     * @param rival  the opposing player hit by the power shot
     * @param source the character that used the power shot
     */
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source);
        rival.setCanMove(false);
        rival.setPosX(rival.getPosX() - PUSH_DISTANCE * rival.getAttackDirection());
    }
}