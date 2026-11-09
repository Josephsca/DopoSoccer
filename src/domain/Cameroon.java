package domain;

/**
 * Represents the Cameroon character with a Lightning special power.
 */
public class Cameroon extends Character {

    /**
     * Constructs a new Cameroon character with default attributes.
     */
    public Cameroon() {
        super("Cameroon", 60, 3);
    }

    /**
     * Executes the character's power shot, launching a straight and fast ball covered in electricity.
     * @param ball the ball object affected by the power shot
     */
    @Override
    public void powerShot(Ball ball) {
        ball.setVelX(18 * shotDirection(ball));
        ball.setVelY(-4);
    }

    /**
     * Applies the electrocuted effect to the rival player, preventing jumping and reducing movement speed to 50 percent.
     * @param rival the rival player affected by the power
     */
    @Override
    public void applyEffect(Player rival) {
        rival.setEffect(this);
        rival.setCanJump(false);
        rival.setSpeedFactor(0.5);
    }
}
