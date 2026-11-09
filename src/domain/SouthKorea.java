package domain;

/**
 * Represents the SouthKorea character with a Blue Aura special power.
 */
public class SouthKorea extends Character {

    public static final int PUSH_DISTANCE = 150;

    /**
     * Constructs a new SouthKorea character with default attributes.
     */
    public SouthKorea() {
        super("SouthKorea", 40, 1.5);
    }

    /**
     * Executes the character's power shot, propelling the ball straight and fast horizontally.
     * @param ball the ball object affected by the power shot
     */
    @Override
    public void powerShot(Ball ball) {
        ball.setVelX(22 * shotDirection(ball));
        ball.setVelY(-2);
    }

    /**
     * Applies the push effect to the rival player, knocking them back toward their own goal and temporarily restricting movement.
     * @param rival the rival player affected by the power
     */
    @Override
    public void applyEffect(Player rival) {
        rival.setEffect(this);
        rival.setCanMove(false);
        rival.setPosX(rival.getPosX() - PUSH_DISTANCE * rival.getAttackDirection());
    }
}
