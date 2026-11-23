package domain;

import java.io.Serializable;

/**
 * Represents the stadium of the match with three possible screen resolutions.
 */
public class Stadium implements Serializable{
    public static final String SMALL = "SMALL";    
    public static final String MEDIUM = "MEDIUM";   
    public static final String LARGE = "LARGE";  
    private int width;
    private int height;
    private String resolution;

    /**
     * Constructs a new Stadium with the specified resolution size.
     * @param resolution the resolution option, either SMALL, MEDIUM, or LARGE
     * @throws DopoSoccerException if the resolution is invalid or null
     */
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
     * Gets the vertical ground level position (Y coordinate) of the stadium. 
     * @return the ground Y coordinate value
     */
    public int getGroundY() {
        return height - height / 9;
    }

    /**
     * Keeps an entity within the boundaries of the stadium and triggers a reaction if a boundary is hit.
     * @param entity the entity object, such as a player or the ball
     */
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
     * Gets the total width of the stadium.
     * @return the stadium width in pixels
     */
    public int getWidth() { return width; }

    /**
     * Gets the total height of the stadium.
     * @return the stadium height in pixels
     */
    public int getHeight() { return height; }

    /**
     * Gets the current resolution name of the stadium.
     * @return the resolution string
     */
    public String getResolution() { return resolution; }
}
