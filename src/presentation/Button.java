package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Represents a custom styled arcade button with a rounded background, border,
 * and hover effect.
 */
public class Button extends JButton {

    // Color de fondo normal del botón.
    private static final Color BACKGROUND = new Color(20, 20, 30);
    // Color de fondo cuando el cursor pasa por encima del botón.
    private static final Color HOVER = new Color(40, 50, 50);
    // Color usado para el texto y el borde del botón.
    private static final Color TEXT = Color.WHITE;

    // Indica si el ratón está actualmente sobre el botón.
    private boolean mouseOver;

    /**
     * Creates a new arcade button with the given label text and visual style.
     *
     * @param text the text displayed on the button
     */
    // Configura la apariencia, fuente, cursor y tamaño del botón e instala sus eventos.
    public Button(String text) {
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
     * Attaches mouse listeners to update the hover state when the cursor
     * enters or leaves the button.
     */
    // Registra los oyentes de ratón para cambiar el estado visual al entrar o salir.
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
     * Paints the rounded background and white border, then lets JButton draw
     * the text label.
     *
     * @param g the graphics context used to paint the component
     */
    // Dibuja el fondo redondeado y el borde del botón antes de pintar su texto.
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