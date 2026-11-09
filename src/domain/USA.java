package domain;

/**
 * Represents the USA character with an illusion special power.
 * Version one simplifies the power by reversing the rival's controls.
 */
public class USA extends Character {

    /**
     * Constructs a new USA character with default attributes.
     */
    public USA() {
        super("USA", 30, 2.5);
    }

    /**
     * Executes the character's power shot, modifying the ball speed and trajectory.
     * @param ball the ball object affected by the power shot
     */
    @Override
    public void powerShot(Ball ball) {
        ball.setVelX(14 * shotDirection(ball));
        ball.setVelY(-7);
    }

    /**
     * Applies the special confusion effect to the rival player, reversing their controls.
     * @param rival the rival player affected by the power
     */
    @Override
    public void applyEffect(Player rival) {
        rival.setEffect(this);
        rival.setSpeedFactor(-1);  
    }
}
