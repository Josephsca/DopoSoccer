package domain;

/** South Korea: straight fast shot that pushes the rival towards his own goal. */
public class BlueAura extends PowerShot {
    public static final int PUSH_DISTANCE = 150;
    public BlueAura() { 
        super("Aura Azul", 40, 1.5);
    }
    @Override 
    public void launch(Ball ball) {
        ball.setVelX(22 * direction(ball)); 
        ball.setVelY(-2);
    }
    @Override 
    public void onHit(Player rival, Character source) {
        rival.setEffect(source);
        rival.setCanMove(false);
        rival.setPosX(rival.getPosX() - PUSH_DISTANCE * rival.getAttackDirection());
    }
}
