package domain;

/**
 * Custom exception class for the DopoSoccer game. Possible error messages are
 * defined as constant strings.
 */
public class DopoSoccerException extends Exception {

    // Mensaje cuando se elige un país o personaje inexistente.
    public static final String INVALID_CHARACTER = "El personaje seleccionado no existe.";
    // Mensaje cuando el modo de partido o su límite numérico no son válidos.
    public static final String INVALID_MATCH_TYPE = "El tipo de partido o su límite (minutos/goles) no es válido.";
    // Mensaje cuando el tamaño del estadio solicitado no está soportado.
    public static final String INVALID_RESOLUTION = "La resolución de la cancha no es válida.";
    // Mensaje cuando se consulta o mejora una estadística desconocida.
    public static final String INVALID_STAT = "La estadística indicada no existe.";
    // Mensaje cuando el jugador no alcanza los puntos necesarios para subir una estadística.
    public static final String NOT_ENOUGH_POINTS = "El jugador no tiene puntos suficientes para esta mejora.";
    // Mensaje cuando una estadística ya alcanzó su tope máximo permitido.
    public static final String MAX_STAT_REACHED = "La estadística ya está en su valor máximo (10).";
    // Mensaje cuando se intenta asignar un tiro especial nulo.
    public static final String POWER_CANNOT_BE_NULL = "El PowerShot no puede ser nulo.";
    // Mensaje genérico si falla la escritura del archivo de partida guardada.
    public static final String SAVE_ERROR = "Ocurrió un error al guardar la partida.";
    // Mensaje genérico si falla la lectura del archivo de partida guardada.
    public static final String LOAD_ERROR = "Ocurrió un error al cargar la partida.";
    // Mensaje cuando el archivo seleccionado para guardar o cargar no es válido.
    public static final String INVALID_SAVE_FILE = "El archivo seleccionado no contiene una partida válida.";
    // Mensaje cuando el contenido deserializado no corresponde a una partida.
    public static final String INVALID_LOAD_FILE = "El archivo no contiene una partida válida";
    // Prefijo para reportar errores detallados al cargar una partida.
    public static final String LOAD_ERROR_MESSAGE = "Error al cargar la partida: ";
    // Mensaje cuando la estrategia de la máquina no es reconocida.
    public static final String INVALID_STRATEGY = "La estrategia indicada no es válida.";
    // Mensaje cuando el nombre del jugador está vacío o es nulo.
    public static final String INVALID_NAME = "Debe escribir el nombre del jugador.";
    // Mensaje cuando ambos jugadores humanos ingresan el mismo nombre.
    public static final String SAME_NAMES = "Los dos jugadores deben tener nombres distintos.";
    // Mensaje cuando falla la persistencia del progreso de los perfiles.
    public static final String PROGRESS_ERROR = "Ocurrió un error al guardar el progreso de los jugadores.";

    /**
     * Creates a new DopoSoccerException with the given error message.
     *
     * @param message the error message describing the problem
     */
    // Construye la excepción pasando el mensaje descriptivo a la superclase Exception.
    public DopoSoccerException(String message) {
        super(message);
    }
}