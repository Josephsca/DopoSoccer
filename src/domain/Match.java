package domain;
import java.io.Serializable;
/**
 * Represents a match between two players. Contains the stadium, goals, and ball,
 * and uses a WinCondition to determine when the game ends.
 */
public class Match implements Serializable{
    public static final int FPS = 60;               
    public static final double DT = 1.0 / FPS;     
    public static final int WIN_POINTS = 100;
    public static final int LOSE_POINTS = 25;
    /** Los dibujos de los personajes tienen bordes transparentes, por eso dejamos que se acerquen 22 px. */
    public static final int ALLOWED_OVERLAP = 22;
    private int timer;          
    private boolean isPaused;
    private boolean isFinished;
    private Player winner;
    private Player leftPlayer;
    private Player rightPlayer;
    private Ball ball;
    private Goal leftGoal;
    private Goal rightGoal;
    private Stadium stadium;
    private WinCondition winCondition;

    /**
     * Constructs a new match with a 0-0 score and sets all entities to their initial positions.
     * @param leftPlayer the player on the left side
     * @param rightPlayer the player on the right side
     * @param stadium the stadium where the match takes place
     * @param winCondition the rule that determines the end of the match
     */
    public Match(Player leftPlayer, Player rightPlayer, Stadium stadium, WinCondition winCondition) {
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
        this.stadium = stadium;
        this.winCondition = winCondition;
        this.timer = 0;
        this.isPaused = false;
        this.isFinished = false;
        this.winner = null;

        int goalWidth = stadium.getWidth() / 14;
        int goalHeight = stadium.getHeight() / 3;
        int goalY = stadium.getGroundY() - goalHeight;
        leftGoal = new Goal(0, goalY, goalWidth, goalHeight, true);
        rightGoal = new Goal(stadium.getWidth() - goalWidth, goalY, goalWidth, goalHeight, false);
        ball = new Ball(stadium.getWidth() / 2, stadium.getHeight() / 4, 14, 0.45);

        leftPlayer.startMatch();
        rightPlayer.startMatch();
        resetPositions();
    }

    /**
     * Advances the match simulation by one frame.
     */
    public void update() {
        if (isPaused || isFinished) return;

        timer++;

        if (leftPlayer instanceof Machine) {
            ((Machine) leftPlayer).decide(this);
        }

        if (rightPlayer instanceof Machine) {
            ((Machine) rightPlayer).decide(this);
        }

        leftPlayer.update();
        rightPlayer.update();

        ball.update();

        separatePlayers();
        stadium.keepInBounds(leftPlayer);
        stadium.keepInBounds(rightPlayer);
        stadium.keepInBounds(ball);

        checkCollisions();
        checkGoal();

        if (winCondition.isGameOver(this)) finish();
    }

    /**
     * Checks all game collisions: power shots against the rival, players touching the ball,
     * and the ball hitting the crossbars.
     */
    public void checkCollisions() {
        Player shooter = ball.getPowerShooter();
        if (shooter != null) {
            Player rival = getRival(shooter);
            if (ball.intersects(rival)) {
                Character power = shooter.getCharacter();
                rival.receiveDamage(power.getShotDamage() * shooter.getKickMultiplier());
                power.applyEffect(rival);
                ball.setPowerShooter(null);
                ball.setVelX(-ball.getVelX() * 0.4);
            }
        }
        if (leftPlayer.intersects(ball) && ball.getPowerShooter() != leftPlayer) leftPlayer.touchBall(ball);
        if (rightPlayer.intersects(ball) && ball.getPowerShooter() != rightPlayer) rightPlayer.touchBall(ball);
        leftGoal.checkCrossbar(ball);
        rightGoal.checkCrossbar(ball);
    }

    /**
     * Checks if the ball has entered a goal and awards the point to the opposing player.
     */
    public void checkGoal() {
        if (leftGoal.containsBall(ball)) {
            scoreGoal(rightPlayer, leftPlayer);
        } else if (rightGoal.containsBall(ball)) {
            scoreGoal(leftPlayer, rightPlayer);
        }
    }

    /**
     * Resets player positions to their sides and drops the ball from the center after a goal.
     */
    public void resetPositions() {
        int groundY = stadium.getGroundY();
        leftPlayer.resetPosition(stadium.getWidth() / 4 - Player.WIDTH / 2, groundY - Player.HEIGHT);
        rightPlayer.resetPosition(3 * stadium.getWidth() / 4 - Player.WIDTH / 2, groundY - Player.HEIGHT);
        ball.reset(stadium.getWidth() / 2, stadium.getHeight() / 5);
    }

    /**
     * Moves the player to the left, but first checks that it will not collide with the rival.
     * @param player the player that wants to move
     */
    public void moveLeft(Player player) {
        if (!willCollide(player, -1)) {
            player.moveLeft();
        }
    }

    /**
     * Moves the player to the right, but first checks that it will not collide with the rival.
     * @param player the player that wants to move
     */
    public void moveRight(Player player) {
        if (!willCollide(player, 1)) {
            player.moveRight();
        }
    }

