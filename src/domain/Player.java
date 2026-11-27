package domain;

/**
 * Represents a match player. It controls a country character, tracks health
 * and power bar levels, and manages upgradeable attributes (speed, jump,
 * kick, and survival) from 1 to 10.
 */
public class Player extends Entity {

    // Ancho del jugador en píxeles.
    public static final int WIDTH = 50;
    // Alto del jugador en píxeles.
    public static final int HEIGHT = 80;
    // Vida máxima base del jugador en el nivel 1 de supervivencia.
    public static final double BASE_LIFE = 100;
    // Daño base infligido al golpear directamente a un rival.
    public static final double KICK_DAMAGE = 10;
    // Valor máximo necesario para llenar la barra de poder.
    public static final double POWER_FULL = 100;
    // Segundos requeridos para cargar por completo la barra de poder de forma pasiva.
    public static final double POWER_CHARGE_TIME = 10;  
    // Duración del aturdimiento en segundos cuando la vida llega a cero.
    public static final double STUN_TIME = 3;            
    // Nivel máximo permitido para cada estadística mejorable.
    public static final int MAX_STAT = 10;
    // Costo base en puntos para la primera mejora de una estadística.
    public static final int BASE_UPGRADE_COST = 500;
    // Velocidad horizontal base del jugador en nivel 1.
    public static final double BASE_SPEED = 5;
    // Impulso vertical base del salto en nivel 1.
    public static final double BASE_JUMP = 13;
    // Aceleración gravitatoria aplicada al movimiento vertical por cuadro.
    public static final double GRAVITY = 0.7;
    // Margen adicional en píxeles alrededor del jugador para detectar alcance de patada.
    public static final int KICK_REACH = 25;
    /** Duration of a kick animation in seconds before the player can kick again. */
    // Tiempo de enfriamiento en segundos antes de permitir una nueva patada.
    public static final double KICK_TIME = 0.3;
    // Nombre del jugador.
    private String name;
    // Cantidad de goles anotados por el jugador en el partido actual.
    private int score;
    // Indica si el jugador se encuentra en el aire tras saltar.
    private boolean isJumping;
    // Puntos de vida actuales del jugador.
    private double life;
    // Nivel actual de carga de la barra de poder.
    private double powerBar;
    // Indica si el jugador tiene activada el aura lista para disparar el tiro especial.
    private boolean hasAura;
    // Tiempo restante de aturdimiento en segundos.
    private double stunTime;
    // Tiempo restante del efecto especial aplicado por un rival en segundos.
    private double effectTime;
    // Tiempo restante de espera antes de poder volver a patear.
    private double kickTime;
    // Nivel actual de la estadística de velocidad (1 a 10).
    private int speed;
    // Nivel actual de la estadística de salto (1 a 10).
    private int jumpStat;
    // Nivel actual de la estadística de pateo (1 a 10).
    private int kickStat;
    // Nivel actual de la estadística de supervivencia (1 a 10).
    private int survival;
    // Saldo total de puntos acumulados del jugador.
    private int points;
    // Puntos obtenidos por el jugador durante el partido en curso.
    private int matchPoints;
    // Indica si el jugador juega en el lado izquierdo (true) o derecho (false).
    private boolean leftSide; 
    // Multiplicador aplicado a la velocidad de desplazamiento horizontal.
    private double speedFactor;     
    // Indica si el jugador tiene permitido moverse horizontalmente.
    private boolean canMove;
    // Indica si el jugador tiene permitido saltar.
    private boolean canJump;
    // Personaje de país seleccionado para el jugador.
    private Character character;   
    // Personaje rival cuyo efecto especial está activo sobre este jugador.
    private Character affectedBy;   

    /**
     * Creates a new player with all attributes set to level 1 and full health.
     *
     * @param name      the name of the player
     * @param character the selected country character
     * @param leftSide  true if the player plays on the left side, false for the right side
     */
    // Crea un jugador con estadísticas iniciales en nivel 1, sin puntos y listo para iniciar partido.
    public Player(String name, Character character, boolean leftSide) {
        super(0, 0, WIDTH, HEIGHT);
        this.name = name;
        this.character = character;
        this.leftSide = leftSide;
        this.speed = 1;
        this.jumpStat = 1;
        this.kickStat = 1;
        this.survival = 1;
        this.points = 0;
        startMatch();
    }

