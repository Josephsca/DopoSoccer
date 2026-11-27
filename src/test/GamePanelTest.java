
package test;

import domain.*;
import presentation.GamePanel;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GamePanelTest {

    private DopoSoccer game;
    private GamePanel panel;

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
    }

    private void prepareGame(
        String country1, String country2, String type
    ) throws Exception {

        game = new DopoSoccer();

        game.startNewMatch(
            "PanelTest1", country1,
            "PanelTest2", country2,
            type, 3, Stadium.MEDIUM
        );

        panel = new GamePanel(null, game);

        Stadium stadium = game.getCurrentMatch().getStadium();

        panel.setSize(
            stadium.getWidth(),
            stadium.getHeight()
        );
    }


    @Test
    public void shouldCreateGamePanel() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            assertTrue(panel.isFocusable());
            assertTrue(panel.getKeyListeners().length > 0);
        });
    }

    @Test
    public void shouldDrawWithoutMatch() throws Exception {
        runOnEDT(() -> {
            game = new DopoSoccer();
            panel = new GamePanel(null, game);
            panel.setSize(800, 600);

            assertNotNull(render());
        });
    }

    @Test
    public void shouldDrawGoalMatch() throws Exception {
        runOnEDT(() -> {
            prepareGame("Cameroon", "Nigeria", DopoSoccer.GOALS);

            BufferedImage image = render();

            assertEquals(panel.getWidth(), image.getWidth());
            assertEquals(panel.getHeight(), image.getHeight());
        });
    }

    @Test
    public void shouldDrawTimeMatch() throws Exception {
        runOnEDT(() -> {
            prepareGame("SouthKorea", "USA", DopoSoccer.TIME);

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawPausedMatch() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.TIME);

            BufferedImage before = render();

            game.pauseMatch();

            BufferedImage after = render();

            assertTrue(game.getCurrentMatch().isPaused());

            assertNotEquals(
                before.getRGB(400, 150),
                after.getRGB(400, 150)
            );
        });
    }

    @Test
    public void shouldDrawAvailableStadiums() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Cameroon", DopoSoccer.GOALS);

            String[] stadiums = GamePanel.availableStadiums();

            for (String stadium : stadiums) {
                panel.setStadium(stadium);
                assertDoesNotThrow(() -> render());
            }
        });
    }

    @Test
    public void shouldReturnSortedStadiums() throws Exception {
        runOnEDT(() -> {
            String[] stadiums = GamePanel.availableStadiums();
            String[] sorted = stadiums.clone();

            Arrays.sort(sorted);

            assertArrayEquals(sorted, stadiums);

            for (String name : stadiums) {
                assertFalse(name.endsWith(".png"));
            }
        });
    }

    @Test
    public void shouldDrawDefaultStadium() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            panel.setStadium(null);

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawPlayerWithFullPower() throws Exception {
        runOnEDT(() -> {
            prepareGame("Cameroon", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getLeftPlayer();

            player.chargePowerBar(Player.POWER_CHARGE_TIME);

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawActiveAura() throws Exception {
        runOnEDT(() -> {
            prepareGame("Cameroon", "SouthKorea", DopoSoccer.GOALS);

            Player left = game.getCurrentMatch().getLeftPlayer();
            Player right = game.getCurrentMatch().getRightPlayer();

            left.chargePowerBar(Player.POWER_CHARGE_TIME);
            right.chargePowerBar(Player.POWER_CHARGE_TIME);

            left.activatePower();
            right.activatePower();

            assertTrue(left.hasAura());
            assertTrue(right.hasAura());

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawLowLifePlayer() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getLeftPlayer();

            player.receiveDamage(player.getMaxLife() * 0.8);

            assertTrue(
                player.getLife() / player.getMaxLife() < 0.3
            );

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawPowerBall() throws Exception {
        runOnEDT(() -> {
            prepareGame("Nigeria", "USA", DopoSoccer.GOALS);

            Match match = game.getCurrentMatch();

            match.getBall().setPowerShooter(
                match.getLeftPlayer()
            );

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldDrawIllusionBalls() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Cameroon", DopoSoccer.GOALS);

            Ball ball = game.getCurrentMatch().getBall();

            ball.activateIllusion(3);

            assertTrue(ball.hasIllusion());
            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldJumpWithW() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            press(KeyEvent.VK_W);

            assertTrue(
                game.getCurrentMatch().getLeftPlayer().isJumping()
            );

            release(KeyEvent.VK_W);

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldKickWithS() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            press(KeyEvent.VK_S);

            assertTrue(
                game.getCurrentMatch().getLeftPlayer().isKicking()
            );

            release(KeyEvent.VK_S);

            assertDoesNotThrow(() -> render());
        });
    }

    @Test
    public void shouldActivatePowerWithQ() throws Exception {
        runOnEDT(() -> {
            prepareGame("Cameroon", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getLeftPlayer();

            player.chargePowerBar(Player.POWER_CHARGE_TIME);

            press(KeyEvent.VK_Q);

            assertTrue(player.hasAura());
        });
    }

    @Test
    public void shouldMoveLeftWithA() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getLeftPlayer();
            int initialX = player.getPosX();

            press(KeyEvent.VK_A);
            tick();
            release(KeyEvent.VK_A);

            assertTrue(player.getPosX() < initialX);
        });
    }

    @Test
    public void shouldMoveRightWithD() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getLeftPlayer();
            int initialX = player.getPosX();

            press(KeyEvent.VK_D);
            tick();
            release(KeyEvent.VK_D);

            assertTrue(player.getPosX() > initialX);
        });
    }


    @Test
    public void shouldJumpWithUpArrow() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            press(KeyEvent.VK_UP);

            assertTrue(
                game.getCurrentMatch().getRightPlayer().isJumping()
            );
        });
    }

    @Test
    public void shouldKickWithDownArrow() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            press(KeyEvent.VK_DOWN);

            assertTrue(
                game.getCurrentMatch().getRightPlayer().isKicking()
            );
        });
    }

    @Test
    public void shouldMoveSecondPlayer() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getRightPlayer();
            int initialX = player.getPosX();

            press(KeyEvent.VK_LEFT);
            tick();
            release(KeyEvent.VK_LEFT);

            assertTrue(player.getPosX() < initialX);

            int newX = player.getPosX();

            press(KeyEvent.VK_RIGHT);
            tick();
            release(KeyEvent.VK_RIGHT);

            assertTrue(player.getPosX() > newX);
        });
    }

    @Test
    public void shouldActivatePowerWithRightControl() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getRightPlayer();

            player.chargePowerBar(Player.POWER_CHARGE_TIME);

            press(
                KeyEvent.VK_CONTROL,
                KeyEvent.KEY_LOCATION_RIGHT
            );

            assertTrue(player.hasAura());
        });
    }

    @Test
    public void shouldIgnoreLeftControl() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Player player = game.getCurrentMatch().getRightPlayer();

            player.chargePowerBar(Player.POWER_CHARGE_TIME);

            press(
                KeyEvent.VK_CONTROL,
                KeyEvent.KEY_LOCATION_LEFT
            );

            assertFalse(player.hasAura());
        });
    }

    @Test
    public void shouldIgnoreRepeatedKeyPress() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            press(KeyEvent.VK_W);
            press(KeyEvent.VK_W);

            assertTrue(
                game.getCurrentMatch().getLeftPlayer().isJumping()
            );

            release(KeyEvent.VK_W);
        });
    }

    @Test
    public void shouldUpdateMatchOnTick() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            int initialTime = game.getCurrentMatch().getTimer();

            tick();

            assertEquals(
                initialTime + 1,
                game.getCurrentMatch().getTimer()
            );
        });
    }

    @Test
    public void shouldStartPanelTimer() throws Exception {
        runOnEDT(() -> {
            prepareGame("USA", "Nigeria", DopoSoccer.GOALS);

            Field field = GamePanel.class.getDeclaredField("timer");
            field.setAccessible(true);

            Timer timer = (Timer) field.get(panel);

            try {
                panel.start();
                assertTrue(timer.isRunning());
            } finally {
                timer.stop();
            }
        });
    }

    private BufferedImage render() {
        BufferedImage image = new BufferedImage(
            panel.getWidth(),
            panel.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = image.createGraphics();

        try {
            panel.paint(graphics);
        } finally {
            graphics.dispose();
        }

        return image;
    }

    private void press(int key) {
        press(key, KeyEvent.KEY_LOCATION_STANDARD);
    }

    private void press(int key, int location) {
        KeyEvent event = new KeyEvent(
            panel,
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED,
            location
        );

        for (KeyListener listener : panel.getKeyListeners()) {
            listener.keyPressed(event);
        }
    }

    private void release(int key) {
        KeyEvent event = new KeyEvent(
            panel,
            KeyEvent.KEY_RELEASED,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED
        );

        for (KeyListener listener : panel.getKeyListeners()) {
            listener.keyReleased(event);
        }
    }

    private void tick() throws Exception {
        Method method = GamePanel.class.getDeclaredMethod("tick");
        method.setAccessible(true);
        method.invoke(panel);
    }

    @FunctionalInterface
    private interface SwingTest {
        void run() throws Exception;
    }

    private void runOnEDT(SwingTest test) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                test.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }
}
