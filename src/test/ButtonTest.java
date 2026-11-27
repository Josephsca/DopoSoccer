
package test;

import presentation.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ButtonTest {


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
    public void shouldCreateButton() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Button button = new Button("Jugar");

            assertEquals("Jugar", button.getText());
            assertEquals(new Dimension(240, 48),
                button.getPreferredSize());
            assertEquals(Color.WHITE, button.getForeground());
            assertFalse(button.isFocusPainted());
            assertFalse(button.isBorderPainted());
            assertFalse(button.isContentAreaFilled());
            assertEquals(Cursor.HAND_CURSOR,
                button.getCursor().getType());
        });
    }

    @Test
    public void shouldExecuteButtonAction() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Button button = new Button("Jugar");
            boolean[] clicked = {false};

            button.addActionListener(e -> clicked[0] = true);
            button.doClick(0);

            assertTrue(clicked[0]);
        });
    }

    @Test
    public void shouldChangeColorOnMouseEnter() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Button button = new Button("Jugar");
            button.setSize(240, 48);

            Color initial = renderColor(button);

            button.dispatchEvent(new MouseEvent(
                button, MouseEvent.MOUSE_ENTERED,
                System.currentTimeMillis(), 0,
                10, 10, 0, false
            ));

            Color hover = renderColor(button);

            assertEquals(new Color(20, 20, 30), initial);
            assertEquals(new Color(40, 50, 50), hover);
        });
    }

    @Test
    public void shouldRestoreColorOnMouseExit() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Button button = new Button("Jugar");
            button.setSize(240, 48);

            button.dispatchEvent(new MouseEvent(
                button, MouseEvent.MOUSE_ENTERED,
                System.currentTimeMillis(), 0,
                10, 10, 0, false
            ));

            button.dispatchEvent(new MouseEvent(
                button, MouseEvent.MOUSE_EXITED,
                System.currentTimeMillis(), 0,
                250, 50, 0, false
            ));

            assertEquals(
                new Color(20, 20, 30),
                renderColor(button)
            );
        });
    }

    private Color renderColor(Button button) {
        BufferedImage image = new BufferedImage(
            240, 48, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = image.createGraphics();

        try {
            button.paintComponent(graphics);
        } finally {
            graphics.dispose();
        }

        return new Color(image.getRGB(20, 24), true);
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
