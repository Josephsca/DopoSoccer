package domain;
/** Cameroon: electrical shot, halves speed and prevents jumping. */
public class LightningShot extends PowerShot {
    public LightningShot() { 
        super("Rayo", 60, 3); 
    }
    
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(18 * direction(ball)); 
        ball.setVelY(-4);
    }
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source);
        rival.setCanJump(false); 
        rival.setSpeedFactor(0.5);
    }
}
