package domain;
/** Nigeria: ground-bound bouncing tornado, disables actions and launches rival. */
public class TornadoShot extends PowerShot {
    public TornadoShot() { 
        super("Tornado", 40, 3); 
    }
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(15 * direction(ball));
        ball.setVelY(8);
    }
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source); 
        rival.setCanMove(false);
        rival.setCanJump(false); 
        rival.setVelY(-14);
    }
}
