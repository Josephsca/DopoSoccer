package domain;

import java.io.Serializable;

/**
 * Represents the match stadium with three possible screen resolutions.
 */
public class Stadium implements Serializable{
    // Identificador de resolución pequeña (800x450).
    public static final String SMALL = "SMALL";    
    // Identificador de resolución mediana (1024x576).
    public static final String MEDIUM = "MEDIUM";   
    // Identificador de resolución grande (1280x720).
    public static final String LARGE = "LARGE";  
    // Ancho total del estadio en píxeles.
    private int width;
    // Alto total del estadio en píxeles.
    private int height;
    // Nombre en mayúsculas de la resolución configurada.
    private String resolution;

    /**
     * Creates a new Stadium with the given resolution size.
     *
     * @param resolution the resolution option (SMALL, MEDIUM, or LARGE)
     * @throws DopoSoccerException if the resolution is invalid or null
     */
    // Valida la resolución indicada, asigna ancho y alto en píxeles y guarda la resolución normalizada.
    public Stadium(String resolution) throws DopoSoccerException {
        if (resolution == null) throw new DopoSoccerException(DopoSoccerException.INVALID_RESOLUTION);
        switch (resolution.toUpperCase()) {
            case SMALL: width = 800; height = 450; break;
            case MEDIUM: width = 1024; height = 576; break;
            case LARGE: width = 1280; height = 720; break;
            default: throw new DopoSoccerException(DopoSoccerException.INVALID_RESOLUTION);
        }
        this.resolution = resolution.toUpperCase();
    }

    /**
     * Returns the vertical Y coordinate of the stadium ground level.
     *
     * @return the ground Y coordinate
     */
    // Calcula la coordenada Y del suelo restando un noveno de la altura total del estadio.
    public int getGroundY() {
        return height - height / 9;
    }

    /**
     * Keeps an entity inside the stadium walls and ground, notifying it when a
     * boundary is hit.
     *
     * @param entity the entity to keep inside the stadium
     */
    // Limita la posición de la entidad a los bordes izquierdo, derecho, superior y al suelo, notificando cada choque.
    public void keepInBounds(Entity entity) {
        if (entity.getPosX() < 0) {
            entity.setPosX(0);
            entity.hitBoundary(1, 0);
        }
        if (entity.getPosX() + entity.getWidth() > width) {
            entity.setPosX(width - entity.getWidth());
            entity.hitBoundary(-1, 0);
        }
        if (entity.getPosY() < 0) {
            entity.setPosY(0);
            entity.hitBoundary(0, 1);
        }
        if (entity.getPosY() + entity.getHeight() >= getGroundY()) {
            entity.setPosY(getGroundY() - entity.getHeight());
            entity.hitBoundary(0, -1);
        }
    }

    /**
     * Returns the total width of the stadium in pixels.
     *
     * @return the stadium width
     */
    // Devuelve el ancho total del estadio en píxeles.
    public int getWidth() { return width; }

    /**
     * Returns the total height of the stadium in pixels.
     *
     * @return the stadium height
     */
    // Devuelve el alto total del estadio en píxeles.
    public int getHeight() { return height; }

    /**
     * Returns the selected resolution name of the stadium.
     *
     * @return the resolution string
     */
    // Devuelve la resolución seleccionada para el estadio.
    public String getResolution() { return resolution; }
}