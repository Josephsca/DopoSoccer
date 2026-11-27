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
     * Creates a new goal with the given position, size, and side.
     *
     * @param posX       the X coordinate of the goal
     * @param posY       the Y coordinate of the goal
     * @param width      the width of the goal
     * @param height     the height of the goal
     * @param isLeftGoal true if this is the left goal, false for the right goal
     */
    public Goal(int posX, int posY, int width, int height, boolean isLeftGoal) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.isLeftGoal = isLeftGoal;
    }

    /**
     * Checks whether the ball is completely inside the goal below the crossbar.
     *
     * @param ball the match ball to check
     * @return true if the ball is fully inside the goal, false otherwise
     */
    public boolean containsBall(Ball ball) {
        boolean belowCrossbar = ball.getPosY() >= posY;
        if (isLeftGoal) {
            return belowCrossbar && ball.getPosX() + ball.getWidth() <= posX + width;
        }
        return belowCrossbar && ball.getPosX() >= posX;
    }

    /**
     * Checks if the ball lands on top of the crossbar and bounces it upward.
     *
     * @param ball the match ball to check for crossbar collision
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
     * Returns the X coordinate of the goal.
     *
     * @return the X position
     */
    public int getPosX() { return posX; }

    /**
     * Returns the Y coordinate of the goal.
     *
     * @return the Y position
     */
    public int getPosY() { return posY; }
    
    /**
     * Returns the width of the goal.
     *
     * @return the width value
     */
    public int getWidth() { return width; }
    
    /**
     * Returns the height of the goal.
     *
     * @return the height value
     */
    public int getHeight() { return height; }
    
    /**
     * Checks whether this goal is on the left side of the stadium.
     *
     * @return true if this is the left goal, false otherwise
     */
    public boolean isLeftGoal() { return isLeftGoal; }
}