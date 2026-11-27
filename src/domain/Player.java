package domain;

/**
 * Represents a match player. It controls a country character, tracks health
 * and power bar levels, and manages upgradeable attributes (speed, jump,
 * kick, and survival) from 1 to 10.
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
    /** Duration of a kick animation in seconds before the player can kick again. */
    public static final double KICK_TIME = 0.3;
    private String name;
    private int score;
    private boolean isJumping;
    private double life;
    private double powerBar;
    private boolean hasAura;
    private double stunTime;
    private double effectTime;
    private double kickTime;
    private int speed;
    private int jumpStat;
    private int kickStat;
    private int survival;
    private int points;
    private int matchPoints;
    private boolean leftSide; 
    private double speedFactor;     
    private boolean canMove;
    private boolean canJump;
    private Character character;   
    private Character affectedBy;   

    /**
     * Creates a new player with all attributes set to level 1 and full health.
     *
     * @param name      the name of the player
     * @param character the selected country character
     * @param leftSide  true if the player plays on the left side, false for the right side
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
     * Resets the player's match state with zero goals, an empty power bar,
     * maximum health, and no active effects.
     */
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
    @Override
    public void hitBoundary(double normalX, double normalY) {
        super.hitBoundary(normalX, normalY);
        if (normalY < 0) isJumping = false;
    }

    /**
     * Moves the player to the left if they are allowed to act.
     */
    public void moveLeft() {
        if (!canAct()) return;
        velX = -getMoveSpeed() * speedFactor;
    }

    /**
     * Moves the player to the right if they are allowed to act.
     */
    public void moveRight() {
        if (!canAct()) return;
        velX = getMoveSpeed() * speedFactor;
    }

    /**
     * Makes the player jump if they can act, are allowed to jump, and are on
     * the ground.
     */
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
    public void addGoal() {
        score++;
    }

    /**
     * Reduces the player's health by the given damage and stuns them if health
     * reaches zero.
     *
     * @param damage the amount of damage received
     */
    public void receiveDamage(double damage) {
        if (isStunned()) return;
        life = Math.max(0, life - damage);
        if (life == 0) stunTime = STUN_TIME;
    }

    /**
     * Activates the power aura when the power bar is full and the player is not
     * stunned.
     */
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
    public void chargePowerBar(double seconds) {
        powerBar = Math.min(POWER_FULL, powerBar + POWER_FULL * seconds / POWER_CHARGE_TIME);
    }

    /**
     * Checks whether the player is currently stunned.
     *
     * @return true if the player is stunned, false otherwise
     */
    public boolean isStunned() {
        return stunTime > 0;
    }

    /**
     * Returns the maximum health of the player based on their survival level.
     *
     * @return the maximum health value
     */
    public double getMaxLife() {
        return BASE_LIFE * (1 + 0.10 * (survival - 1));
    }

    /**
     * Returns the kick damage multiplier based on the player's kick level.
     *
     * @return the kick damage multiplier
     */
    public double getKickMultiplier() {
        return 1 + 0.10 * (kickStat - 1);
    }

    /**
     * Increases the given attribute by one level using the player's saved points.
     *
     * @param stat the attribute name ("speed", "jump", "kick", or "survival")
     * @throws DopoSoccerException if the attribute name is invalid, already at max level, or points are insufficient
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
     * Calculates the point cost needed to upgrade the given attribute.
     *
     * @param stat the attribute name ("speed", "jump", "kick", or "survival")
     * @return the required number of points
     * @throws DopoSoccerException if the attribute name is invalid or null
     */
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
    public int getStep() {
        return (int) Math.round(getMoveSpeed() * speedFactor);
    }

    /**
     * Applies a special power effect from a rival character, replacing any
     * previous active effect.
     *
     * @param source the character whose power shot hit this player
     */
    public void setEffect(Character source) {
        clearEffect();
        affectedBy = source;
        effectTime = source.getEffectDuration();
    }

    /**
     * Clears any active power effect and restores normal movement and jumping.
     */
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
    private boolean canAct() {
        return !isStunned() && canMove;
    }

    /**
     * Calculates the base movement speed from the player's speed level.
     *
     * @return the movement speed in pixels per frame
     */
    private double getMoveSpeed() {
        return BASE_SPEED * (1 + 0.10 * (speed - 1));
    }

    /**
     * Checks whether another entity is within the player's kick reach.
     *
     * @param e the target entity
     * @return true if the entity is within reach, false otherwise
     */
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
    public String getName() { return name; }

    /**
     * Returns the number of goals scored by the player in the current match.
     *
     * @return the current goal score
     */
    public int getScore() { return score; }

    /**
     * Checks whether the player is currently in the air from a jump.
     *
     * @return true if jumping, false otherwise
     */
    public boolean isJumping() { return isJumping; }

    /**
     * Returns the player's current health.
     *
     * @return the current health value
     */
    public double getLife() { return life; }

    /**
     * Returns the current charge level of the player's power bar.
     *
     * @return the power bar value
     */
    public double getPowerBar() { return powerBar; }

    /**
     * Checks whether the player has an active power aura ready to fire.
     *
     * @return true if the aura is active, false otherwise
     */
    public boolean hasAura() { return hasAura; }

    /**
     * Returns the remaining stun duration in seconds.
     *
     * @return the remaining stun time
     */
    public double getStunTime() { return stunTime; }

    /**
     * Returns the remaining duration of the active power effect in seconds.
     *
     * @return the remaining effect time
     */
    public double getEffectTime() { return effectTime; }

    /**
     * Returns the player's total saved points.
     *
     * @return the total points balance
     */
    public int getPoints() { return points; }

    /**
     * Returns the points earned by the player in the current match.
     *
     * @return the match points earned
     */
    public int getMatchPoints() { return matchPoints; }

    /**
     * Checks whether the player plays on the left side of the stadium.
     *
     * @return true if on the left side, false if on the right side
     */
    public boolean isLeftSide() { return leftSide; }

    /**
     * Returns the current movement speed multiplier affecting the player.
     *
     * @return the speed factor
     */
    public double getSpeedFactor() { return speedFactor; }

    /**
     * Checks whether the player is allowed to move horizontally.
     *
     * @return true if movement is allowed, false otherwise
     */
    public boolean canMove() { return canMove; }

    /**
     * Checks whether the player is allowed to jump.
     *
     * @return true if jumping is allowed, false otherwise
     */
    public boolean canJump() { return canJump; }

    /**
     * Returns the country character controlled by the player.
     *
     * @return the player's character
     */
    public Character getCharacter() { return character; }

    /**
     * Returns the character that applied the current effect to this player.
     *
     * @return the source character, or null if no effect is active
     */
    public Character getAffectedBy() { return affectedBy; }

    /**
     * Checks whether the player is currently performing a kick.
     *
     * @return true if kicking, false otherwise
     */
    public boolean isKicking() { return kickTime > 0; }

    /**
     * Sets whether the player is allowed to move horizontally.
     *
     * @param canMove true to allow movement, false to block it
     */
    public void setCanMove(boolean canMove) { this.canMove = canMove; }

    /**
     * Sets whether the player is allowed to jump.
     *
     * @param canJump true to allow jumping, false to block it
     */
    public void setCanJump(boolean canJump) { this.canJump = canJump; }

    /**
     * Sets the movement speed multiplier affecting the player.
     *
     * @param speedFactor the new speed factor
     */
    public void setSpeedFactor(double speedFactor) { this.speedFactor = speedFactor; }
}