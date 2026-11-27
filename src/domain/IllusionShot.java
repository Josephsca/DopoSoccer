package domain;

/**
 * Represents the USA power shot. It launches the ball in a curved path and
 * creates an illusion of 8 identical balls, where only one is real.
 */
public class IllusionShot extends PowerShot {

    /**
     * Creates a new IllusionShot power shot with its default name, damage, and duration.
     */
    public IllusionShot() { 
        super("Ilusión", 30, 2.5);
    }

    /**
     * Launches the ball forward and upward while activating the 8-ball illusion effect.
     *
     * @param ball the match ball to launch
     */
    @Override public void launch(Ball ball) {
        ball.setVelX(14 * direction(ball)); 
        ball.setVelY(-7);
        ball.activateIllusion(getDuration());
    }

    /**
     * Applies the illusion visual effect to the rival player hit by the ball.
     *
     * @param rival  the opposing player hit by the power shot
     * @param source the character that used the power shot
     */
    @Override public void onHit(Player rival, Character source) {
        rival.setEffect(source);
    }
}