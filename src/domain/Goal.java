package domain;

import java.io.Serializable;

/**
 * Represents a goal located on one side of the stadium.
 */
public class Goal implements Serializable{

    private int posX;
    private int posY;
    private int width;
    private int height;
    private boolean isLeftGoal;

    /**
     * Constructs a new Goal with the specified position, dimensions, and side orientation.
     * @param posX the X coordinate of the goal
     * @param posY the Y coordinate of the goal
     * @param width the width of the goal
     * @param height the height of the goal
     * @param isLeftGoal true if this is the left goal, false if it is the right goal
     */
    public Goal(int posX, int posY, int width, int height, boolean isLeftGoal) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.isLeftGoal = isLeftGoal;
    }

    /**
     * Checks whether a goal has been scored because the ball has completely entered the goal below the crossbar.
     * @param ball the ball object to check
     * @return true if the ball is completely inside the goal, false otherwise
     */
    public boolean containsBall(Ball ball) {
        boolean belowCrossbar = ball.getPosY() >= posY;
        if (isLeftGoal) {
            return belowCrossbar && ball.getPosX() + ball.getWidth() <= posX + width;
        }
        return belowCrossbar && ball.getPosX() >= posX;
    }

    /**
     * Checks if the ball hits the crossbar and bounces upward if it lands on top of it.
     * @param ball the ball object to check for crossbar collision
     */
    public void checkCrossbar(Ball ball) {
        boolean overGoal = ball.getPosX() + ball.getWidth() > posX && ball.getPosX() < posX + width;
        boolean touchingTop = ball.getPosY() + ball.getHeight() >= posY && ball.getCenterY() < posY;
        if (overGoal && touchingTop) {
            ball.setPosY(posY - ball.getHeight());
            ball.bounce(0, -1);
            if (isLeftGoal) {
                        ball.setVelX(ball.getVelX() + 1);
            } else {
                ball.setVelX(ball.getVelX() - 1);
            }
        }
    }

    /**
     * Gets the X coordinate of the goal.
     * @return the X position value
     */
    public int getPosX() { return posX; }

    /**
     * Gets the Y coordinate of the goal.
     * @return the Y position value
     */
    public int getPosY() { return posY; }
    
    /**
     * Gets the width of the goal.
     * @return the width value
     */
    public int getWidth() { return width; }
    
    /**
     * Gets the height of the goal.
     * @return the height value
     */
    public int getHeight() { return height; }
    
    /**
     * Checks if the goal is the left goal.
     * @return true if the goal is the left goal, false otherwise
     */
    public boolean isLeftGoal() { return isLeftGoal; }
}
