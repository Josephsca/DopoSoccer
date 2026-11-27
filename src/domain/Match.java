package domain;
import java.io.Serializable;

/**
 * Represents a match between two players. It contains the stadium, goals, and
 * ball, and uses a WinCondition to decide when the game ends.
 */
public class Match implements Serializable{
    // Cuadros por segundo objetivo para la simulación del partido.
    public static final int FPS = 60;               
    // Duración de un cuadro en segundos según los FPS configurados.
    public static final double DT = 1.0 / FPS;     
    // Puntos otorgados al jugador que gana el partido.
    public static final int WIN_POINTS = 100;
    // Puntos otorgados al jugador que pierde o en caso de empate.
    public static final int LOSE_POINTS = 25;

    // Solapamiento horizontal tolerado en píxeles antes de bloquear o separar jugadores.
    public static final int ALLOWED_OVERLAP = 22;
    // Contador de cuadros transcurridos desde que inició el partido.
    private int timer;          
    // Indica si el partido está pausado temporalmente.
    private boolean isPaused;
    // Indica si el partido ya finalizó.
    private boolean isFinished;
    // Jugador ganador del partido, o null si sigue en curso o empataron.
    private Player winner;
    // Jugador ubicado en el lado izquierdo de la cancha.
    private Player leftPlayer;
    // Jugador ubicado en el lado derecho de la cancha.
    private Player rightPlayer;
    // Balón principal del partido.
    private Ball ball;
    // Portería del lado izquierdo.
    private Goal leftGoal;
    // Portería del lado derecho.
    private Goal rightGoal;
    // Estadio donde se disputa el partido.
    private Stadium stadium;
    // Regla que determina cuándo termina el partido.
    private WinCondition winCondition;

    /**
     * Creates a new match with a 0-0 score and sets all entities to their
     * starting positions.
     *
     * @param leftPlayer   the player on the left side
     * @param rightPlayer  the player on the right side
     * @param stadium      the stadium where the match takes place
     * @param winCondition the rule that determines when the match ends
     */
    // Inicializa el partido, crea las porterías y el balón según el estadio y ubica a los jugadores.
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
    // Ejecuta un ciclo de simulación: decisiones de IA, actualización de entidades, límites, colisiones, goles y fin de partido.
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
     * Checks all match collisions: power shots hitting a rival, players touching
     * the ball, and the ball hitting the crossbars.
     */
    // Evalúa impactos de tiros especiales sobre el rival, contacto de jugadores con el balón y rebotes en travesaños.
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
     * Checks whether the ball has entered either goal and awards a point to the
     * scoring player.
     */
    // Revisa si el balón entró en la portería izquierda o derecha para anotar el gol correspondiente.
    public void checkGoal() {
        if (leftGoal.containsBall(ball)) {
            scoreGoal(rightPlayer, leftPlayer);
        } else if (rightGoal.containsBall(ball)) {
            scoreGoal(leftPlayer, rightPlayer);
        }
    }

    /**
     * Resets both players to their starting sides and drops the ball from the
     * center after a goal.
     */
    // Devuelve a ambos jugadores a sus posiciones iniciales y reinicia el balón sobre el centro de la cancha.
    public void resetPositions() {
        int groundY = stadium.getGroundY();
        leftPlayer.resetPosition(stadium.getWidth() / 4 - Player.WIDTH / 2, groundY - Player.HEIGHT);
        rightPlayer.resetPosition(3 * stadium.getWidth() / 4 - Player.WIDTH / 2, groundY - Player.HEIGHT);
        ball.reset(stadium.getWidth() / 2, stadium.getHeight() / 5);
    }

    /**
     * Moves the player to the left if doing so will not collide with the rival.
     *
     * @param player the player to move
     */
    // Desplaza al jugador hacia la izquierda si el paso no provoca colisión con el rival.
    public void moveLeft(Player player) {
        if (!willCollide(player, -1)) {
            player.moveLeft();
        }
    }

    /**
     * Moves the player to the right if doing so will not collide with the rival.
     *
     * @param player the player to move
     */
    // Desplaza al jugador hacia la derecha si el paso no provoca colisión con el rival.
    public void moveRight(Player player) {
        if (!willCollide(player, 1)) {
            player.moveRight();
        }
    }

