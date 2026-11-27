package domain;

/**
 * Represents the Cameroon power shot. It launches a fast electrical ball
 * that reduces the rival's speed by half and stops them from jumping.
 */
public class LightningShot extends PowerShot {

    /**
     * Creates a new LightningShot power shot with its default name, damage, and duration.
     */
    public LightningShot() { 
        super("Rayo", 60, 3); 
    }
    
    /**
     * Launches the ball horizontally at high speed with a slight upward angle.
     *
     * @param ball the match ball to launch
     */
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(18 * direction(ball)); 
        ball.setVelY(-4);
    }

    /**
     * Applies the electrical shock effect to the rival player, cutting their
     * speed in half and disabling jumping.
     *
     * @param rival  the opposing player hit by the power shot
     * @param source the character that used the power shot
     */
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source);
        rival.setCanJump(false); 
        rival.setSpeedFactor(0.5);
    }
}