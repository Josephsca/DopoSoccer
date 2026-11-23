package domain;
import java.io.Serializable;

/**
 * Represents a game entity that occupies a rectangular area on the field and can move.
 * Player and Ball inherit from this class.
 * The Y axis grows downward (as on a screen).
 */
public abstract class Entity implements Serializable {

    protected int posX;
    protected int posY;
    protected double velX;
    protected double velY;
    protected int width;
    protected int height;

    /**
     * Constructs a new entity with a specified position, width, and height.     * @param posX the initial X coordinate of the entity
     * @param posY the initial Y coordinate of the entity
     * @param width the width of the entity
     * @param height the height of the entity
     */
    public Entity(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.velX = 0;
        this.velY = 0;
    }

    /**
     * Advances the entity by one unit of time (one game frame).
     * Each type of entity moves in its own way.
     */
    public abstract void update();

    /**
     * Checks whether this entity intersects with another entity.
     * @param other the other entity to check intersection against
     * @return true if the bounding rectangles touch or overlap, false otherwise
     */
    public boolean intersects(Entity other) {
        return posX < other.posX + other.width
            && posX + width > other.posX
            && posY < other.posY + other.height
            && posY + height > other.posY;
    }

    /**
     * Called when the entity collides with a boundary of the stadium.
     * By default, it stops movement in that direction. Subclasses can override this behavior.
     * @param normalX the X component of the boundary normal direction (-1, 0, or 1)
     * @param normalY the Y component of the boundary normal direction (-1, 0, or 1)
     */
    public void hitBoundary(double normalX, double normalY) {
        if (normalX != 0) velX = 0;
        if (normalY != 0) velY = 0;
    }

    /**
     * Gets the X coordinate of the entity's center.
     * @return the center X position value
     */
    public int getCenterX() { return posX + width / 2; }
    
    /**
     * Gets the Y coordinate of the entity's center.
     * @return the center Y position value
     */
    public int getCenterY() { return posY + height / 2; }
    
    /**
     * Gets the current X coordinate of the entity.
     * @return the X position value
     */
    public int getPosX() { return posX; }
    
    /**
     * Gets the current Y coordinate of the entity.
     * @return the Y position value
     */
    public int getPosY() { return posY; }

    /**
     * Gets the horizontal velocity of the entity.
     * @return the velocity in the X direction
     */
    public double getVelX() { return velX; }

    /**
     * Gets the vertical velocity of the entity.
     * @return the velocity in the Y direction
     */
    public double getVelY() { return velY; }
    
    /**
     * Gets the width of the entity.
     * @return the width value
     */
    public int getWidth() { return width; }
    
    /**
     * Gets the height of the entity.
     * @return the height value
     */
    public int getHeight() { return height; }
    
    /**
     * Sets the X coordinate of the entity.
     * @param posX the new X position value
     */
    public void setPosX(int posX) { this.posX = posX; }
    
    /**
     * Sets the Y coordinate of the entity.
     * @param posY the new Y position value
     */
    public void setPosY(int posY) { this.posY = posY; }
    
    /**
     * Sets the horizontal velocity of the entity.
     * @param velX the new velocity in the X direction
     */
    public void setVelX(double velX) { this.velX = velX; }
    
    /**
     * Sets the vertical velocity of the entity.
     * @param velY the new velocity in the Y direction
     */
    public void setVelY(double velY) { this.velY = velY; }
}