    /**
     * Resets the player's match state with zero goals, an empty power bar,
     * maximum health, and no active effects.
     */
    // Reinicia goles, puntos del partido, barra de poder, temporizadores, vida máxima y efectos activos.
    public void startMatch() {
        score = 0;
        matchPoints = 0;
        powerBar = 0;
        hasAura = false;
        stunTime = 0;
        kickTime = 0;
        life = getMaxLife();
        clearEffect();
    }

    /**
     * Advances the player simulation by one frame, updating stun and effect
     * timers, charging the power bar, and applying movement and gravity.
     */
    // Actualiza temporizadores de aturdimiento, efecto, patada y carga de poder, y aplica gravedad y desplazamiento.
    @Override
    public void update() {
        if (stunTime > 0) {
            stunTime -= Match.DT;
            if (stunTime <= 0) {
                stunTime = 0;
                life = getMaxLife();  
            }
        }
        if (effectTime > 0) {
            effectTime -= Match.DT;
            if (effectTime <= 0) clearEffect();
        }
        chargePowerBar(Match.DT);
        if (kickTime > 0) {
            kickTime = Math.max(0, kickTime - Match.DT);
        }

        velY += GRAVITY;
        posX += (int) Math.round(velX);
        posY += (int) Math.round(velY);
        velX = 0; 
    }

    /**
     * Stops jumping when the player lands on the ground boundary.
     *
     * @param normalX the X direction of the boundary (-1, 0, or 1)
     * @param normalY the Y direction of the boundary (-1, 0, or 1)
     */
    // Detiene la velocidad en el eje chocado y marca que el jugador ya tocó el suelo si choca desde arriba.
    @Override
    public void hitBoundary(double normalX, double normalY) {
        super.hitBoundary(normalX, normalY);
        if (normalY < 0) isJumping = false;
    }

    /**
     * Moves the player to the left if they are allowed to act.
     */
    // Asigna velocidad horizontal hacia la izquierda según la velocidad del jugador y el factor activo.
    public void moveLeft() {
        if (!canAct()) return;
        velX = -getMoveSpeed() * speedFactor;
    }

    /**
     * Moves the player to the right if they are allowed to act.
     */
    // Asigna velocidad horizontal hacia la derecha según la velocidad del jugador y el factor activo.
    public void moveRight() {
        if (!canAct()) return;
        velX = getMoveSpeed() * speedFactor;
    }

    /**
     * Makes the player jump if they can act, are allowed to jump, and are on
     * the ground.
     */
    // Aplica impulso vertical hacia arriba escalado por el nivel de salto si el jugador puede saltar y no está en el aire.
    public void jump() {
        if (!canAct() || !canJump || isJumping) return;
        velY = -BASE_JUMP * (1 + 0.10 * (jumpStat - 1));
        isJumping = true;
    }

    /**
     * Kicks the ball if it is in front and within reach, or damages the rival
     * if the rival is within reach instead.
     *
     * @param rival the opposing player
     * @param ball  the match ball
     */
    // Inicia el enfriamiento de patada y golpea el balón al frente o daña al rival si está dentro del alcance.
    public void kick(Player rival, Ball ball) {
        if (!canAct() || isKicking()) return;   // no se puede patear otra vez hasta que termine la patada
        kickTime = KICK_TIME;
        if (inReach(ball) && (ball.getCenterX() - getCenterX()) * getAttackDirection() >= 0) {
            if (hasAura) {
                firePower(ball);
            } else {
                ball.setVelX(14 * getAttackDirection());
                ball.setVelY(-9);
            }
        } else if (inReach(rival)) {
            rival.receiveDamage(KICK_DAMAGE * getKickMultiplier());
        }
    }


    /**
     * Handles contact between the player's body or head and the ball, firing
     * the power shot if the aura is active.
     *
     * @param ball the match ball
     */
    // Dispara el tiro especial si el aura está activa o rebota el balón con la cabeza o el cuerpo.
    public void touchBall(Ball ball) {
        if (hasAura && canAct()) {
            firePower(ball);
            return;
        }
        if (ball.getCenterY() < posY + 15) {
            ball.setVelY(-Math.abs(ball.getVelY()) * 0.8 - 4);
            ball.setVelX(ball.getVelX() + 1.5 * getAttackDirection());
            ball.setPosY(posY - ball.getHeight());
        } else {
            int side;
            if (ball.getCenterX() < getCenterX()) {
                side = -1;
            } else {
                side = 1;
            }
            ball.setVelX(side * Math.max(Math.abs(ball.getVelX()), 5));
            if (side < 0) {
                ball.setPosX(posX - ball.getWidth());
            } else {
                ball.setPosX(posX + width);
            }
        }
    }

