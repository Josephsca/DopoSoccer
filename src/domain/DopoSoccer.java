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
 * Main domain class of the game. The graphical user interface only interacts
 * with this class, and players are identified as 1 (left) and 2 (right).
 */
public class DopoSoccer {

    // Códigos de países disponibles para crear personajes jugables.
    public static final String[] COUNTRIES = {"SouthKorea", "Cameroon", "Nigeria", "USA"};
    // Identificador del modo de partido por tiempo límite.
    public static final String TIME = "TIME";
    // Identificador del modo de partido por cantidad de goles.
    public static final String GOALS = "GOALS";
    // Ruta del archivo donde se persiste el progreso de los jugadores.
    public static final String PROGRESS_FILE = "progreso.dat";
    // Indica si el sonido del juego está activado.
    private boolean sound;
    // Instancia del partido activo o en pausa.
    private Match currentMatch;
    // Evita guardar dos veces los puntos al terminar el mismo partido.
    private boolean progressSaved;
    /** Saved progress (points and stats) of each human player, indexed by name. */
    // Mapa de perfiles guardados por nombre con los puntos y estadísticas de cada jugador.
    private HashMap<String, Player> profiles;

    /**
     * Creates a new DopoSoccer game instance with default settings, loads saved
     * player progress, and starts a default paused match.
     */
    // Inicializa la configuración base, carga los perfiles guardados y prepara un partido por defecto pausado.
    public DopoSoccer() {
        sound = true;
        progressSaved = false;
        profiles = new HashMap<>();
        loadProgress();
        createDefaultMatch();
    }

    /**
     * Creates the initial match when the game starts (J1 vs J2, by goals) and
     * pauses it until the players begin a real match.
     */
    // Crea el partido inicial de ejemplo y lo deja en pausa hasta que inicie uno real.
    private void createDefaultMatch() {
        try {
            startNewMatch("J1", COUNTRIES[0], "J2", COUNTRIES[1], GOALS, 3, Stadium.MEDIUM);
            currentMatch.pause();
        } catch (DopoSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Creates and starts a new Player versus Player match.
     *
     * @param name1      the name of player 1
     * @param country1   the country of player 1
     * @param name2      the name of player 2
     * @param country2   the country of player 2
     * @param matchType  the type of match, either TIME or GOALS
     * @param limit      the time limit in minutes or the target number of goals
     * @param resolution the stadium size, such as SMALL, MEDIUM, or LARGE
     * @throws DopoSoccerException if any parameter is invalid or both players have the same name
     */
    // Valida los datos de dos jugadores humanos, aplica su progreso y crea un nuevo partido.
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
     * Creates and starts a new Player versus Machine match.
     *
     * @param playerName     the name of the human player
     * @param playerCountry  the country of the human player
     * @param machineCountry the country of the machine player
     * @param matchType      the winning condition type, either TIME or GOALS
     * @param limit          the time limit in minutes or the target number of goals
     * @param resolution     the stadium size
     * @param machineLeft    true if the machine plays on the left side, false for the right side
     * @param strategy       the machine strategy name: "Torpe", "Ofensiva", "Defensiva", or "Experta"
     * @throws DopoSoccerException if any configuration parameter or strategy is invalid
     */
    // Configura un partido contra la máquina según la estrategia elegida y ubica a cada jugador en su lado.
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
     * Pauses the current match.
     */
    // Pone en pausa el partido en curso.
    public void pauseMatch() {
        currentMatch.pause();
    }

    /**
     * Resumes the paused match.
     */
    // Reanuda el partido que estaba en pausa.
    public void resumeMatch() {
        currentMatch.resume();
    }

    /**
     * Finishes the current match and saves player progress if not already saved.
     */
    // Finaliza el partido actual y guarda los puntos obtenidos por los jugadores.
    public void finishMatch() {
        currentMatch.finish();
        saveProgressIfFinished();
    }

    /**
     * Saves the current match to a file so it can be loaded later.
     *
     * @param filePath the path of the destination file
     * @return true if the match was saved successfully
     * @throws DopoSoccerException if an error occurs while writing the file
     */
    // Serializa y guarda el estado del partido actual en la ruta de archivo indicada.
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
     * Loads a saved match from a file and sets it as the current match.
     *
     * @param filePath the path of the saved match file
     * @throws DopoSoccerException if the file cannot be read or does not contain a valid match
     */
    // Lee un partido guardado desde disco y lo establece como el partido actual.
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
     * Returns the currently active match.
     *
     * @return the current match instance
     */
    // Devuelve la instancia del partido actual.
    public Match getCurrentMatch() {
        return currentMatch;
    }

    /**
     * Advances the simulation by one frame and saves progress if the match ends.
     */
    // Avanza un cuadro de simulación del partido y guarda el progreso si terminó.
    public void update() {
        currentMatch.update();
        saveProgressIfFinished();
    }
    
    /**
     * Moves the given human player to the left.
     *
     * @param player the player number (1 for left, 2 for right)
     */
    // Mueve hacia la izquierda al jugador humano indicado si puede actuar.
    public void moveLeft(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            currentMatch.moveLeft(p);
        }
    }

    /**
     * Moves the given human player to the right.
     *
     * @param player the player number (1 for left, 2 for right)
     */
    // Mueve hacia la derecha al jugador humano indicado si puede actuar.
    public void moveRight(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            currentMatch.moveRight(p);
        }
    }

