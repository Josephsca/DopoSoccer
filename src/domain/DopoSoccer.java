package domain;

/**
 * Clase principal del dominio. La interfaz gráfica solo habla con esta clase.
 * Los jugadores se identifican como 1 (izquierda) y 2 (derecha).
 */
public class DopoSoccer {

    public static final String[] COUNTRIES = {"SouthKorea", "Cameroon", "Nigeria", "USA"};
    public static final String TIME = "TIME";
    public static final String GOALS = "GOALS";
    private boolean sound;
    private Match currentMatch;

    /**
     * Constructs a new DopoSoccer game instance with default settings.
     */
    public DopoSoccer() {
        sound = true;
        currentMatch = null;
    }

    /**
     * Creates a new Player versus Player match.
     * @param name1 the name of player 1
     * @param country1 the country of player 1 (see COUNTRIES)
     * @param name2 the name of player 2
     * @param country2 the country of player 2
     * @param matchType the type of match, either TIME or GOALS
     * @param limit the time limit in minutes or goal target to win
     * @param resolution the screen resolution size such as SMALL, MEDIUM, or LARGE
     * @throws DopoSoccerException if any provided parameter or data is invalid
     */
    public void startNewMatch(String name1, String country1, String name2, String country2, String matchType, int limit, String resolution) throws DopoSoccerException {
        Stadium stadium = new Stadium(resolution);
        WinCondition condition = createWinCondition(matchType, limit);
        Player player1 = new Player(defaultName(name1, "J1"), createCharacter(country1), true);
        Player player2 = new Player(defaultName(name2, "J2"), createCharacter(country2), false);
        currentMatch = new Match(player1, player2, stadium, condition);
    }

    /**
     * Pauses the currently active match.
     */
    public void pauseMatch() {
        if (currentMatch != null) currentMatch.pause();
    }

    /**
     * Resumes the currently paused match.
     */
    public void resumeMatch() {
        if (currentMatch != null) currentMatch.resume();
    }

    /**
     * Finishes the currently active match.
     */
    public void finishMatch() {
        if (currentMatch != null) currentMatch.finish();
    }

    /**
     * Saves the current game state to a file.
     * @param filePath the path where the game file will be saved
     * @return true if the game was successfully saved, false otherwise
     */
    public boolean saveGame(String filePath) {
        return false;
    }

    /**
     * Loads a saved game state from a file.
     * @param filePath the path of the saved game file to load
     */
    public void loadGame(String filePath) {
    }

    /**
     * Gets the currently active match. 
     * @return the current Match instance, or null if no match is active
     */
    public Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Updates the simulation frame for the current match.
     */
    public void update() {
        if (currentMatch != null) currentMatch.update();
    }

    /**
     * Moves the specified player to the left.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void moveLeft(int player) {
        if (getPlayer(player) != null) getPlayer(player).moveLeft();
    }

    /**
     * Moves the specified player to the right.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void moveRight(int player) {
        if (getPlayer(player) != null) getPlayer(player).moveRight();
    }

    /**
     * Makes the specified player jump.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void jump(int player) {
        if (getPlayer(player) != null) getPlayer(player).jump();
    }

    /**
     * Makes the specified player kick the ball toward the rival.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void kick(int player) {
        Player p = getPlayer(player);
        if (p != null) p.kick(currentMatch.getRival(p), currentMatch.getBall());
    }

    /**
     * Activates the special power for the specified player.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void activatePower(int player) {
        if (getPlayer(player) != null) getPlayer(player).activatePower();
    }

    /**
     * Toggles the game sound settings on or off.
     */
    public void toggleSound() {
        sound = !sound;
    }

    /**
     * Checks whether the game sound is enabled.
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSound() {
        return sound;
    }

    /**
     * Creates a character instance based on the specified country.
     * @param country the name of the country representing the character
     * @return the corresponding Character instance
     * @throws DopoSoccerException if the country name is invalid or null
     */
    private Character createCharacter(String country) throws DopoSoccerException {
        if (country == null){
            throw new DopoSoccerException(DopoSoccerException.INVALID_CHARACTER);
        }
        switch (country.toLowerCase()) {
            case "southkorea": return new SouthKorea();
            case "cameroon": return new Cameroon();
            case "nigeria": return new Nigeria();
            case "usa": return new USA();
            default: throw new DopoSoccerException(DopoSoccerException.INVALID_CHARACTER);
        }
    }

    /**
     * Creates the winning condition rule according to the match type and limit.
     * @param matchType the rule type, either TIME or GOALS
     * @param limit the numerical limit for time or goals
     * @return the corresponding WinCondition instance
     * @throws DopoSoccerException if the match type is invalid or the limit is non-positive
     */
    private WinCondition createWinCondition(String matchType, int limit) throws DopoSoccerException {
        if (matchType == null || limit <= 0){ 
            throw new DopoSoccerException(DopoSoccerException.INVALID_MATCH_TYPE);
        }
        if (matchType.equals(TIME)) return new TimeCondition(limit);
        if (matchType.equals(GOALS)) return new GoalCondition(limit);
        throw new DopoSoccerException(DopoSoccerException.INVALID_MATCH_TYPE);
    }

    /**
     * Retrieves a specific player instance from the current match.
     * @param player the player number (1 or 2)
     * @return the Player object, or null if not found or no match exists
     */
    private Player getPlayer(int player) {
        if (currentMatch == null) return null;
        if (player == 1) return currentMatch.getLeftPlayer();
        if (player == 2) return currentMatch.getRightPlayer();
        return null;
    }

    /**
     * Returns the trimmed name or a default value if the name is blank or null.
     * @param name the original player name input
     * @param defaultValue the fallback name to use if the input is empty
     * @return the valid trimmed name or the default value
     */
    private String defaultName(String name, String defaultValue) {
        if (name == null || name.trim().isEmpty()) {
            return defaultValue;
        }
        return name.trim();
    }
}
