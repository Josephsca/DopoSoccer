package domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Closeable;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase principal del dominio. La interfaz gráfica solo habla con esta clase.
 * Los jugadores se identifican como 1 (izquierda) y 2 (derecha).
 */
public class DopoSoccer {

    public static final String[] COUNTRIES = {"SouthKorea", "Cameroon", "Nigeria", "USA"};
    public static final String TIME = "TIME";
    public static final String GOALS = "GOALS";
    public static final String PROGRESS_FILE = "progreso.dat";
    private boolean sound;
    private Match currentMatch;
    private boolean progressSaved;
    /** Progreso de cada jugador humano (puntos y estadísticas), buscado por su nombre. */
    private HashMap<String, Player> profiles;

    /**
     * Constructs a new DopoSoccer game instance with default settings,
     * loads the saved progress of the players and creates the default match.
     */
    public DopoSoccer() {
        sound = true;
        progressSaved = false;
        profiles = new HashMap<>();
        loadProgress();
        createDefaultMatch();
    }

    /**
     * Creates the match that exists as soon as the game starts (J1 vs J2, by goals).
     * It stays paused until the players start a real match, so DopoSoccer always has a match.
     */
    private void createDefaultMatch() {
        try {
            startNewMatch("J1", COUNTRIES[0], "J2", COUNTRIES[1], GOALS, 3, Stadium.MEDIUM);
            currentMatch.pause();
        } catch (DopoSoccerException e) {
            Log.record(e);
        }
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
     * @throws DopoSoccerException if any provided parameter is invalid or both players have the same name
     */
    public void startNewMatch(String name1, String country1, String name2, String country2, String matchType, int limit, String resolution) throws DopoSoccerException {
        if (defaultName(name1, "J1").equals(defaultName(name2, "J2"))) {
            // el progreso se guarda por nombre: si se llamaran igual, uno borraría el del otro
            throw new DopoSoccerException(DopoSoccerException.SAME_NAMES);
        }
        Stadium stadium = new Stadium(resolution);
        WinCondition condition = createWinCondition(matchType, limit);
        Player player1 = new Player(defaultName(name1, "J1"), createCharacter(country1), true);
        Player player2 = new Player(defaultName(name2, "J2"), createCharacter(country2), false);
        applyProgress(player1);
        applyProgress(player2);
        currentMatch = new Match(player1, player2, stadium, condition);
        progressSaved = false;
    }

    /**
     * Creates a Player versus Machine match.
     *
     * @param playerName human player's name
     * @param playerCountry human player's country
     * @param machineCountry machine's country
     * @param matchType winning condition type
     * @param limit time or goal limit
     * @param resolution stadium size
     * @param machineLeft true if the machine plays on the left side
     * @param strategy profile of the machine: "Torpe", "Ofensiva", "Defensiva" or "Experta"
     * @throws DopoSoccerException if the configuration is invalid
     */
    public void startMachineMatch(String playerName, String playerCountry, String machineCountry, String matchType, int limit, String resolution, boolean machineLeft, String strategy) throws DopoSoccerException {

        Stadium stadium = new Stadium(resolution);
        WinCondition condition = createWinCondition(matchType, limit);

        MachineStrategy machineStrategy;

        if (strategy == null) {
            throw new DopoSoccerException(DopoSoccerException.INVALID_STRATEGY + "ninguna");
        }
        switch (strategy) {
            case "Torpe":
                machineStrategy = new ClumsyStrategy();
                break;

            case "Ofensiva":
                machineStrategy = new OffensiveStrategy();
                break;

            case "Defensiva":
                machineStrategy = new DefensiveStrategy();
                break;

            case "Experta":
                machineStrategy = new ExpertStrategy();
                break;

            default:
                throw new DopoSoccerException(
                    DopoSoccerException.INVALID_STRATEGY + strategy
                );
        }

        Player human = new Player(
            defaultName(playerName, "Jugador"),
            createCharacter(playerCountry),
            !machineLeft
        );
        applyProgress(human);

        Machine machine = new Machine(
            "Máquina " + strategy,
            createCharacter(machineCountry),
            machineLeft,
            machineStrategy
        );

        if (machineLeft) {
            currentMatch = new Match(
                machine, human, stadium, condition
            );
        } else {
            currentMatch = new Match(
                human, machine, stadium, condition
            );
        }
        progressSaved = false;
    }


    /**
     * Pauses the currently active match.
     */
    public void pauseMatch() {
        currentMatch.pause();
    }

    /**
     * Resumes the currently paused match.
     */
    public void resumeMatch() {
        currentMatch.resume();
    }

    /**
     * Finishes the currently active match.
     */
    public void finishMatch() {
        currentMatch.finish();
        saveProgressIfFinished();
    }

    /**
     * Saves the current match in a file so it can be opened later.
     * @param filePath the path of the file
     * @return true if the match was saved
     * @throws DopoSoccerException if the file cannot be written
     */
    public boolean saveGame(String filePath) throws DopoSoccerException {
        FileOutputStream fileOutput = null;
        ObjectOutputStream output = null;

        try {
            fileOutput = new FileOutputStream(filePath);
            output = new ObjectOutputStream(fileOutput);
            output.writeObject(currentMatch);
            return true;

        } catch (IOException e) {
            throw new DopoSoccerException(DopoSoccerException.SAVE_ERROR);

        } finally {
            // si el ObjectOutputStream no se alcanzó a crear, igual hay que cerrar el archivo
            if (output != null) {
                close(output);
            } else {
                close(fileOutput);
            }
        }
    }

    /**
     * Opens a match saved in a file and makes it the current match.
     * If the file is not valid, the current match does not change.
     * @param filePath the path of the file
     * @throws DopoSoccerException if the file does not exist or is not a saved match
     */
    public void loadGame(String filePath) throws DopoSoccerException {
        FileInputStream fileInput = null;
        ObjectInputStream input = null;

        try {
            fileInput = new FileInputStream(filePath);
            input = new ObjectInputStream(fileInput);

            Object data = input.readObject();

            if (!(data instanceof Match)) {
                throw new DopoSoccerException(DopoSoccerException.INVALID_LOAD_FILE);
            }

            currentMatch = (Match) data;
            progressSaved = false;

        } catch (IOException | ClassNotFoundException e) {
            throw new DopoSoccerException(DopoSoccerException.LOAD_ERROR_MESSAGE + e.getMessage());

        } finally {
            // si el ObjectInputStream no se alcanzó a crear, igual hay que cerrar el archivo
            if (input != null) {
                close(input);
            } else {
                close(fileInput);
            }
        }
    }



    /**
     * Gets the currently active match. 
     * @return the current Match instance (there is always one)
     */
    public Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Updates the simulation frame for the current match.
     */
    public void update() {
        currentMatch.update();
        saveProgressIfFinished();
    }
    
    /**
     * Moves the specified human player to the left.
     * @param player the identifier of the player
     */
    public void moveLeft(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            currentMatch.moveLeft(p);
        }
    }