    /**
     * Makes the given human player jump.
     *
     * @param player the player number (1 for left, 2 for right)
     */
    // Ordena saltar al jugador humano indicado si está habilitado para jugar.
    public void jump(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.jump();
        }
    }

    /**
     * Makes the given human player kick the ball or hit the rival.
     *
     * @param player the player number (1 for left, 2 for right)
     */
    // Ejecuta la patada del jugador humano indicado contra el balón o el rival.
    public void kick(int player) {
        Player p = getPlayer(player);

        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.kick(currentMatch.getRival(p), currentMatch.getBall());
        }
    }

    /**
     * Activates the special power shot for the given human player.
     *
     * @param player the player number (1 for left, 2 for right)
     */
    // Activa el tiro especial del jugador humano indicado si el partido está activo.
    public void activatePower(int player) {
        Player p = getPlayer(player);
        if (p != null && !(p instanceof Machine) && canPlay()) {
            p.activatePower();
        }
    }

    /**
     * Returns the saved profile of a player, or creates a new one if the player
     * has no saved progress yet.
     *
     * @param name the name of the player
     * @return the player profile with saved points and stats
     * @throws DopoSoccerException if the name is null or empty
     */
    // Obtiene el perfil guardado de un jugador o crea uno nuevo si aún no existe.
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
     * Returns the names of all players with saved progress in alphabetical order.
     *
     * @return an array of saved player names
     */
    // Devuelve ordenados alfabéticamente los nombres de jugadores con progreso guardado.
    public String[] getSavedPlayers() {
        String[] names = profiles.keySet().toArray(new String[0]);
        Arrays.sort(names);
        return names;
    }

    /**
     * Upgrades a specific stat for a player using their saved points and saves
     * the updated progress to disk.
     *
     * @param name the name of the player
     * @param stat the stat name ("speed", "jump", "kick", or "survival")
     * @throws DopoSoccerException if the name or stat is invalid, or if the player lacks points
     */
    // Mejora una estadística del perfil del jugador y persiste los cambios en disco.
    public void upgradeStat(String name, String stat) throws DopoSoccerException {
        Player profile = getProfile(name);
        profile.upgradeStat(stat);
        saveProgress();
    }

    /**
     * Saves all player profiles to the progress file.
     *
     * @throws DopoSoccerException if an error occurs while writing the progress file
     */
    // Guarda en disco el mapa completo de perfiles de jugadores.
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
     * Loads saved player profiles from the progress file if it exists.
     */
    // Carga los perfiles guardados desde el archivo de progreso si existe y es válido.
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
            // si el ObjectOutputStream no se alcanzó a crear, igual hay que cerrar el archivo
            if (input != null) {
                close(input);
            } else {
                close(fileInput);
            }
        }
    }

    /**
     * Safely closes an open stream without throwing exceptions.
     *
     * @param stream the stream to close, or null if not opened
     */
    // Cierra un flujo de entrada o salida de forma segura capturando posibles errores de E/S.
    private void close(Closeable stream) {
        if (stream == null) return;
        try {
            stream.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el archivo: " + e.getMessage());
        }
    }

    /**
     * Copies saved points and stats from a stored profile into an active player.
     *
     * @param player the player who is about to play
     */
    // Copia los puntos y estadísticas guardados al jugador que va a disputar el partido.
    private void applyProgress(Player player) {
        if (profiles.containsKey(player.getName())) {
            player.copyProgress(profiles.get(player.getName()));
        }
    }

    /**
     * Saves the match points of human players once when the current match ends.
     */
    // Actualiza y guarda los puntos de los jugadores humanos una sola vez al finalizar el partido.
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
     * Adds the points earned by a human player during the match to their saved profile.
     *
     * @param player the player who just finished the match
     */
    // Suma los puntos ganados en el partido al perfil del jugador humano o lo registra si es nuevo.
    private void updateProfile(Player player) {
        if (player instanceof Machine) return;
        if (profiles.containsKey(player.getName())) {
            profiles.get(player.getName()).addPoints(player.getMatchPoints());
        } else {
            profiles.put(player.getName(), player);
        }
    }

    /**
     * Checks if players can currently act in the match.
     *
     * @return true if the match is neither paused nor finished, false otherwise
     */
    // Indica si el partido está en curso y no está pausado ni terminado.
    private boolean canPlay() {
        return !currentMatch.isPaused() && !currentMatch.isGameOver();
    }

    /**
     * Switches the game sound between enabled and disabled.
     */
    // Alterna el estado del sonido entre activado y desactivado.
    public void toggleSound() {
        sound = !sound;
    }

    /**
     * Returns whether the game sound is currently enabled.
     *
     * @return true if sound is enabled, false otherwise
     */
    // Indica si el sonido está actualmente activado.
    public boolean isSound() {
        return sound;
    }

    /**
     * Creates a character instance for the given country name.
     *
     * @param country the name of the country
     * @return the corresponding character instance
     * @throws DopoSoccerException if the country name is null or unknown
     */
    // Instancia el personaje correspondiente al país solicitado.
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
     * Creates a winning condition based on the match type and limit.
     *
     * @param matchType the rule type, either TIME or GOALS
     * @param limit     the positive limit for minutes or goals
     * @return the corresponding winning condition instance
     * @throws DopoSoccerException if the match type is invalid or the limit is not positive
     */
    // Crea la condición de victoria por tiempo o por goles validando el tipo y el límite.
    private WinCondition createWinCondition(String matchType, int limit) throws DopoSoccerException {
        if (matchType == null || limit <= 0){ 
            throw new DopoSoccerException(DopoSoccerException.INVALID_MATCH_TYPE);
        }
        if (matchType.equals(TIME)) return new TimeCondition(limit);
        if (matchType.equals(GOALS)) return new GoalCondition(limit);
        throw new DopoSoccerException(DopoSoccerException.INVALID_MATCH_TYPE);
    }

    /**
     * Returns a player from the current match by player number.
     *
     * @param player the player number (1 for left, 2 for right)
     * @return the matching player, or null if the number is not 1 or 2
     */
    // Devuelve el jugador 1 (izquierda) o 2 (derecha) del partido actual.
    private Player getPlayer(int player) {
        if (player == 1) return currentMatch.getLeftPlayer();
        if (player == 2) return currentMatch.getRightPlayer();
        return null;
    }

    /**
     * Returns the trimmed player name, or a default name if the input is null or blank.
     *
     * @param name         the raw player name input
     * @param defaultValue the fallback name to use when input is empty
     * @return the trimmed name or the default value
     */
    // Devuelve el nombre recortado sin espacios extremos o un valor por defecto si viene vacío.
    private String defaultName(String name, String defaultValue) {
        if (name == null || name.trim().isEmpty()) {
            return defaultValue;
        }
        return name.trim();
    }
}