    /**
     * Checks if the player would collide with the rival after taking one step.
     * Only counts as a collision if the player is walking towards the rival,
     * so a player can always walk away from the other one.
     * @param player the player that wants to move
     * @param direction -1 to the left, 1 to the right
     * @return true if the step would make the player crash into the rival
     */
    public boolean willCollide(Player player, int direction) {
        Player rival = getRival(player);

        boolean goingTowardsRival = (rival.getCenterX() - player.getCenterX()) * direction > 0;
        if (!goingTowardsRival) {
            return false;
        }

        int nextX = player.getPosX() + direction * player.getStep();

        int overlapX = Math.min(nextX + player.getWidth(), rival.getPosX() + rival.getWidth())
                     - Math.max(nextX, rival.getPosX());
        int overlapY = Math.min(player.getPosY() + player.getHeight(), rival.getPosY() + rival.getHeight())
                     - Math.max(player.getPosY(), rival.getPosY());

        return overlapX > ALLOWED_OVERLAP && overlapY > 0;
    }

    /**
     * Safety check for the cases where moveLeft/moveRight cannot prevent the crash
     * (for example when a player falls on top of the other after a jump, or a power pushes him).
     * Prevents players from passing through each other: if they overlap, 
     * each one moves outward to their respective side until they touch.
     */ 
    private void separatePlayers() {
        int overlapX = Math.min(leftPlayer.getPosX() + leftPlayer.getWidth(),
                                rightPlayer.getPosX() + rightPlayer.getWidth())
                    - Math.max(leftPlayer.getPosX(), rightPlayer.getPosX()) - ALLOWED_OVERLAP;
        int overlapY = Math.min(leftPlayer.getPosY() + leftPlayer.getHeight(),
                                rightPlayer.getPosY() + rightPlayer.getHeight())
                    - Math.max(leftPlayer.getPosY(), rightPlayer.getPosY());
        if (overlapX <= 0 || overlapY <= 0) return;  

        Player lefter = leftPlayer.getCenterX() <= rightPlayer.getCenterX() ? leftPlayer : rightPlayer;
        Player righter = getRival(lefter);
        int push = (overlapX + 1) / 2;
        lefter.setPosX(lefter.getPosX() - push);
        righter.setPosX(righter.getPosX() + push);
    }

    /**
     * Checks whether the match is over.
     * @return true if the match has finished, false otherwise
     */
    public boolean isGameOver() {
        return isFinished;
    }

    /**
     * Gets the winner of the match.
     * @return the winning Player instance, or null if the match is ongoing or tied
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Pauses the match if it has not finished yet.
     */
    public void pause() {
        if (!isFinished) isPaused = true;
    }

    /**
     * Resumes the paused match.
     */
    public void resume() {
        isPaused = false;
    }

    /**
     * Finishes the match, determines the winner, and distributes points.
     */
    public void finish() {
        if (isFinished) return;
        isFinished = true;
        isPaused = false;
        if (leftPlayer.getScore() > rightPlayer.getScore()) {
            winner = leftPlayer;
        } else if (rightPlayer.getScore() > leftPlayer.getScore()) {
            winner = rightPlayer;
        }
        if (winner == null) {
            leftPlayer.addPoints(LOSE_POINTS);
            rightPlayer.addPoints(LOSE_POINTS);
        } else {
            winner.addPoints(WIN_POINTS);
            getRival(winner).addPoints(LOSE_POINTS);
        }
    }

    /**
     * Records a goal for the scorer and grants power charge to the conceding player.
     * @param scorer the player who scored the goal
     * @param conceded the player who conceded the goal
     */
    private void scoreGoal(Player scorer, Player conceded) {
        scorer.addGoal();
        conceded.chargePowerBar(Player.POWER_CHARGE_TIME / 4);
        resetPositions();
    }

    /**
     * Gets the rival player of the specified player.
     * @param player the reference player
     * @return the opposing Player instance
     */
    public Player getRival(Player player) {
        if (player == leftPlayer) {
            return rightPlayer;
        } else {
            return leftPlayer;
        }
    }

    /**
     * Gets the elapsed time of the match in seconds.
     * @return the elapsed seconds
     */
    public int getSeconds() { return timer / FPS; }

    /**
     * Gets the raw timer value in frames.
     * @return the elapsed time in frames
     */
    public int getTimer() { return timer; }

    /**
     * Checks if the match is currently paused.
     * @return true if paused, false otherwise
     */
    public boolean isPaused() { return isPaused; }

    /**
     * Gets the left player instance.
     * @return the left Player object
     */
    public Player getLeftPlayer() { return leftPlayer; }

    /**
     * Gets the right player instance.
     * @return the right Player object
     */
    public Player getRightPlayer() { return rightPlayer; }

    /**
     * Gets the ball instance used in the match.
     * @return the Ball object
     */
    public Ball getBall() { return ball; }

    /**
     * Gets the left goal instance.
     * @return the left Goal object
     */
    public Goal getLeftGoal() { return leftGoal; }

    /**
     * Gets the right goal instance.
     * @return the right Goal object
     */
    public Goal getRightGoal() { return rightGoal; }

    /**
     * Gets the stadium instance of the match.
     * @return the Stadium object
     */
    public Stadium getStadium() { return stadium; }

    /**
     * Gets the win condition rule used in the match.
     * @return the WinCondition object
     */
    public WinCondition getWinCondition() { return winCondition; }
}