    /**
     * Increases the player's goal score by one.
     */
    // Incrementa en uno el marcador de goles del jugador.
    public void addGoal() {
        score++;
    }

    /**
     * Reduces the player's health by the given damage and stuns them if health
     * reaches zero.
     *
     * @param damage the amount of damage received
     */
    // Resta daño a la vida actual y activa el aturdimiento si la vida baja hasta cero.
    public void receiveDamage(double damage) {
        if (isStunned()) return;
        life = Math.max(0, life - damage);
        if (life == 0) stunTime = STUN_TIME;
    }

    /**
     * Activates the power aura when the power bar is full and the player is not
     * stunned.
     */
    // Consume la barra de poder llena y activa el aura para el próximo disparo o contacto con el balón.
    public void activatePower() {
        if (powerBar >= POWER_FULL && !hasAura && !isStunned()) {
            hasAura = true;
            powerBar = 0;
        }
    }

    /**
     * Charges the power bar according to the given number of seconds.
     *
     * @param seconds the elapsed charge time in seconds
     */
    // Incrementa la barra de poder proporcionalmente a los segundos transcurridos sin superar el máximo.
    public void chargePowerBar(double seconds) {
        powerBar = Math.min(POWER_FULL, powerBar + POWER_FULL * seconds / POWER_CHARGE_TIME);
    }

    /**
     * Checks whether the player is currently stunned.
     *
     * @return true if the player is stunned, false otherwise
     */
    // Indica si el jugador tiene tiempo de aturdimiento pendiente.
    public boolean isStunned() {
        return stunTime > 0;
    }

    /**
     * Returns the maximum health of the player based on their survival level.
     *
     * @return the maximum health value
     */
    // Calcula la vida máxima incrementada un 10 % por cada nivel de supervivencia sobre 1.
    public double getMaxLife() {
        return BASE_LIFE * (1 + 0.10 * (survival - 1));
    }

    /**
     * Returns the kick damage multiplier based on the player's kick level.
     *
     * @return the kick damage multiplier
     */
    // Calcula el multiplicador de daño de patada según el nivel de pateo.
    public double getKickMultiplier() {
        return 1 + 0.10 * (kickStat - 1);
    }

    /**
     * Increases the given attribute by one level using the player's saved points.
     *
     * @param stat the attribute name ("speed", "jump", "kick", or "survival")
     * @throws DopoSoccerException if the attribute name is invalid, already at max level, or points are insufficient
     */
    // Valida costo, tope de nivel y saldo de puntos para subir en un nivel la estadística solicitada.
    public void upgradeStat(String stat) throws DopoSoccerException {
        int cost = getUpgradeCost(stat);
        if (getStat(stat) >= MAX_STAT) throw new DopoSoccerException(DopoSoccerException.MAX_STAT_REACHED);
        if (points < cost) throw new DopoSoccerException(DopoSoccerException.NOT_ENOUGH_POINTS);
        points -= cost;
        switch (stat.toLowerCase()) {
            case "speed": speed++; break;
            case "jump": jumpStat++; break;
            case "kick": kickStat++; break;
            default: survival++; life = getMaxLife(); break;
        }
    }

    /**
     * Calculates the point cost needed to upgrade the given attribute.
     *
     * @param stat the attribute name ("speed", "jump", "kick", or "survival")
     * @return the required number of points
     * @throws DopoSoccerException if the attribute name is invalid or null
     */
    // Calcula el costo exponencial en puntos según el nivel actual de la estadística.
    public int getUpgradeCost(String stat) throws DopoSoccerException {
        return BASE_UPGRADE_COST * (int) Math.pow(2, getStat(stat) - 1);
    }

