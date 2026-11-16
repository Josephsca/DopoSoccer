package domain;

public class IllusionShot extends PowerShot {
    public IllusionShot() { 
        super("Ilusión", 30, 2.5);
    }
    @Override public void launch(Ball ball) {
        ball.setVelX(14 * direction(ball)); 
        ball.setVelY(-7);
        ball.activateIllusion(getDuration());
    }
    @Override public void onHit(Player rival, Character source) {
        rival.setEffect(source);
    }
}
