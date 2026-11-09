package domain;

/**
 * Represents the Nigeria character with a Tornado special power.
 */
public class Nigeria extends Character {

    /**
     * Constructs a new Nigeria character with default attributes.
     */
    public Nigeria() {
        super("Nigeria", 40, 3);
    }

    /**
     * Executes the character's power shot, launching the ball toward the ground to create a fast-bouncing shot.
     * @param ball the ball object affected by the power shot
     */
    @Override
    public void powerShot(Ball ball) {
        ball.setVelX(15 * shotDirection(ball));
        ball.setVelY(8);
    }

    /**
     * Applies the unconscious effect to the rival player, launching them into the air and restricting movement, jumping, and kicking.
     * @param rival the rival player affected by the power
     */
    @Override
    public void applyEffect(Player rival) {
        rival.setEffect(this);
        rival.setCanMove(false);
        rival.setCanJump(false);
        rival.setVelY(-14); 
    }
}