    /**
     * Returns the current level of the given attribute.
     *
     * @param stat the attribute name ("speed", "jump", "kick", or "survival")
     * @return the current attribute level
     * @throws DopoSoccerException if the attribute name is invalid or null
     */
    // Devuelve el nivel actual de la estadística indicada o lanza excepción si no existe.
    public int getStat(String stat) throws DopoSoccerException {
        if (stat == null) throw new DopoSoccerException(DopoSoccerException.INVALID_STAT);
        switch (stat.toLowerCase()) {
            case "speed": return speed;
            case "jump": return jumpStat;
            case "kick": return kickStat;
            case "survival": return survival;
            default: throw new DopoSoccerException(DopoSoccerException.INVALID_STAT);
        }
    }

    /**
     * Adds points to both the player's total balance and current match points.
     *
     * @param newPoints the number of points to add
     */
    // Suma los puntos recibidos al acumulado total y a los puntos ganados en el partido.
    public void addPoints(int newPoints) {
        points += newPoints;
        matchPoints += newPoints;
    }

    /**
     * Copies saved points and attribute levels from a stored profile into this
     * player.
     *
     * @param saved the player profile containing saved progress
     */
    // Copia puntos y niveles de atributos desde un perfil guardado y ajusta la vida al máximo correspondiente.
    public void copyProgress(Player saved) {
        points = saved.points;
        speed = saved.speed;
        jumpStat = saved.jumpStat;
        kickStat = saved.kickStat;
        survival = saved.survival;
        life = getMaxLife();
    }

    /**
     * Returns the horizontal distance in pixels the player moves per frame.
     *
     * @return the step size in pixels
     */
    // Calcula el desplazamiento horizontal entero en píxeles por cuadro.
    public int getStep() {
        return (int) Math.round(getMoveSpeed() * speedFactor);
    }

    /**
     * Applies a special power effect from a rival character, replacing any
     * previous active effect.
     *
     * @param source the character whose power shot hit this player
     */
    // Limpia efectos previos y registra el nuevo personaje que aplica efecto junto con su duración.
    public void setEffect(Character source) {
        clearEffect();
        affectedBy = source;
        effectTime = source.getEffectDuration();
    }

    /**
     * Clears any active power effect and restores normal movement and jumping.
     */
    // Restablece los modificadores de velocidad, movimiento y salto a su estado normal.
    public void clearEffect() {
        affectedBy = null;
        effectTime = 0;
        speedFactor = 1;
        canMove = true;
        canJump = true;
    }

    /**
     * Moves the player to the given coordinates and resets their velocity and
     * jump state.
     *
     * @param x the new X coordinate
     * @param y the new Y coordinate
     */
    // Ubica al jugador en (x, y) y reinicia velocidades y estado de salto.
    public void resetPosition(int x, int y) {
        posX = x;
        posY = y;
        velX = 0;
        velY = 0;
        isJumping = false;
    }

