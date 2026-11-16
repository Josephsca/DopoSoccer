package presentation;

import domain.DopoSoccer;
import domain.DopoSoccerException;
import domain.Log;
import domain.Match;
import domain.Player;
import domain.Stadium;

import javax.swing.*;
import java.awt.*;

/**
 * Main window of the game. It has three screens: menu, configuration, and match.
 * It only communicates with the domain through the DopoSoccer class.
 */
public class DopoSoccerGUI extends JFrame {

    private static final String MENU = "menu";
    private static final String CONFIG = "config";
    private static final String GAME = "game";
    private static final String[] COUNTRY_NAMES = {"Corea del Sur", "Camerún", "Nigeria", "Estados Unidos"};
    private static final String[] RESOLUTIONS = {Stadium.SMALL, Stadium.MEDIUM, Stadium.LARGE};

    private DopoSoccer game;
    private CardLayout cards;
    private JPanel container;
    private GamePanel gamePanel;

    // Menu screen buttons
    private JButton enterToPlay;
    private JButton selectPlayers;
    private JButton soundButton;
    private JButton playerVsPlayer;
    private JButton playerVsMachine;
    private JButton quickStart;
    private JButton backToMenu;
    private JButton backToMode;

    // Configuration screen components
    private JTextField name1;
    private JTextField name2;
    private JComboBox<String> country1;
    private JComboBox<String> country2;
    private JComboBox<String> matchType;
    private JSpinner limit;
    private JComboBox<String> resolution;
    private JButton back;
    private JButton start;

    /**
     * Constructs the main application window, initializes the game instance, and sets up UI elements and actions.
     */
    public DopoSoccerGUI() {
        game = new DopoSoccer();
        prepareElements();
        prepareActions();
    }

