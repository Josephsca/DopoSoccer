package domain;

/**
 * Represents a time-based match condition that lasts for a specific duration.
 * Enters sudden death if there is a tie when time runs out.
 */
public class TimeCondition implements WinCondition {

    private int duration;       
    private boolean suddenDeath;

    /**
     * Constructs a new TimeCondition with the specified duration in minutes.
     * @param minutes the duration of the match in minutes
     */
    public TimeCondition(int minutes) {
        this.duration = minutes * 60;
        this.suddenDeath = false;
    }

    /**
     * Checks whether the match has ended based on time and score conditions.
     * @param match the match instance to inspect
     * @return true if the match is over, false otherwise
     */
    @Override
    public boolean isGameOver(Match match) {
        if (match.getSeconds() < duration) return false;
        if (match.getLeftPlayer().getScore() != match.getRightPlayer().getScore()) return true;
        suddenDeath = true;  
        return false;
    }

    /**
     * Calculates the remaining seconds left in the match.
     * @param match the match instance to check
     * @return the remaining seconds, or 0 if time has expired
     */
    public int getRemainingSeconds(Match match) {
        return Math.max(0, duration - match.getSeconds());
    }

    /**
     * Gets the total duration of the match in seconds.
     * @return the match duration in seconds
     */
    public int getDuration() { return duration; }

    /**
     * Checks whether the match is currently in sudden death mode.
     * @return true if sudden death is active, false otherwise
     */
    public boolean isSuddenDeath() { return suddenDeath; }
}
