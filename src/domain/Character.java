package domain;

import java.io.Serializable;

/** Country character: owns a replaceable special shot. */
public abstract class Character implements Serializable {
    private final String country;
    private PowerShot powerShot;

    protected Character(String country, PowerShot powerShot) {
        this.country = country;
        this.powerShot = powerShot;
    }
    
    public String getCountry() { return country; }
    
    public PowerShot getPowerShot() { return powerShot; }
    
    public int getShotDamage() { return powerShot.getDamage(); }
    
    public double getEffectDuration() { return powerShot.getDuration(); }
    
    public void powerShot(Ball ball) { powerShot.launch(ball); }
    
    public void applyEffect(Player rival) { powerShot.onHit(rival, this); }
}