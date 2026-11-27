package domain;

/**
 * Represents a goal-based winning condition where the first player to reach a
 * target number of goals wins the match.
 */
public class GoalCondition implements WinCondition {

    // Cantidad de goles requerida para ganar el partido.
    private int goalsToWin;

    /**
     * Creates a new GoalCondition with the given target number of goals.
     *
     * @param goalsToWin the number of goals needed to win the match
     */
    // Inicializa la condición de victoria con la meta de goles indicada.
    public GoalCondition(int goalsToWin) {
        this.goalsToWin = goalsToWin;
    }

    /**
     * Checks whether either player has reached the target number of goals.
     *
     * @param match the current match
     * @return true if the game is over, false otherwise
     */
    // Comprueba si alguno de los jugadores alcanzó o superó la cantidad de goles para ganar.
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
    // Devuelve el número de goles necesario para ganar el partido.
    public int getGoalsToWin() { return goalsToWin; }
}