    /**
     * Checks whether the player would collide with the rival after taking one
     * step toward them.
     *
     * @param player    the player that wants to move
     * @param direction the movement direction (-1 for left, 1 for right)
     * @return true if the step would cause a collision, false otherwise
     */
    // Calcula si avanzar un paso en la dirección dada superaría el solapamiento permitido con el rival.
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
     * Pushes overlapping players apart horizontally so they do not pass through
     * each other after jumps or power shots.
     */ 
    // Separa en partes iguales hacia izquierda y derecha a los jugadores cuando superan el solapamiento permitido.
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
     * Checks whether the match has ended.
     *
     * @return true if the match is finished, false otherwise
     */
    // Indica si el partido ya terminó.
    public boolean isGameOver() {
        return isFinished;
    }

    /**
     * Returns the winner of the match.
     *
     * @return the winning player, or null if the match is still running or tied
     */
    // Devuelve el jugador ganador o null si el partido sigue activo o terminó en empate.
    public Player getWinner() {
        return winner;
    }

    /**
     * Pauses the match if it has not finished yet.
     */
    // Activa la pausa del partido siempre que no haya finalizado.
    public void pause() {
        if (!isFinished) isPaused = true;
    }

    /**
     * Resumes the paused match.
     */
    // Reanuda el partido retirando la pausa.
    public void resume() {
        isPaused = false;
    }

    /**
     * Ends the match, sets the winner, and gives match points to both players.
     */
    // Cierra el partido, determina el ganador según los goles y asigna los puntos correspondientes.
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
     * Adds a goal to the scorer, charges part of the conceding player's power bar,
     * and resets entity positions.
     *
     * @param scorer   the player who scored the goal
     * @param conceded the player who conceded the goal
     */
    // Suma el gol al anotador, otorga carga parcial de poder a quien recibió el gol y reinicia posiciones.
    private void scoreGoal(Player scorer, Player conceded) {
        scorer.addGoal();
        conceded.chargePowerBar(Player.POWER_CHARGE_TIME / 4);
        resetPositions();
    }

    /**
     * Returns the opponent of the given player.
     *
     * @param player the reference player
     * @return the opposing player
     */
    // Devuelve el jugador contrario al recibido como parámetro.
    public Player getRival(Player player) {
        if (player == leftPlayer) {
            return rightPlayer;
        } else {
            return leftPlayer;
        }
    }

    /**
     * Returns the elapsed match time in seconds.
     *
     * @return the elapsed seconds
     */
    // Convierte el contador de cuadros transcurridos a segundos de partido.
    public int getSeconds() { return timer / FPS; }

    /**
     * Returns the elapsed match time in frames.
     *
     * @return the timer value in frames
     */
    // Devuelve el tiempo transcurrido del partido en cuadros.
    public int getTimer() { return timer; }

    /**
     * Checks whether the match is currently paused.
     *
     * @return true if the match is paused, false otherwise
     */
    // Indica si el partido está pausado en este momento.
    public boolean isPaused() { return isPaused; }

    /**
     * Returns the player on the left side of the stadium.
     *
     * @return the left player
     */
    // Devuelve el jugador del lado izquierdo.
    public Player getLeftPlayer() { return leftPlayer; }

    /**
     * Returns the player on the right side of the stadium.
     *
     * @return the right player
     */
    // Devuelve el jugador del lado derecho.
    public Player getRightPlayer() { return rightPlayer; }

    /**
     * Returns the ball used in the match.
     *
     * @return the match ball
     */
    // Devuelve el balón del partido.
    public Ball getBall() { return ball; }

    /**
     * Returns the goal on the left side of the stadium.
     *
     * @return the left goal
     */
    // Devuelve la portería izquierda.
    public Goal getLeftGoal() { return leftGoal; }

    /**
     * Returns the goal on the right side of the stadium.
     *
     * @return the right goal
     */
    // Devuelve la portería derecha.
    public Goal getRightGoal() { return rightGoal; }

    /**
     * Returns the stadium where the match is played.
     *
     * @return the match stadium
     */
    // Devuelve el estadio del partido.
    public Stadium getStadium() { return stadium; }

    /**
     * Returns the winning condition rule used in the match.
     *
     * @return the winning condition
     */
    // Devuelve la condición de victoria configurada para el partido.
    public WinCondition getWinCondition() { return winCondition; }
}