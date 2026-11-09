package presentation;

import domain.Ball;
import domain.Goal;
import domain.DopoSoccer;
import domain.Match;
import domain.Player;
import domain.Stadium;
import domain.TimeCondition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Panel where the match is drawn and keyboard inputs are read.
 * 
 * Images (optional, see images/LEEME.txt). If an image does not exist,
 * the next option is used and finally a simple shape:
 *   Stadium: images/stadiums/[name].png -> images/stadium.png -> drawn field
 *   Player:  images/[Country]/[pose].png -> images/[Country]/idle.png -> images/[Country].png -> shape
 *            poses: idle, walk1, walk2, jump, kick, stunned
 *   Ball:    images/ball.png
 * Characters are drawn facing right; player 2 is flipped automatically.
 */
public class GamePanel extends JPanel {

    private static final String IMAGES = "images" + File.separator;
    private static final int WALK_FRAME_TICKS = 8;   

    private DopoSoccerGUI gui;
    private DopoSoccer game;
    private Timer timer;
    private Set<Integer> pressed;
    private Map<String, BufferedImage> images;
    private Map<Player, Integer> lastX;
    private String stadiumName;
    private int frames;

    /**
     * Constructs a new GamePanel linked to the main GUI and game controller.
     * @param gui the main application window
     * @param game the game domain controller
     */
    public GamePanel(DopoSoccerGUI gui, DopoSoccer game) {
        this.gui = gui;
        this.game = game;
        this.pressed = new HashSet<>();
        this.images = new HashMap<>();
        this.lastX = new HashMap<>();
        this.stadiumName = null;
        this.frames = 0;
        setFocusable(true);
        setBackground(Color.BLACK);
        prepareKeys();
        timer = new Timer(1000 / Match.FPS, e -> tick());
    }

