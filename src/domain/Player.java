package domain;

/**
 * Represents a match player. Controls a character (country), has health, a power bar,
 * and attributes (speed, jump, kick, survival) ranging from 1 to 10.
 */
public class Player extends Entity {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 80;
    public static final double BASE_LIFE = 100;
    public static final double KICK_DAMAGE = 10;
    public static final double POWER_FULL = 100;
    public static final double POWER_CHARGE_TIME = 10;  
    public static final double STUN_TIME = 3;            
    public static final int MAX_STAT = 10;
    public static final int BASE_UPGRADE_COST = 500;
    public static final double BASE_SPEED = 5;
    public static final double BASE_JUMP = 13;
    public static final double GRAVITY = 0.7;
    public static final int KICK_REACH = 25;
    private String name;
    private int score;
    private boolean isJumping;
    private double life;
    private double powerBar;
    private boolean hasAura;
    private double stunTime;
    private double effectTime;
    private int speed;
    private int jumpStat;
    private int kickStat;
    private int survival;
    private int points;
    private boolean leftSide; 
    private double speedFactor;     
    private boolean canMove;
    private boolean canJump;
    private Character character;   
    private Character affectedBy;   

    /**
     * Constructs a new player with all attributes set to 1 and maximum health.
     * @param name the name of the player
     * @param character the chosen character (country)
     * @param leftSide true if the player plays on the left side
     */
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
     * Prepares the player to start a match with a zero score, empty power bar, maximum health, and no active effects.
     */
    public void startMatch() {
        score = 0;
        powerBar = 0;
        hasAura = false;
        stunTime = 0;
        life = getMaxLife();
        clearEffect();
    }

    /**
     * Advances the player simulation by one frame, handling stun and effect timers, power bar charging, and movement.
     */
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

