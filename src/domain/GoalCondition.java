package domain;

/**
 * Represents a goal-based match condition where the first player to reach a specific score wins.
 */
public class GoalCondition implements WinCondition {

    private int goalsToWin;

    /**
     * Constructs a new GoalCondition with the specified goal target.
     * @param goalsToWin the number of goals required to win the match
     */
    public GoalCondition(int goalsToWin) {
        this.goalsToWin = goalsToWin;
    }

    /**
     * Checks if the game is over based on the goal condition.
     * @param match the current match
     * @return true if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver(Match match) {
        return match.getLeftPlayer().getScore() >= goalsToWin
            || match.getRightPlayer().getScore() >= goalsToWin;
    }

    /**
     * Gets the number of goals required to win the match.
     * @return the goal target value
     */
    public int getGoalsToWin() { return goalsToWin; }
}
