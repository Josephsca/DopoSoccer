package domain;

/**
 * Represents the Nigeria power shot. It launches a fast tornado ball toward
 * the ground that lifts the rival into the air and stops them from moving or
 * jumping.
 */
public class TornadoShot extends PowerShot {

    /**
     * Creates a new TornadoShot power shot with its default name, damage, and duration.
     */
    public TornadoShot() { 
        super("Tornado", 40, 3); 
    }

    /**
     * Launches the ball forward and downward to bounce along the ground.
     *
     * @param ball the match ball to launch
     */
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(15 * direction(ball));
        ball.setVelY(8);
    }

    /**
     * Applies the tornado effect to the rival player, disabling movement and
     * jumping and launching them upward.
     *
     * @param rival  the opposing player hit by the power shot
     * @param source the character that used the power shot
     */
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source); 
        rival.setCanMove(false);
        rival.setCanJump(false); 
        rival.setVelY(-14);
    }
}