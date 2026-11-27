
package test;

import domain.*;
import presentation.DopoSoccerGUI;
import presentation.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import javax.swing.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DopoSoccerGUITest {

    private DopoSoccerGUI gui;


    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
    }

    @Test
    public void shouldCreateMainWindow() throws Exception {
        runOnEDT(() -> {
            assertEquals("DopoSoccer", gui.getTitle());
            assertFalse(gui.isResizable());

            assertEquals(
                JFrame.EXIT_ON_CLOSE,
                gui.getDefaultCloseOperation()
            );
        });
    }

    @Test
    public void shouldShowMainMenuButtons() throws Exception {
        runOnEDT(() -> {
            assertTrue(button("enterToPlay").isVisible());
            assertTrue(button("loadGameButton").isVisible());
            assertTrue(button("upgradeButton").isVisible());
            assertTrue(button("soundButton").isVisible());

            assertFalse(button("playerVsPlayer").isVisible());
            assertFalse(button("playerVsMachine").isVisible());
            assertFalse(button("quickStart").isVisible());
        });
    }


    @Test
    public void shouldShowGameModes() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");

            assertTrue(button("playerVsPlayer").isVisible());
            assertTrue(button("playerVsMachine").isVisible());
            assertTrue(button("backToMenu").isVisible());

            assertFalse(button("enterToPlay").isVisible());
            assertFalse(button("upgradeButton").isVisible());
        });
    }

    @Test
    public void shouldReturnToMainMenu() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");
            click("backToMenu");

            assertTrue(button("enterToPlay").isVisible());
            assertFalse(button("playerVsPlayer").isVisible());
        });
    }

    @Test
    public void shouldShowPlayerSelectionOptions() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");
            click("playerVsPlayer");

            assertTrue(button("quickStart").isVisible());
            assertTrue(button("selectPlayers").isVisible());
            assertTrue(button("backToMode").isVisible());

            assertFalse(button("playerVsPlayer").isVisible());
        });
    }

    @Test
    public void shouldReturnToModeSelection() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");
            click("playerVsPlayer");
            click("backToMode");

            assertTrue(button("playerVsPlayer").isVisible());
            assertTrue(button("playerVsMachine").isVisible());
            assertFalse(button("quickStart").isVisible());
        });
    }

    @Test
    public void shouldOpenConfigurationScreen() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            assertTrue(container().getComponent(1).isVisible());
            assertFalse(container().getComponent(0).isVisible());
        });
    }

    @Test
    public void shouldReturnFromConfiguration() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            click("back");

            assertTrue(container().getComponent(0).isVisible());
            assertFalse(container().getComponent(1).isVisible());
        });
    }

    // SOUND

    @Test
    public void shouldToggleSound() throws Exception {
        runOnEDT(() -> {
            boolean initial = game().isSound();

            click("soundButton");

            assertEquals(!initial, game().isSound());

            assertEquals(
                game().isSound() ? "Sonido: ON" : "Sonido: OFF",
                button("soundButton").getText()
            );

            click("soundButton");

            assertEquals(initial, game().isSound());
        });
    }

    @Test
    public void shouldConfigureHumanPlayers() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            assertTrue(text("name1").isEnabled());
            assertTrue(text("name2").isEnabled());

            JPanel options = get("machineOptions");

            assertFalse(options.isVisible());
        });
    }

    @Test
    public void shouldConfigureRightMachine() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineSide").setSelectedItem("Derecha");

            JPanel options = get("machineOptions");

            assertTrue(options.isVisible());
            assertTrue(text("name1").isEnabled());
            assertFalse(text("name2").isEnabled());
        });
    }

    @Test
    public void shouldConfigureLeftMachine() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineSide").setSelectedItem("Izquierda");

            assertFalse(text("name1").isEnabled());
            assertTrue(text("name2").isEnabled());
        });
    }

    @Test
    public void shouldRestoreHumanConfiguration() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");
            click("playerVsMachine");
            click("backToMode");
            click("playerVsPlayer");

            assertTrue(text("name1").isEnabled());
            assertTrue(text("name2").isEnabled());

            JPanel options = get("machineOptions");

            assertFalse(options.isVisible());
        });
    }

    @Test
    public void shouldStartHumanGoalMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            text("name1").setText("Ana");
            text("name2").setText("Luis");

            combo("country1").setSelectedIndex(0);
            combo("country2").setSelectedIndex(3);

            combo("matchType").setSelectedIndex(1);

            JSpinner limit = get("limit");
            limit.setValue(5);

            combo("resolution").setSelectedItem(Stadium.MEDIUM);

            click("start");

            Match match = game().getCurrentMatch();

            assertNotNull(match);
            assertEquals("Ana", match.getLeftPlayer().getName());
            assertEquals("Luis", match.getRightPlayer().getName());

            assertTrue(
                match.getLeftPlayer().getCharacter()
                    instanceof SouthKorea
            );

            assertTrue(
                match.getRightPlayer().getCharacter()
                    instanceof USA
            );

            assertTrue(
                match.getWinCondition() instanceof GoalCondition
            );

            assertTrue(container().getComponent(2).isVisible());
        });
    }

    @Test
    public void shouldStartHumanTimeMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            combo("matchType").setSelectedIndex(0);

            JSpinner limit = get("limit");
            limit.setValue(3);

            click("start");

            assertTrue(
                game().getCurrentMatch().getWinCondition()
                    instanceof TimeCondition
            );
        });
    }

    @Test
    public void shouldStartQuickMatch() throws Exception {
        runOnEDT(() -> {
            click("enterToPlay");
            click("playerVsPlayer");
            click("quickStart");

            assertNotNull(game().getCurrentMatch());
            assertTrue(container().getComponent(2).isVisible());
        });
    }

    // MACHINE MATCHES

    @Test
    public void shouldStartRightMachineMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineSide").setSelectedItem("Derecha");
            combo("machineStrategy").setSelectedItem("Ofensiva");

            text("name1").setText("Human");

            click("start");

            Match match = game().getCurrentMatch();

            assertEquals("Human", match.getLeftPlayer().getName());

            assertTrue(match.getRightPlayer() instanceof Machine);

            Machine machine = (Machine) match.getRightPlayer();

            assertTrue(
                machine.getStrategy() instanceof OffensiveStrategy
            );
        });
    }

    @Test
    public void shouldStartLeftMachineMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineSide").setSelectedItem("Izquierda");
            combo("machineStrategy").setSelectedItem("Experta");

            text("name2").setText("Human");

            click("start");

            Match match = game().getCurrentMatch();

            assertTrue(match.getLeftPlayer() instanceof Machine);
            assertEquals("Human", match.getRightPlayer().getName());

            Machine machine = (Machine) match.getLeftPlayer();

            assertTrue(
                machine.getStrategy() instanceof ExpertStrategy
            );
        });
    }

    @Test
    public void shouldStartDefensiveMachineMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineStrategy").setSelectedItem("Defensiva");

            click("start");

            Machine machine = (Machine)
                game().getCurrentMatch().getRightPlayer();

            assertTrue(
                machine.getStrategy() instanceof DefensiveStrategy
            );
        });
    }

    @Test
    public void shouldStartClumsyMachineMatch() throws Exception {
        runOnEDT(() -> {
            openConfiguration(true);

            combo("machineStrategy").setSelectedItem("Torpe");

            click("start");

            Machine machine = (Machine)
                game().getCurrentMatch().getRightPlayer();

            assertTrue(
                machine.getStrategy() instanceof ClumsyStrategy
            );
        });
    }

    // RESOLUTIONS

    @Test
    public void shouldStartSmallStadium() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            combo("resolution").setSelectedItem(Stadium.SMALL);

            click("start");

            Stadium stadium = game().getCurrentMatch().getStadium();

            assertEquals(800, stadium.getWidth());
            assertEquals(450, stadium.getHeight());
        });
    }

    @Test
    public void shouldStartLargeStadium() throws Exception {
        runOnEDT(() -> {
            openConfiguration(false);

            combo("resolution").setSelectedItem(Stadium.LARGE);

            click("start");

            Stadium stadium = game().getCurrentMatch().getStadium();

            assertEquals(1280, stadium.getWidth());
            assertEquals(720, stadium.getHeight());
        });
    }

    // MENU RENDERING

    @Test
    public void shouldRenderMainMenu() throws Exception {
        runOnEDT(() -> {
            JPanel menu = (JPanel) container().getComponent(0);

            menu.setSize(800, 450);
            menu.doLayout();

            BufferedImage image = new BufferedImage(
                800, 450, BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D graphics = image.createGraphics();

            try {
                assertDoesNotThrow(() -> menu.paint(graphics));

                assertNotEquals(
                    0, image.getRGB(400, 100) >>> 24
                );
            } finally {
                graphics.dispose();
            }
        });
    }

    // AUXILIARY METHODS

    private void openConfiguration(boolean machine) throws Exception {
        click("enterToPlay");

        click(machine ? "playerVsMachine" : "playerVsPlayer");

        click("selectPlayers");
    }

    private void click(String name) throws Exception {
        button(name).doClick(0);
    }

    private JButton button(String name) throws Exception {
        return get(name);
    }

    private JTextField text(String name) throws Exception {
        return get(name);
    }

    private JComboBox<String> combo(String name) throws Exception {
        return get(name);
    }

    private JPanel container() throws Exception {
        return get("container");
    }

    private DopoSoccer game() throws Exception {
        return get("game");
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String name) throws Exception {
        Field field = DopoSoccerGUI.class.getDeclaredField(name);

        field.setAccessible(true);

        return (T) field.get(gui);
    }

    private void stopTimer() throws Exception {
        GamePanel panel = get("gamePanel");

        Field field = GamePanel.class.getDeclaredField("timer");

        field.setAccessible(true);

        Timer timer = (Timer) field.get(panel);

        timer.stop();
    }

    @FunctionalInterface
    private interface GUITest {
        void run() throws Exception;
    }

    private void runOnEDT(GUITest test) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                gui = new DopoSoccerGUI();

                test.run();

            } catch (Exception e) {
                throw new RuntimeException(e);

            } finally {
                if (gui != null) {
                    try {
                        stopTimer();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        gui.dispose();
                    }
                }
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