        velY += GRAVITY;
        posX += (int) Math.round(velX);
        posY += (int) Math.round(velY);
        velX = 0; 
    }

    /**
     * Stops jumping when the player touches the ground boundary.
     * @param normalX the X component of the boundary normal direction (-1, 0, or 1)
     * @param normalY the Y component of the boundary normal direction (-1, 0, or 1)
     */
    @Override
    public void hitBoundary(double normalX, double normalY) {
        super.hitBoundary(normalX, normalY);
        if (normalY < 0) isJumping = false;
    }

    /**
     * Moves the player to the left if action is permitted.
     */
    public void moveLeft() {
        if (!canAct()) return;
        velX = -getMoveSpeed() * speedFactor;
    }

    /**
     * Moves the player to the right if action is permitted.
     */
    public void moveRight() {
        if (!canAct()) return;
        velX = getMoveSpeed() * speedFactor;
    }

    /**
     * Makes the player jump if action is permitted and they are not already jumping.
     */
    public void jump() {
        if (!canAct() || !canJump || isJumping) return;
        velY = -BASE_JUMP * (1 + 0.10 * (jumpStat - 1));
        isJumping = true;
    }

    /**
     * Performs a kick action. If the ball is within reach, it propels the ball;
     * otherwise, if the rival is within reach, it deals damage to the rival.
     * @param rival the opposing player
     * @param ball the match ball
     */
    public void kick(Player rival, Ball ball) {
        if (!canAct()) return;
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
     * Handles the player touching the ball with their body or head.
     * If the aura is active, the power shot is executed.
     * @param ball the match ball
     */
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
     * Increments the player's score by one goal.
     */
    public void addGoal() {
        score++;
    }

    /**
     * Receives damage. Health never drops below 0; if it reaches 0, the player becomes stunned.
     * @param damage the amount of damage received
     */
    public void receiveDamage(double damage) {
        if (isStunned()) return;
        life = Math.max(0, life - damage);
        if (life == 0) stunTime = STUN_TIME;
    }

    /**
     * Activates the special power if the power bar is full and the player has no active aura or stun.
     */
    public void activatePower() {
        if (powerBar >= POWER_FULL && !hasAura && !isStunned()) {
            hasAura = true;
            powerBar = 0;
        }
    }

    /**
     * Charges the power bar based on elapsed seconds.
     * @param seconds the seconds of charge time
     */
    public void chargePowerBar(double seconds) {
        powerBar = Math.min(POWER_FULL, powerBar + POWER_FULL * seconds / POWER_CHARGE_TIME);
    }

    /**
     * Checks whether the player is currently stunned.
     * @return true if stunned, false otherwise
     */
    public boolean isStunned() {
        return stunTime > 0;
    }

    /**
     * Gets the maximum health limit based on the survival stat.
     * @return the maximum life value
     */
    /** vidaMax = 100 * (1 + 0.10 * (Survival - 1)) */
    public double getMaxLife() {
        return BASE_LIFE * (1 + 0.10 * (survival - 1));
    }

    /**
     * Gets the kick damage multiplier based on the kick stat.
     * @return the kick multiplier value
     */
    /** multiplicadorKick = 1 + 0.10 * (Kick - 1) */
    public double getKickMultiplier() {
        return 1 + 0.10 * (kickStat - 1);
    }

    /**
     * Upgrades a specified attribute by 1 point using the player's points.
     * @param stat the name of the attribute to upgrade ("speed", "jump", "kick", or "survival")
     * @throws DopoSoccerException if the stat does not exist, has reached the maximum level of 10, or points are insufficient
     */
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
     * Calculates the cost for the next upgrade of a specified attribute.
     * @param stat the name of the attribute
     * @return the upgrade cost value
     * @throws DopoSoccerException if the stat is invalid
     */
    public int getUpgradeCost(String stat) throws DopoSoccerException {
        return BASE_UPGRADE_COST * (int) Math.pow(2, getStat(stat) - 1);
    }

    /**
     * Gets the current value of a specified attribute.
     * @param stat the name of the attribute
     * @return the current attribute level
     * @throws DopoSoccerException if the stat is invalid or null
     */
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
     * Adds points to the player's currency balance.
     * @param newPoints the number of points to add
     */
    public void addPoints(int newPoints) {
        points += newPoints;
    }

    /**
     * Applies a new effect to the player. Effects do not stack; the new one replaces the previous one.
     * @param source the character whose power shot caused the effect
     */
    public void setEffect(Character source) {
        clearEffect();
        affectedBy = source;
        effectTime = source.getEffectDuration();
    }

    /**
     * Removes any active effect and restores the player to normal status.
     */
    public void clearEffect() {
        affectedBy = null;
        effectTime = 0;
        speedFactor = 1;
        canMove = true;
        canJump = true;
    }

    /**
     * Resets the player to a stationary state at a specific position.
     * @param x the new X coordinate
     * @param y the new Y coordinate
     */
    public void resetPosition(int x, int y) {
        posX = x;
        posY = y;
        velX = 0;
        velY = 0;
        isJumping = false;
    }

    /**
     * Gets the direction of the player's attack.
     * @return 1 if the player is on the left side, -1 if on the right side
     */
    public int getAttackDirection() {
        if (leftSide) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Checks if the player can perform an action.
     * @return true if the player can act, false otherwise
     */
    private boolean canAct() {
        return !isStunned() && canMove;
    }

    /**
     * Calculates the movement speed based on the speed stat.
     * @return the calculated movement speed
     */
    private double getMoveSpeed() {
        return BASE_SPEED * (1 + 0.10 * (speed - 1));
    }

    /**
     * Checks whether another entity is within the player's reach.
     * @param e the target entity
     * @return true if within reach, false otherwise
     */
    private boolean inReach(Entity e) {
        return e.getPosX() < posX + width + KICK_REACH
            && e.getPosX() + e.getWidth() > posX - KICK_REACH
            && e.getPosY() < posY + height + KICK_REACH
            && e.getPosY() + e.getHeight() > posY - KICK_REACH;
    }

    /**
     * Triggers the power shot on the ball and resets the aura.
     * @param ball the match ball
     */
    private void firePower(Ball ball) {
        hasAura = false;
        ball.setPowerShooter(this);
        character.powerShot(ball);
    }

   /**
     * Gets the player's name.
     * @return the name string
     */
    public String getName() { return name; }

    /**
     * Gets the player's current goal score.
     * @return the score value
     */
    public int getScore() { return score; }

    /**
     * Checks whether the player is currently jumping.
     * @return true if jumping, false otherwise
     */
    public boolean isJumping() { return isJumping; }

    /**
     * Gets the player's current health.
     * @return the life value
     */
    public double getLife() { return life; }

    /**
     * Gets the player's power bar progress value.
     * @return the power bar value
     */
    public double getPowerBar() { return powerBar; }

    /**
     * Checks whether the player has an active power aura.
     * @return true if the aura is active, false otherwise
     */
    public boolean hasAura() { return hasAura; }

    /**
     * Gets the remaining stun time in seconds.
     * @return the stun time value
     */
    public double getStunTime() { return stunTime; }

    /**
     * Gets the remaining effect time in seconds.
     * @return the effect time value
     */
    public double getEffectTime() { return effectTime; }

    /**
     * Gets the player's accumulated points currency.
     * @return the points value
     */
    public int getPoints() { return points; }

    /**
     * Checks whether the player is on the left side of the field.
     * @return true if on the left side, false otherwise
     */
    public boolean isLeftSide() { return leftSide; }

    /**
     * Gets the current speed factor multiplier affecting the player.
     * @return the speed factor value
     */
    public double getSpeedFactor() { return speedFactor; }

    /**
     * Checks whether movement is allowed.
     * @return true if movement is allowed, false otherwise
     */
    public boolean canMove() { return canMove; }

    /**
     * Checks whether jumping is allowed.
     * @return true if jumping is allowed, false otherwise
     */
    public boolean canJump() { return canJump; }

    /**
     * Gets the character associated with this player.
     * @return the Character object
     */
    public Character getCharacter() { return character; }

    /**
     * Gets the character that caused the active effect on this player.
     * @return the Character source object, or null if unaffected
     */
    public Character getAffectedBy() { return affectedBy; }

    /**
     * Checks whether the player is currently moving or kicking.
     * @return true if moving horizontally, false otherwise
     */
    public boolean isKicking() { return velX != 0; }

    /**
     * Sets whether movement is permitted.
     * @param canMove true to allow movement, false to restrict it
     */
    public void setCanMove(boolean canMove) { this.canMove = canMove; }

    /**
     * Sets whether jumping is permitted.
     * @param canJump true to allow jumping, false to restrict it
     */
    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    /**
     * Sets the speed factor multiplier for the player.
     * @param speedFactor the new speed factor value
     */
    public void setSpeedFactor(double speedFactor) { this.speedFactor = speedFactor; }
}