    /**
     * Initializes the main frame settings, layout container, and adds all game screens.
     */
    private void prepareElements() {
        setTitle("DopoSoccer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        cards = new CardLayout();
        container = new JPanel(cards);
        container.add(createMenuPanel(), MENU);
        container.add(createConfigPanel(), CONFIG);
        gamePanel = new GamePanel(this, game);
        container.add(gamePanel, GAME);
        add(container);
        showScreen(MENU);
    }

    /**
     * Creates and configures the main menu panel with background image and options.
     * @return the configured menu JPanel
     */
    private JPanel createMenuPanel() {
        final Image background = new ImageIcon("images/menu_bg.jpg").getImage();   
        JPanel panel = new JPanel(new GridBagLayout()) {                          
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setPreferredSize(new Dimension(800, 450));
        panel.setBorder(BorderFactory.createEmptyBorder(220, 0, 0, 0)); 
        panel.setBackground(new Color(20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        enterToPlay = new Button("Entrar a jugar");
        soundButton = new Button("Sonido: ON");
        playerVsPlayer = new Button("Jugador vs Jugador");
        playerVsMachine = new Button("Jugador vs Máquina");
        backToMenu = new Button("Volver");
        quickStart = new Button("Empezar");
        selectPlayers = new Button("Seleccionar jugadores");
        backToMode = new Button("Volver");
        panel.add(enterToPlay, c);
        panel.add(soundButton, c);
        panel.add(playerVsPlayer, c);
        panel.add(playerVsMachine, c);
        panel.add(backToMenu, c);
        panel.add(quickStart, c);
        panel.add(selectPlayers, c);
        panel.add(backToMode, c);
        showMenuStep(0);
       return panel;
    }

    /**
     * Updates visibility of menu buttons based on the current menu navigation step.
     * @param step the active menu step index
     */
    private void showMenuStep(int step) {
        enterToPlay.setVisible(step == 0);
        soundButton.setVisible(step == 0);
        playerVsPlayer.setVisible(step == 1);
        playerVsMachine.setVisible(step == 1);
        backToMenu.setVisible(step == 1);
        quickStart.setVisible(step == 2);
        selectPlayers.setVisible(step == 2);
        backToMode.setVisible(step == 2);
    }

    /**
     * Creates and configures the match configuration panel.
     * @return the configured configuration JPanel
     */
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(800, 450));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel players = new JPanel(new GridLayout(1, 2, 30, 0));
        name1 = new JTextField("J1");
        name2 = new JTextField("J2");
        country1 = new JComboBox<>(COUNTRY_NAMES);
        country2 = new JComboBox<>(COUNTRY_NAMES);
        country2.setSelectedIndex(1);
        players.add(createPlayerBox("J1  (A D W S Q)", name1, country1));
        players.add(createPlayerBox("J2  (Flechas + Ctrl derecho)", name2, country2));

        JPanel options = new JPanel(new GridLayout(3, 2, 10, 10));
        options.setBorder(BorderFactory.createTitledBorder("Partido"));
        matchType = new JComboBox<>(new String[]{"Por tiempo (minutos)", "Por goles"});
        limit = new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        resolution = new JComboBox<>(RESOLUTIONS);
        options.add(new JLabel("Tipo de partido:"));
        options.add(matchType);
        options.add(new JLabel("Minutos / goles para ganar:"));
        options.add(limit);
        options.add(new JLabel("Resolución de la cancha:"));
        options.add(resolution);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        back = new Button("Volver");
        start = new Button("Empezar");
        buttons.add(back);
        buttons.add(start);

        panel.add(players, BorderLayout.NORTH);
        JPanel optionsWrapper = new JPanel(new BorderLayout());
        optionsWrapper.add(options, BorderLayout.NORTH);
        panel.add(optionsWrapper, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Helper method to create a titled sub-panel for player configuration fields.
     * @param title the section title
     * @param name text field for the player's name
     * @param country dropdown for selecting the player's character country
     * @return a JPanel containing player setup fields
     */
    private JPanel createPlayerBox(String title, JTextField name, JComboBox<String> country) {
        JPanel box = new JPanel(new GridLayout(4, 1, 5, 5));
        box.setBorder(BorderFactory.createTitledBorder(title));
        box.add(new JLabel("Nombre:"));
        box.add(name);
        box.add(new JLabel("Personaje:"));
        box.add(country);
        return box;
    }

    /**
     * Registers all action listeners for menu buttons, configuration controls, and sound toggles.
     */
    private void prepareActions() {
            enterToPlay.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMenuStep(1);
                }
            });

            playerVsPlayer.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMenuStep(2);
                }
            });

            playerVsMachine.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    JOptionPane.showMessageDialog(null,
                        "Jugador vs Máquina está en construcción.",
                        "En construcción", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            quickStart.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startMatch();
                }
            });

            backToMenu.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMenuStep(0);
                }
            });

            backToMode.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showMenuStep(1);
                }
            });

            selectPlayers.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showScreen(CONFIG);
                }
            });

            soundButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    game.toggleSound();
                    if (game.isSound()) {
                        soundButton.setText("Sonido: ON");
                    } else {
                        soundButton.setText("Sonido: OFF");
                    }
                }
            });

            back.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showScreen(MENU);
                }
            });

            start.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startMatch();
                }
            });
        }

    /**
     * Creates the match with the chosen configuration and switches to the game screen view.
     */
    private void startMatch() {
        try {
            String type = matchType.getSelectedIndex() == 0 ? DopoSoccer.TIME : DopoSoccer.GOALS;
            game.startNewMatch(
                name1.getText(), DopoSoccer.COUNTRIES[country1.getSelectedIndex()],
                name2.getText(), DopoSoccer.COUNTRIES[country2.getSelectedIndex()],
                type, (Integer) limit.getValue(), (String) resolution.getSelectedItem());
            Stadium stadium = game.getCurrentMatch().getStadium();
            gamePanel.setPreferredSize(new Dimension(stadium.getWidth(), stadium.getHeight()));
            showScreen(GAME);
            gamePanel.start();
        } catch (DopoSoccerException e) {
            showError(e);
        }
    }

    /**
     * Opens the pause menu allowing the user to return, save, or finish the match.
     */
    public void showPauseMenu() {
        game.pauseMatch();
        String[] options = {"Volver", "Guardar", "Finalizar"};
        int choice = JOptionPane.showOptionDialog(this, "Juego en pausa", "Pausa",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == 1) {
            JOptionPane.showMessageDialog(this, "Guardar partidas está en construcción.");
            showPauseMenu();
        } else if (choice == 2) {
            game.finishMatch();
        } else {
            game.resumeMatch();
        }
        gamePanel.requestFocusInWindow();
    }

    /**
     * Displays the winner, final score, and earned points summary dialog.
     */
    public void showWinner() {
        Match match = game.getCurrentMatch();
        Player left = match.getLeftPlayer();
        Player right = match.getRightPlayer();
        Player winner = match.getWinner();
        String title = winner == null ? "Empate" : "Ganador: " + winner.getName();
        String message = title + "\n"
            + left.getName() + ": " + left.getScore() + "  -  " + right.getName() + ": " + right.getScore() + "\n\n"
            + "Puntos obtenidos:\n"
            + left.getName() + ": +" + left.getPoints() + "\n"
            + right.getName() + ": +" + right.getPoints();
        String[] options = {"Jugar de nuevo", "Menú principal"};
        int choice = JOptionPane.showOptionDialog(this, message, "Fin del partido",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            startMatch();
        } else {
            showMenuStep(0);
            showScreen(MENU);
        }
    }

    /**
     * Switches the active card layout screen and resizes the window container.
     * @param screen the name of the screen to display
     */
    private void showScreen(String screen) {
        cards.show(container, screen);
        if (!screen.equals(GAME)) {
            Component current = screen.equals(MENU) ? container.getComponent(0) : container.getComponent(1);
            gamePanel.setPreferredSize(current.getPreferredSize());
        }
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Displays a clear error message dialog to the user and records the exception in the log file.
     * @param e the DopSoccerException error to handle
     */
    private void showError(DopoSoccerException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        Log.record(e);
    }

    /**
     * Main entry point of the application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DopoSoccerGUI gui=new DopoSoccerGUI();
        gui.setVisible(true);
    }
}
