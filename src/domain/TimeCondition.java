package domain;

/**
 * Represents a time-based winning condition that lasts for a set number of
 * minutes and enters sudden death if the score is tied when time expires.
 */
public class TimeCondition implements WinCondition {

    private int duration;       
    private boolean suddenDeath;

    /**
     * Creates a new TimeCondition with the given match duration in minutes.
     *
     * @param minutes the match duration in minutes
     */
    public TimeCondition(int minutes) {
        this.duration = minutes * 60;
        this.suddenDeath = false;
    }

    /**
     * Checks whether the match has ended because time expired with a winner,
     * activating sudden death if the players are tied.
     *
     * @param match the current match
     * @return true if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver(Match match) {
        if (match.getSeconds() < duration) return false;
        if (match.getLeftPlayer().getScore() != match.getRightPlayer().getScore()) return true;
        suddenDeath = true;  
        return false;
    }

    /**
     * Returns the remaining time in seconds before the match duration ends.
     *
     * @param match the current match
     * @return the remaining seconds, or 0 if time has run out
     */
    public int getRemainingSeconds(Match match) {
        return Math.max(0, duration - match.getSeconds());
    }

    /**
     * Returns the total match duration in seconds.
     *
     * @return the duration in seconds
     */
    public int getDuration() { return duration; }

    /**
     * Checks whether the match is currently in sudden death mode.
     *
     * @return true if sudden death is active, false otherwise
     */
    public boolean isSuddenDeath() { return suddenDeath; }
}