    /**
     * Moves the specified human player to the right.
     * @param player the identifier of the player
     */
    public void moveRight(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            currentMatch.moveRight(p);
        }
    }

    /**
     * Makes the specified human player jump.
     * @param player the identifier of the player
     */
    public void jump(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.jump();
        }
    }

    /**
     * Makes the specified human player kick.
     * @param player the identifier of the player
     */
    public void kick(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.kick(currentMatch.getRival(p), currentMatch.getBall());
        }
    }

    /**
     * Activates the special power for the specified player.
     * @param player the identifier of the player (1 for left, 2 for right)
     */
    public void activatePower(int player) {
        Player p = getPlayer(player);
        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.activatePower();
        }
    }

    /**
     * Gets the saved progress of a player. If the player has never played, a new
     * profile is created with 0 points and all the stats in 1.
     * @param name the name of the player
     * @return the player with the saved points and stats
     * @throws DopoSoccerException if the name is empty
     */
    public Player getProfile(String name) throws DopoSoccerException {
        if (name == null || name.trim().isEmpty()) {
            throw new DopoSoccerException(DopoSoccerException.INVALID_NAME);
        }
        String key = name.trim();
        if (!profiles.containsKey(key)) {
            // el perfil solo guarda puntos y estadísticas, por eso no necesita personaje
            profiles.put(key, new Player(key, null, true));
        }
        return profiles.get(key);
    }

    /**
     * Gets the names of the players that have saved progress, in alphabetical order.
     * @return the names of the saved players (empty if nobody has played yet)
     */
    public String[] getSavedPlayers() {
        String[] names = profiles.keySet().toArray(new String[0]);
        Arrays.sort(names);
        return names;
    }

    /**
     * Upgrades one stat of a player using his saved points, and saves the progress.
     * @param name the name of the player
     * @param stat the stat to upgrade ("speed", "jump", "kick" or "survival")
     * @throws DopoSoccerException if the name or the stat are invalid, or there are not enough points
     */
    public void upgradeStat(String name, String stat) throws DopoSoccerException {
        Player profile = getProfile(name);
        profile.upgradeStat(stat);
        saveProgress();
    }

    /**
     * Saves the progress of all the players in the progress file.
     * @throws DopoSoccerException if the file cannot be written
     */
    public void saveProgress() throws DopoSoccerException {
        FileOutputStream fileOutput = null;
        ObjectOutputStream output = null;
        try {
            fileOutput = new FileOutputStream(PROGRESS_FILE);
            output = new ObjectOutputStream(fileOutput);
            output.writeObject(profiles);
        } catch (IOException e) {
            throw new DopoSoccerException(DopoSoccerException.PROGRESS_ERROR);
        } finally {
            // si el ObjectOutputStream no se alcanzó a crear, igual hay que cerrar el archivo
            if (output != null) {
                close(output);
            } else {
                close(fileOutput);
            }
        }
    }

    /**
     * Loads the progress of the players. If the file does not exist yet
     * (first time playing), the game starts with no saved progress.
     */
    @SuppressWarnings("unchecked")
    private void loadProgress() {
        File file = new File(PROGRESS_FILE);
        if (!file.exists()) return;

        FileInputStream fileInput = null;
        ObjectInputStream input = null;
        try {
            fileInput = new FileInputStream(file);
            input = new ObjectInputStream(fileInput);
            profiles = (HashMap<String, Player>) input.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            Log.record(e);
            profiles = new HashMap<>();
        } finally {
            // si el ObjectInputStream no se alcanzó a crear, igual hay que cerrar el archivo
            if (input != null) {
                close(input);
            } else {
                close(fileInput);
            }
        }
    }

    /**
     * Closes a file without stopping the program if closing fails.
     * @param stream the file to close (it can be null if it was never opened)
     */
    private void close(Closeable stream) {
        if (stream == null) return;
        try {
            stream.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el archivo: " + e.getMessage());
        }
    }

    /**
     * Puts the saved points and stats into a player that is going to play.
     * @param player the player that is going to play
     */
    private void applyProgress(Player player) {
        if (profiles.containsKey(player.getName())) {
            player.copyProgress(profiles.get(player.getName()));
        }
    }

    /**
     * When the match ends, saves the points and stats of the human players (only once).
     */
    private void saveProgressIfFinished() {
        if (!currentMatch.isGameOver() || progressSaved) return;
        progressSaved = true;

        updateProfile(currentMatch.getLeftPlayer());
        updateProfile(currentMatch.getRightPlayer());

        try {
            saveProgress();
        } catch (DopoSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Saves in the profile of a human player the points he earned in the match that just ended.
     * Only the earned points are added (not the whole player), so a match loaded from an old file
     * cannot erase the points and upgrades obtained after it was saved.
     * @param player the player that has just finished the match
     */
    private void updateProfile(Player player) {
        if (player instanceof Machine) return;
        if (profiles.containsKey(player.getName())) {
            profiles.get(player.getName()).addPoints(player.getMatchPoints());
        } else {
            profiles.put(player.getName(), player);
        }
    }

    /**
     * Checks if the players can act: there must be a match that is not paused or finished.
     * @return true if the players can move, jump, kick or use the power
     */
    private boolean canPlay() {
        return !currentMatch.isPaused() && !currentMatch.isGameOver();
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
     * @return the Player object, or null if the number is not 1 or 2
     */
    private Player getPlayer(int player) {
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
