package domain;
import java.io.Serializable;
/**
 * Defines the rule that determines when a match ends.
 * To create a new match type, simply implement this interface.
 */
public interface WinCondition extends Serializable{

    /**
     * Checks whether the current match has ended based on the winning condition.
     * @param match the match instance to inspect
     * @return true if the match is over, false otherwise
     */
    public abstract boolean isGameOver(Match match);
}
