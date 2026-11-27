package domain;
import java.io.Serializable;

/**
 * Defines the rule that decides when a match ends. Implement this interface to
 * add a new match type.
 */
public interface WinCondition extends Serializable{

    /**
     * Checks whether the match is over according to this winning condition.
     *
     * @param match the current match
     * @return true if the match has ended, false otherwise
     */
    public abstract boolean isGameOver(Match match);
}