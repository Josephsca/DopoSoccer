package domain;

import java.io.Serializable;

/**
 * Represents a playable country character that has a replaceable special shot.
 */
public abstract class Character implements Serializable {
    // Nombre del país que identifica al personaje.
    private final String country;
    // Tiro especial asignado actualmente al personaje.
    private PowerShot powerShot;

    /**
     * Creates a new character with the given country name and special shot.
     *
     * @param country   the name of the country
     * @param powerShot the special shot assigned to this character
     */
    // Inicializa el personaje con su nombre de país y su tiro especial por defecto.
    protected Character(String country, PowerShot powerShot) {
        this.country = country;
        this.powerShot = powerShot;
    }
    
    /**
     * Returns the country name of the character.
     *
     * @return the country name
     */
    // Devuelve el nombre del país del personaje.
    public String getCountry() { return country; }
    
    /**
     * Returns the special shot of the character.
     *
     * @return the power shot
     */
    // Devuelve el tiro especial configurado para el personaje.
    public PowerShot getPowerShot() { return powerShot; }
    
    /**
     * Returns the base damage of the character's power shot.
     *
     * @return the shot damage value
     */
    // Devuelve el daño base del tiro especial del personaje.
    public int getShotDamage() { return powerShot.getDamage(); }
    
    /**
     * Returns how many seconds the power shot effect lasts on the rival.
     *
     * @return the effect duration in seconds
     */
    // Devuelve la duración en segundos del efecto aplicado al rival.
    public double getEffectDuration() { return powerShot.getDuration(); }
    
    /**
     * Launches the character's power shot using the given ball.
     *
     * @param ball the match ball
     */
    // Ejecuta el lanzamiento del tiro especial sobre el balón del partido.
    public void powerShot(Ball ball) { powerShot.launch(ball); }
    
    /**
     * Applies the effect of the power shot to the rival player that was hit.
     *
     * @param rival the player hit by the shot
     */
    // Aplica sobre el jugador rival el efecto del tiro especial de este personaje.
    public void applyEffect(Player rival) { powerShot.onHit(rival, this); }
}