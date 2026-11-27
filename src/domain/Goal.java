package domain;

import java.io.Serializable;

/**
 * Represents a goal located on one side of the stadium.
 */
public class Goal implements Serializable{

    // Coordenada horizontal de la esquina superior izquierda de la portería.
    private int posX;
    // Coordenada vertical de la esquina superior izquierda de la portería.
    private int posY;
    // Ancho de la portería en píxeles.
    private int width;
    // Alto de la portería en píxeles.
    private int height;
    // Indica si la portería pertenece al lado izquierdo (true) o al derecho (false).
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
    // Inicializa la posición, dimensiones y lado de la portería.
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
    // Comprueba si el balón ha entrado por completo dentro de la portería por debajo del travesaño.
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
    // Detecta si el balón cae sobre el travesaño, ajusta su altura y lo hace rebotar hacia arriba.
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
    // Devuelve la coordenada X de la portería.
    public int getPosX() { return posX; }

    /**
     * Returns the Y coordinate of the goal.
     *
     * @return the Y position
     */
    // Devuelve la coordenada Y de la portería.
    public int getPosY() { return posY; }
    
    /**
     * Returns the width of the goal.
     *
     * @return the width value
     */
    // Devuelve el ancho de la portería.
    public int getWidth() { return width; }
    
    /**
     * Returns the height of the goal.
     *
     * @return the height value
     */
    // Devuelve el alto de la portería.
    public int getHeight() { return height; }
    
    /**
     * Checks whether this goal is on the left side of the stadium.
     *
     * @return true if this is the left goal, false otherwise
     */
    // Indica si esta portería está ubicada en el lado izquierdo de la cancha.
    public boolean isLeftGoal() { return isLeftGoal; }
}