    /**
     * Returns the horizontal direction in which the player attacks.
     *
     * @return 1 if the player is on the left side, -1 if on the right side
     */
    // Devuelve 1 si el jugador ataca hacia la derecha o -1 si ataca hacia la izquierda.
    public int getAttackDirection() {
        if (leftSide) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Checks whether the player is able to move, jump, or kick.
     *
     * @return true if the player is not stunned and can move, false otherwise
     */
    // Comprueba si el jugador no está aturdido y tiene habilitado el movimiento.
    private boolean canAct() {
        return !isStunned() && canMove;
    }

    /**
     * Calculates the base movement speed from the player's speed level.
     *
     * @return the movement speed in pixels per frame
     */
    // Calcula la velocidad de desplazamiento según el nivel de velocidad del jugador.
    private double getMoveSpeed() {
        return BASE_SPEED * (1 + 0.10 * (speed - 1));
    }

    /**
     * Checks whether another entity is within the player's kick reach.
     *
     * @param e the target entity
     * @return true if the entity is within reach, false otherwise
     */
    // Comprueba si el área de otra entidad entra dentro del rango expandido de patada.
    private boolean inReach(Entity e) {
        return e.getPosX() < posX + width + KICK_REACH
            && e.getPosX() + e.getWidth() > posX - KICK_REACH
            && e.getPosY() < posY + height + KICK_REACH
            && e.getPosY() + e.getHeight() > posY - KICK_REACH;
    }

    /**
     * Launches the character's power shot on the ball and consumes the active aura.
     *
     * @param ball the match ball
     */
    // Desactiva el aura, marca al jugador como lanzador del tiro especial y ejecuta el disparo del personaje.
    private void firePower(Ball ball) {
        hasAura = false;
        ball.setPowerShooter(this);
        character.powerShot(ball);
    }

    /**
     * Returns the player's name.
     *
     * @return the player name
     */
    // Devuelve el nombre del jugador.
    public String getName() { return name; }

    /**
     * Returns the number of goals scored by the player in the current match.
     *
     * @return the current goal score
     */
    // Devuelve la cantidad de goles anotados en el partido actual.
    public int getScore() { return score; }

    /**
     * Checks whether the player is currently in the air from a jump.
     *
     * @return true if jumping, false otherwise
     */
    // Indica si el jugador está saltando actualmente.
    public boolean isJumping() { return isJumping; }

    /**
     * Returns the player's current health.
     *
     * @return the current health value
     */
    // Devuelve los puntos de vida actuales del jugador.
    public double getLife() { return life; }

    /**
     * Returns the current charge level of the player's power bar.
     *
     * @return the power bar value
     */
    // Devuelve la carga actual de la barra de poder.
    public double getPowerBar() { return powerBar; }

    /**
     * Checks whether the player has an active power aura ready to fire.
     *
     * @return true if the aura is active, false otherwise
     */
    // Indica si el jugador tiene activa el aura de poder.
    public boolean hasAura() { return hasAura; }

    /**
     * Returns the remaining stun duration in seconds.
     *
     * @return the remaining stun time
     */
    // Devuelve el tiempo de aturdimiento restante en segundos.
    public double getStunTime() { return stunTime; }

    /**
     * Returns the remaining duration of the active power effect in seconds.
     *
     * @return the remaining effect time
     */
    // Devuelve el tiempo restante del efecto activo en segundos.
    public double getEffectTime() { return effectTime; }

    /**
     * Returns the player's total saved points.
     *
     * @return the total points balance
     */
    // Devuelve el total de puntos guardados del jugador.
    public int getPoints() { return points; }

    /**
     * Returns the points earned by the player in the current match.
     *
     * @return the match points earned
     */
    // Devuelve los puntos acumulados en el partido actual.
    public int getMatchPoints() { return matchPoints; }

    /**
     * Checks whether the player plays on the left side of the stadium.
     *
     * @return true if on the left side, false if on the right side
     */
    // Indica si el jugador está en el lado izquierdo de la cancha.
    public boolean isLeftSide() { return leftSide; }

    /**
     * Returns the current movement speed multiplier affecting the player.
     *
     * @return the speed factor
     */
    // Devuelve el multiplicador de velocidad actual del jugador.
    public double getSpeedFactor() { return speedFactor; }

    /**
     * Checks whether the player is allowed to move horizontally.
     *
     * @return true if movement is allowed, false otherwise
     */
    // Indica si el jugador puede desplazarse horizontalmente.
    public boolean canMove() { return canMove; }

    /**
     * Checks whether the player is allowed to jump.
     *
     * @return true if jumping is allowed, false otherwise
     */
    // Indica si el jugador puede saltar.
    public boolean canJump() { return canJump; }

    /**
     * Returns the country character controlled by the player.
     *
     * @return the player's character
     */
    // Devuelve el personaje de país asignado al jugador.
    public Character getCharacter() { return character; }

    /**
     * Returns the character that applied the current effect to this player.
     *
     * @return the source character, or null if no effect is active
     */
    // Devuelve el personaje rival que aplicó el efecto actual, o null si no hay ninguno.
    public Character getAffectedBy() { return affectedBy; }

    /**
     * Checks whether the player is currently performing a kick.
     *
     * @return true if kicking, false otherwise
     */
    // Indica si la patada aún está en curso o en enfriamiento.
    public boolean isKicking() { return kickTime > 0; }

    /**
     * Sets whether the player is allowed to move horizontally.
     *
     * @param canMove true to allow movement, false to block it
     */
    // Habilita o bloquea el desplazamiento horizontal del jugador.
    public void setCanMove(boolean canMove) { this.canMove = canMove; }

    /**
     * Sets whether the player is allowed to jump.
     *
     * @param canJump true to allow jumping, false to block it
     */
    // Habilita o bloquea el salto del jugador.
    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    /**
     * Sets the movement speed multiplier affecting the player.
     *
     * @param speedFactor the new speed factor
     */
    // Establece el multiplicador aplicado a la velocidad del jugador.
    public void setSpeedFactor(double speedFactor) { this.speedFactor = speedFactor; }
}