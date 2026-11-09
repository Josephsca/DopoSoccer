package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Arcade-styled button. 
 */
public class ArcadeButton extends JButton {

    private static final Color BACKGROUND = new Color(20, 20, 30);
    private static final Color HOVER = new Color(40, 50, 50);
    private static final Color TEXT = Color.WHITE;

    private boolean mouseOver;

    /**
     * Constructs a new arcade button with the specified text.
     * @param text the text displayed on the button
     */
    public ArcadeButton(String text) {
        super(text);
        mouseOver = false;
        setFont(new Font("Impact", Font.PLAIN, 22));        
        setForeground(TEXT);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(240, 48));
        prepareActions();
    }


    /**
     * Prepares mouse event listeners to handle hover effects.
     */
    private void prepareActions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint();
            }
        });
    }

    /**
     * Draws the background and white border of the button; the text is drawn by JButton.
     * @param g the graphics context used for painting
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color colorActual;
        if (mouseOver) {
            colorActual = HOVER;
        } else {
            colorActual = BACKGROUND;
        }
        g2.setColor(colorActual);
        g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
        g2.setColor(TEXT);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }
}
