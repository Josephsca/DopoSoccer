package domain;

/**
 * Custom exception class for the DopoSoccer game. Possible error messages are
 * defined as constant strings.
 */
public class DopoSoccerException extends Exception {

    public static final String INVALID_CHARACTER = "El personaje seleccionado no existe.";
    public static final String INVALID_MATCH_TYPE = "El tipo de partido o su límite (minutos/goles) no es válido.";
    public static final String INVALID_RESOLUTION = "La resolución de la cancha no es válida.";
    public static final String INVALID_STAT = "La estadística indicada no existe.";
    public static final String NOT_ENOUGH_POINTS = "El jugador no tiene puntos suficientes para esta mejora.";
    public static final String MAX_STAT_REACHED = "La estadística ya está en su valor máximo (10).";
    public static final String POWER_CANNOT_BE_NULL = "El PowerShot no puede ser nulo.";
    public static final String SAVE_ERROR = "Ocurrió un error al guardar la partida.";
    public static final String LOAD_ERROR = "Ocurrió un error al cargar la partida.";
    public static final String INVALID_SAVE_FILE = "El archivo seleccionado no contiene una partida válida.";
    public static final String INVALID_LOAD_FILE = "El archivo no contiene una partida válida";
    public static final String LOAD_ERROR_MESSAGE = "Error al cargar la partida: ";
    public static final String INVALID_STRATEGY = "La estrategia indicada no es válida.";
    public static final String INVALID_NAME = "Debe escribir el nombre del jugador.";
    public static final String SAME_NAMES = "Los dos jugadores deben tener nombres distintos.";
    public static final String PROGRESS_ERROR = "Ocurrió un error al guardar el progreso de los jugadores.";

    /**
     * Creates a new DopoSoccerException with the given error message.
     *
     * @param message the error message describing the problem
     */
    public DopoSoccerException(String message) {
        super(message);
    }
}