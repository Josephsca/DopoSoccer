package domain;
import java.io.Serializable;

/**
 * Represents a game entity that occupies a rectangular area on the field and
 * can move. The Y axis grows downward as on a screen.
 */
public abstract class Entity implements Serializable {

    protected int posX;
    protected int posY;
    protected double velX;
    protected double velY;
    protected int width;
    protected int height;

    /**
     * Creates a new entity with the given position and size.
     *
     * @param posX   the initial X coordinate of the entity
     * @param posY   the initial Y coordinate of the entity
     * @param width  the width of the entity
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
     * Advances the entity by one game frame according to its movement rules.
     */
    public abstract void update();

    /**
     * Checks whether this entity touches or overlaps with another entity.
     *
     * @param other the other entity to check intersection with
     * @return true if the rectangular areas overlap, false otherwise
     */
    public boolean intersects(Entity other) {
        return posX < other.posX + other.width
            && posX + width > other.posX
            && posY < other.posY + other.height
            && posY + height > other.posY;
    }

    /**
     * Handles a collision with a stadium boundary by stopping movement in that
     * direction.
     *
     * @param normalX the X direction of the boundary (-1, 0, or 1)
     * @param normalY the Y direction of the boundary (-1, 0, or 1)
     */
    public void hitBoundary(double normalX, double normalY) {
        if (normalX != 0) velX = 0;
        if (normalY != 0) velY = 0;
    }

    /**
     * Returns the X coordinate of the entity center.
     *
     * @return the center X position
     */
    public int getCenterX() { return posX + width / 2; }
    
    /**
     * Returns the Y coordinate of the entity center.
     *
     * @return the center Y position
     */
    public int getCenterY() { return posY + height / 2; }
    
    /**
     * Returns the current X coordinate of the entity.
     *
     * @return the X position
     */
    public int getPosX() { return posX; }
    
    /**
     * Returns the current Y coordinate of the entity.
     *
     * @return the Y position
     */
    public int getPosY() { return posY; }

    /**
     * Returns the horizontal velocity of the entity.
     *
     * @return the velocity on the X axis
     */
    public double getVelX() { return velX; }

    /**
     * Returns the vertical velocity of the entity.
     *
     * @return the velocity on the Y axis
     */
    public double getVelY() { return velY; }
    
    /**
     * Returns the width of the entity.
     *
     * @return the width value
     */
    public int getWidth() { return width; }
    
    /**
     * Returns the height of the entity.
     *
     * @return the height value
     */
    public int getHeight() { return height; }
    
    /**
     * Sets the X coordinate of the entity.
     *
     * @param posX the new X position
     */
    public void setPosX(int posX) { this.posX = posX; }
    
    /**
     * Sets the Y coordinate of the entity.
     *
     * @param posY the new Y position
     */
    public void setPosY(int posY) { this.posY = posY; }
    
    /**
     * Sets the horizontal velocity of the entity.
     *
     * @param velX the new velocity on the X axis
     */
    public void setVelX(double velX) { this.velX = velX; }
    
    /**
     * Sets the vertical velocity of the entity.
     *
     * @param velY the new velocity on the Y axis
     */
    public void setVelY(double velY) { this.velY = velY; }
}