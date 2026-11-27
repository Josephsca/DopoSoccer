package domain;

/**
 * Represents a goal-based winning condition where the first player to reach a
 * target number of goals wins the match.
 */
public class GoalCondition implements WinCondition {

    private int goalsToWin;

    /**
     * Creates a new GoalCondition with the given target number of goals.
     *
     * @param goalsToWin the number of goals needed to win the match
     */
    public GoalCondition(int goalsToWin) {
        this.goalsToWin = goalsToWin;
    }

    /**
     * Checks whether either player has reached the target number of goals.
     *
     * @param match the current match
     * @return true if the game is over, false otherwise
     */
    @Override
    public boolean isGameOver(Match match) {
        return match.getLeftPlayer().getScore() >= goalsToWin
            || match.getRightPlayer().getScore() >= goalsToWin;
    }

    /**
     * Returns the number of goals needed to win the match.
     *
     * @return the target number of goals
     */
    public int getGoalsToWin() { return goalsToWin; }
}