    /**
     * Gets the names of available stadiums (.png files in images/stadiums).
     * @return an array of available stadium names
     */
    public static String[] availableStadiums() {
        File folder = new File(IMAGES + "stadiums");
        String[] files = folder.list((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null) return new String[0];
        java.util.Arrays.sort(files);
        for (int i = 0; i < files.length; i++) files[i] = files[i].substring(0, files[i].length() - 4);
        return files;
    }

    /**
     * Sets the stadium to be drawn (null for the basic field).
     * @param stadiumName the name of the stadium
     */
    public void setStadium(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    /**
     * Starts running the current match.
     */
    public void start() {
        pressed.clear();
        lastX.clear();
        timer.start();
        requestFocusInWindow();
    }

    /**
     * Processes one game frame: reads held keys, updates the domain state, and triggers a repaint.
     */
    private void tick() {
        if (pressed.contains(KeyEvent.VK_A)) game.moveLeft(1);
        if (pressed.contains(KeyEvent.VK_D)) game.moveRight(1);
        if (pressed.contains(KeyEvent.VK_LEFT)) game.moveLeft(2);
        if (pressed.contains(KeyEvent.VK_RIGHT)) game.moveRight(2);
        Match current = game.getCurrentMatch();
        if (current != null && frames % WALK_FRAME_TICKS == 0) {
            lastX.put(current.getLeftPlayer(), current.getLeftPlayer().getPosX());
            lastX.put(current.getRightPlayer(), current.getRightPlayer().getPosX());
        }
        game.update();
        frames++;
        repaint();
        Match match = game.getCurrentMatch();
        if (match != null && match.isGameOver()) {
            timer.stop();
            pressed.clear();
            gui.showWinner();
        }
    }

    /**
     * Prepares key event listeners for player movement, actions, and menu shortcuts.
     */
    private void prepareKeys() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                boolean firstPress = !pressed.contains(code);
                pressed.add(code);
                if (!firstPress) return;   
                switch (code) {
                    case KeyEvent.VK_W: game.jump(1); break;
                    case KeyEvent.VK_S: game.kick(1); break;
                    case KeyEvent.VK_Q: game.activatePower(1); break;
                    case KeyEvent.VK_UP: game.jump(2); break;
                    case KeyEvent.VK_DOWN: game.kick(2); break;
                    case KeyEvent.VK_CONTROL:
                        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) game.activatePower(2);
                        break;
                    case KeyEvent.VK_P:
                    case KeyEvent.VK_ESCAPE:
                        pressed.clear();
                        gui.showPauseMenu();
                        break;
                    default: break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressed.remove(e.getKeyCode());
            }
        });
    }

    /**
     * Searches for an image (the first existing option from the given paths).
     * Images are loaded only once and cached.
     * @param options paths inside images/, without ".png"
     * @return the loaded BufferedImage, or null if none exist
     */
    private BufferedImage getImage(String... options) {
        for (String option : options) {
            if (option == null) continue;
            if (!images.containsKey(option)) {
                BufferedImage image = null;
                File file = new File(IMAGES + option.replace("/", File.separator) + ".png");
                if (file.exists()) {
                    try {
                        image = ImageIO.read(file);
                    } catch (Exception e) {
                        image = null;   
                    }
                }
                images.put(option, image);
            }
            if (images.get(option) != null) return images.get(option);
        }
        return null;
    }

    /**
     * Determines the player pose based on their state in the domain.
     * @param p the player object
     * @return the pose name string
     */
    private String pose(Player p) {
        boolean moving = lastX.containsKey(p) && lastX.get(p) != p.getPosX();
        if (p.isStunned()) return "stunned";
        if (p.isKicking()) return "kick";
        if (p.isJumping()) return "jump";
        if (moving) return (frames / WALK_FRAME_TICKS) % 2 == 0 ? "walk1" : "walk2";
        return "idle";
    }

    /**
     * Renders all match components including stadium, referee, goals, players, ball, and HUD.
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Match match = game.getCurrentMatch();
        if (match == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawStadium(g2, match.getStadium());

        BufferedImage referee = getImage("referee");
        if (referee != null) {
            int rh = 60;
            int rw = referee.getWidth() * rh / referee.getHeight();
            int rx = match.getStadium().getWidth() / 2 - rw / 2;
            int ry = match.getStadium().getGroundY() - rh;
            g2.drawImage(referee, rx, ry, rw, rh, null);
        }

        drawGoal(g2, match.getLeftGoal());
        drawGoal(g2, match.getRightGoal());
        drawPlayer(g2, match.getLeftPlayer());
        drawPlayer(g2, match.getRightPlayer());
        drawBall(g2, match.getBall());
        drawHud(g2, match);
    }

    /**
     * Draws the stadium background or a default field if no image is found.
     * @param g the graphics context
     * @param stadium the stadium object
     */
    private void drawStadium(Graphics2D g, Stadium stadium) {
        int w = stadium.getWidth();
        int h = stadium.getHeight();
        BufferedImage image = getImage(stadiumName == null ? null : "stadiums/" + stadiumName, "stadium");
        if (image != null) {
            g.drawImage(image, 0, 0, w, h, null);
            return;
        }
        g.setPaint(new GradientPaint(0, 0, new Color(90, 160, 230), 0, h, new Color(190, 225, 250)));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(60, 150, 60));
        g.fillRect(0, stadium.getGroundY(), w, h - stadium.getGroundY());
        g.setColor(Color.WHITE);
        g.drawLine(0, stadium.getGroundY(), w, stadium.getGroundY());
        g.drawLine(w / 2, stadium.getGroundY(), w / 2, h);
    }

    /**
     * Draws a goal net and crossbar structure.
     * @param g the graphics context
     * @param goal the goal object
     */
    private void drawGoal(Graphics2D g, Goal goal) {
        int x = goal.getPosX();
        int y = goal.getPosY();
        g.setColor(new Color(255, 255, 255, 90));
        for (int i = 0; i < goal.getWidth(); i += 10) g.drawLine(x + i, y, x + i, y + goal.getHeight());
        for (int j = 0; j < goal.getHeight(); j += 10) g.drawLine(x, y + j, x + goal.getWidth(), y + j);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(5));
        g.drawLine(x, y, x + goal.getWidth(), y);  
        int postX = goal.isLeftGoal() ? x + goal.getWidth() : x;
        g.drawLine(postX, y, postX, y + goal.getHeight());  
        g.setStroke(new BasicStroke(1));
    }

    /**
     * Draws a player sprite or placeholder shape, along with active status effects.
     * @param g the graphics context
     * @param p the player object
     */
    private void drawPlayer(Graphics2D g, Player p) {
        int x = p.getPosX();
        int y = p.getPosY();
        int w = p.getWidth();
        int h = p.getHeight();
        String country = p.getCharacter().getCountry();
        Color[] colors = countryColors(country);

        if (p.hasAura()) {
            g.setColor(new Color(colors[0].getRed(), colors[0].getGreen(), colors[0].getBlue(), 110));
            g.fillOval(x - 8, y - 8, w + 8, h + 8);
        }

        BufferedImage image = getImage(country + "/" + pose(p), country + "/idle", country);
        if (image != null) {
            if (p.isLeftSide()) g.drawImage(image, x, y, w, h, null);
            else g.drawImage(image, x + w, y, -w, h, null);
        }
    }

    /**
     * Draws the ball sprite or default circular representation with power shot glow effects.
     * @param g the graphics context
     * @param ball the ball object
     */
    private void drawBall(Graphics2D g, Ball ball) {
        int x = ball.getPosX();
        int y = ball.getPosY();
        int d = ball.getWidth();
        if (ball.getPowerShooter() != null) {
            Color c = countryColors(ball.getPowerShooter().getCharacter().getCountry())[0];
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 140));
            g.fillOval(x - 8, y - 8, d + 16, d + 16);
        }
        BufferedImage image = getImage("ball");
        if (image != null) {
            g.drawImage(image, x, y, d, d, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(x, y, d, d);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, d, d);
            g.fillOval(x + d / 3, y + d / 3, d / 3, d / 3);
        }
    }

    /**
     * Draws the score, match timer, player info bars, and pause overlay.
     * @param g the graphics context
     * @param match the match object
     */
    private void drawHud(Graphics2D g, Match match) {
        int w = match.getStadium().getWidth();
        Player left = match.getLeftPlayer();
        Player right = match.getRightPlayer();

        String score = left.getName() + ": " + left.getScore() + "  -  " + right.getName() + ": " + right.getScore();
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        int scoreWidth = g.getFontMetrics().stringWidth(score);
        g.setColor(Color.black);
        g.fillRoundRect(w / 2 - scoreWidth / 2 - 12, 8, scoreWidth + 24, 56, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(score, w / 2 - scoreWidth / 2, 32);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        String time = timeText(match);
        g.drawString(time, w / 2 - g.getFontMetrics().stringWidth(time) / 2, 55);

        drawPlayerInfo(g, left, 12);
        drawPlayerInfo(g, right, w - 212);

        if (match.isPaused()) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, w, match.getStadium().getHeight());
        }
    }

    /**
     * Draws individual player status cards including health, power bar, stats, and points.
     * @param g the graphics context
     * @param p the player object
     * @param x the X coordinate offset for the info card
     */
    private void drawPlayerInfo(Graphics2D g, Player p, int x) {
        g.setColor(Color.black);
        g.fillRoundRect(x, 8, 200, 94, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString(p.getName() + " (" + p.getCharacter().getCountry() + ")", x + 8, 25);

        // vida
        double lifeRatio = p.getLife() / p.getMaxLife();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 8, 32, 184, 12);
        g.setColor(lifeRatio > 0.3 ? new Color(60, 200, 60) : Color.RED);
        g.fillRect(x + 8, 32, (int) (184 * lifeRatio), 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString("Vida " + (int) p.getLife() + "/" + (int) p.getMaxLife(), x + 12, 42);

        // barra de poder
        double powerRatio = p.getPowerBar() / Player.POWER_FULL;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 8, 50, 184, 12);
        g.setColor(powerRatio >= 1 ? Color.ORANGE : new Color(80, 140, 255));
        g.fillRect(x + 8, 50, (int) (184 * powerRatio), 12);
        g.setColor(Color.WHITE);
        String power = p.hasAura() ? "AURA ACTIVA" : (powerRatio >= 1 ? "PODER LISTO" : "Poder");
        g.drawString(power, x + 12, 60);

        // estadísticas y puntos
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        try {
            g.drawString("SPEED " + p.getStat("speed") + "  JUMP " + p.getStat("jump")
                + "  KICK " + p.getStat("kick") + "  SURV " + p.getStat("survival"), x + 8, 78);
        } catch (Exception e) {
        }
        g.drawString("Puntos: " + p.getPoints(), x + 8, 92);
    }

    /**
     * Formats the match time string according to the win condition rules.
     * @param match the match object
     * @return the formatted time string
     */
    private String timeText(Match match) {
        if (match.getWinCondition() instanceof TimeCondition) {
            TimeCondition condition = (TimeCondition) match.getWinCondition();
            if (condition.isSuddenDeath()) return "MUERTE SÚBITA";
            int seconds = condition.getRemainingSeconds(match);
            return String.format("%d:%02d", seconds / 60, seconds % 60);
        }
        int seconds = match.getSeconds();
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * Gets the theme colors for each country: [shirt/aura, hair].
     * @param country the country name string
     * @return an array of Color objects
     */
    private Color[] countryColors(String country) {
        switch (country) {
            case "SouthKorea": return new Color[]{new Color(220, 30, 50), new Color(30, 30, 30)};
            case "Cameroon": return new Color[]{new Color(0, 140, 70), new Color(250, 210, 0)};
            case "Nigeria": return new Color[]{new Color(0, 160, 80), new Color(255, 255, 255)};
            case "USA": return new Color[]{new Color(30, 50, 140), new Color(200, 30, 40)};
            default: return new Color[]{Color.GRAY, Color.BLACK};
        }
    }

    /**
     * Gets the display name for each country's special effect.
     * @param country the country name string
     * @return the effect name string
     */
    private String effectName(String country) {
        switch (country) {
            case "SouthKorea": return "EMPUJADO";
            case "Cameroon": return "ELECTROCUTADO";
            case "Nigeria": return "INCONSCIENTE";
            case "USA": return "CONFUNDIDO";
            default: return "";
        }
    }
}
