package presentation;

import domain.DopoSoccer;
import domain.DopoSoccerException;
import domain.Log;
import domain.Match;
import domain.Player;
import domain.Stadium;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main window of the game with three screens: menu, configuration, and match.
 * It only talks to the domain layer through DopoSoccer.
 */
public class DopoSoccerGUI extends JFrame {

    private static final String MENU = "menu";
    private static final String CONFIG = "config";
    private static final String GAME = "game";
    private static final String[] COUNTRY_NAMES = {"Corea del Sur", "Camerún", "Nigeria", "Estados Unidos"};
    private static final String[] RESOLUTIONS = {Stadium.SMALL, Stadium.MEDIUM, Stadium.LARGE};
    private boolean againstMachine = false;

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
    private Button loadGameButton;
    private Button upgradeButton;

    // Configuration screen components
    private JTextField name1;
    private JTextField name2;
    private JComboBox<String> country1;
    private JComboBox<String> country2;
    private JComboBox<String> machineSide;
    private JComboBox<String> machineStrategy;
    private JPanel machineOptions;
    private JComboBox<String> matchType;
    private JSpinner limit;
    private JComboBox<String> resolution;
    private JButton back;
    private JButton start;
    private JPanel playerBox1;
    private JPanel playerBox2;
    

    /**
     * Creates the main game window, initializes the domain controller, and sets
     * up UI elements and event listeners.
     */
    public DopoSoccerGUI() {
        game = new DopoSoccer();
        prepareElements();
        prepareActions();
    }

    /**
     * Sets up the window properties, card container, and screen panels.
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
     * Creates the main menu panel with a background image and navigation buttons.
     *
     * @return the configured menu panel
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
        panel.setBorder(BorderFactory.createEmptyBorder(250, 0, 0, 0));   // deja ver el logo
        panel.setBackground(new Color(20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        enterToPlay = new Button("Entrar a jugar");
        soundButton = new Button("Sonido: ON");
        playerVsPlayer = new Button("Jugador vs Jugador");
        playerVsMachine = new Button("Jugador vs Máquina");
        backToMenu = new Button("Volver");
        quickStart = new Button("Empezar");
        selectPlayers = new Button("Seleccionar jugadores");
        backToMode = new Button("Volver");
        loadGameButton = new Button("Cargar partida");
        upgradeButton = new Button("Mejoras");
        // Menú principal: dos columnas de dos botones
        addButton(panel, c, enterToPlay, 0, 0, 1);
        addButton(panel, c, loadGameButton, 1, 0, 1);
        addButton(panel, c, upgradeButton, 0, 1, 1);
        addButton(panel, c, soundButton, 1, 1, 1);
        // Los otros pasos del menú: un botón debajo del otro, más anchos para que quepa el texto
        JButton[] wideButtons = {playerVsPlayer, playerVsMachine, backToMenu, quickStart, selectPlayers, backToMode};
        for (JButton b : wideButtons) {
            b.setPreferredSize(new Dimension(330, 48));
        }
        addButton(panel, c, playerVsPlayer, 0, 2, 2);
        addButton(panel, c, playerVsMachine, 0, 3, 2);
        addButton(panel, c, backToMenu, 0, 4, 2);
        addButton(panel, c, quickStart, 0, 5, 2);
        addButton(panel, c, selectPlayers, 0, 6, 2);
        addButton(panel, c, backToMode, 0, 7, 2);
        showMenuStep(0);
       return panel;
    }

    /**
     * Adds a button to the menu layout at the given row, column, and column span.
     *
     * @param panel  the target menu panel
     * @param c      the grid bag constraints object to update
     * @param button the button to add
     * @param column the starting grid column
     * @param row    the starting grid row
     * @param width  the number of columns the button spans
     */
    private void addButton(JPanel panel, GridBagConstraints c, JButton button, int column, int row, int width) {
        c.gridx = column;
        c.gridy = row;
        c.gridwidth = width;
        panel.add(button, c);
    }

    /**
     * Shows or hides menu buttons based on the active menu step.
     *
     * @param step the menu step to display (0 for main, 1 for mode, 2 for start options)
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
        loadGameButton.setVisible(step == 0);
        upgradeButton.setVisible(step == 0);
    }

    /**
     * Creates the match configuration panel with player boxes, match settings,
     * and machine options.
     *
     * @return the configured configuration panel
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
        playerBox1 = createPlayerBox("J1 (A D W S Q)", name1, country1);
        playerBox2 = createPlayerBox("J2 (Flechas + Ctrl derecho)", name2, country2);

        players.add(playerBox1);
        players.add(playerBox2);

        JPanel options = new JPanel(new GridLayout(3, 2, 10, 10));
        options.setBorder(BorderFactory.createTitledBorder("Partido"));

        machineSide = new JComboBox<>(
            new String[]{"Derecha", "Izquierda"}
        );

        machineStrategy = new JComboBox<>(
            new String[]{"Torpe", "Ofensiva", "Defensiva", "Experta"}
        );

        machineOptions = new JPanel(new GridLayout(2, 2, 10, 10));
        machineOptions.setBorder(
            BorderFactory.createTitledBorder("Configuración de máquina")
        );

        machineOptions.add(new JLabel("Lado de la máquina:"));
        machineOptions.add(machineSide);

        machineOptions.add(new JLabel("Estrategia:"));
        machineOptions.add(machineStrategy);

        machineOptions.setVisible(false);

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
        JPanel optionsWrapper = new JPanel(new BorderLayout(10, 10));

        optionsWrapper.add(options, BorderLayout.NORTH);
        optionsWrapper.add(machineOptions, BorderLayout.CENTER);

        panel.add(optionsWrapper, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates a titled box with fields for a player's name and country character.
     *
     * @param title   the border title of the box
     * @param name    the text field for the player's name
     * @param country the combo box for selecting the country character
     * @return the configured player box panel
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
     * Registers listeners for all buttons and controls across the menu and
     * configuration screens.
     */
    private void prepareActions() {
        enterToPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMenuStep(1);
            }
        });

        playerVsPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                againstMachine = false;
                machineOptions.setVisible(false);
                updatePlayerLabels();
                showMenuStep(2);
            }
        });

        playerVsMachine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                againstMachine = true;
                machineOptions.setVisible(true);
                updatePlayerLabels();
                showMenuStep(2);
            }
        });

        machineSide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayerLabels();
            }
        });

        
        quickStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMatch();
            }
        });

        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMenuStep(0);
            }
        });

        backToMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMenuStep(1);
            }
        });

        selectPlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScreen(CONFIG);
            }
        });

        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.toggleSound();
                if (game.isSound()) {
                    soundButton.setText("Sonido: ON");
                } else {
                    soundButton.setText("Sonido: OFF");
                }
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScreen(MENU);
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMatch();
            }
        });

        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMatch();
            }
        });

        upgradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUpgradeMenu();
            }
        });

        
    }

    /**
     * Starts a new match using the selected settings and opens the game screen.
     */            
    private void startMatch() {
        try {
            String type = matchType.getSelectedIndex() == 0
                    ? DopoSoccer.TIME
                    : DopoSoccer.GOALS;

            String countryLeft =
                    DopoSoccer.COUNTRIES[country1.getSelectedIndex()];

            String countryRight =
                    DopoSoccer.COUNTRIES[country2.getSelectedIndex()];

            int matchLimit = (Integer) limit.getValue();

            String stadiumSize =
                    (String) resolution.getSelectedItem();

            if (againstMachine) {

                boolean machineLeft =
                        machineSide.getSelectedItem().equals("Izquierda");

                String strategy =
                        (String) machineStrategy.getSelectedItem();

                // Identificar el personaje del humano
                String humanCountry = machineLeft
                        ? countryRight
                        : countryLeft;

                // Identificar el personaje de la máquina
                String machineCountry = machineLeft
                        ? countryLeft
                        : countryRight;

                // El nombre del humano depende de su lado
                String humanName = machineLeft
                        ? name2.getText()
                        : name1.getText();

                game.startMachineMatch(
                        humanName,
                        humanCountry,
                        machineCountry,
                        type,
                        matchLimit,
                        stadiumSize,
                        machineLeft,
                        strategy
                );

            } else {

                game.startNewMatch(
                        name1.getText(),
                        countryLeft,
                        name2.getText(),
                        countryRight,
                        type,
                        matchLimit,
                        stadiumSize
                );
            }

            Stadium stadium = game.getCurrentMatch().getStadium();

            gamePanel.setPreferredSize(
                    new Dimension(
                            stadium.getWidth(),
                            stadium.getHeight()
                    )
            );

            showScreen(GAME);
            gamePanel.start();

        } catch (DopoSoccerException e) {
            showError(e);
        }
    }


    /**
     * Updates the player box titles and name fields based on the selected game
     * mode and machine side.
     */
    private void updatePlayerLabels() {

        if (!againstMachine) {
            playerBox1.setBorder(
                BorderFactory.createTitledBorder("J1 (A D W S Q)")
            );

            playerBox2.setBorder(
                BorderFactory.createTitledBorder("J2 (Flechas + Ctrl derecho)")
            );

            name1.setEnabled(true);
            name2.setEnabled(true);

        } else {

            boolean machineLeft =
                    machineSide.getSelectedItem().equals("Izquierda");

            playerBox1.setBorder(
                BorderFactory.createTitledBorder(
                    machineLeft ? "J1 - Máquina" : "J1 - Humano (A D W S Q)"
                )
            );

            playerBox2.setBorder(
                BorderFactory.createTitledBorder(
                    machineLeft ? "J2 - Humano (Flechas + Ctrl derecho)"
                                : "J2 - Máquina"
                )
            );

            name1.setEnabled(!machineLeft);
            name2.setEnabled(machineLeft);
        }

        playerBox1.revalidate();
        playerBox2.revalidate();
        playerBox1.repaint();
        playerBox2.repaint();
    }


    /**
     * Opens the pause dialog so the user can resume, save, or finish the match.
     */
    public void showPauseMenu() {
        game.pauseMatch();

        String[] options = {"Volver", "Guardar", "Finalizar"};

        int choice = JOptionPane.showOptionDialog(
            this, "Juego en pausa", "Pausa",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]
        );

        if (choice == 1) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                if (!file.getName().toLowerCase().endsWith(".dat")) {
                    file = new File(file.getAbsolutePath() + ".dat");
                }

                
                boolean guardar = true;

                if (file.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo ya existe. ¿Deseas reemplazarlo?",
                        "Confirmar guardado",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm != JOptionPane.YES_OPTION) {
                        guardar = false;
                    }
                }

                if (guardar) {
                    try {
                        game.saveGame(file.getAbsolutePath());

                        JOptionPane.showMessageDialog(
                            this,
                            "Partida guardada correctamente."
                        );

                    } catch (DopoSoccerException e) {
                        showError(e);
                    }
                }
            }

        } else if (choice == 2) {
            game.finishMatch();
            gamePanel.requestFocusInWindow();
            return;
        }

        game.resumeMatch();
        gamePanel.requestFocusInWindow();
    }


    
    /**
     * Opens a file chooser to load a saved match and starts the game screen.
     */
    private void loadMatch() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar partida");

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try {
                game.loadGame(file.getAbsolutePath());

                Stadium stadium = game.getCurrentMatch().getStadium();

                gamePanel.setPreferredSize(
                    new Dimension(
                        stadium.getWidth(),
                        stadium.getHeight()
                    )
                );

                showScreen(GAME);

                game.resumeMatch();

                gamePanel.start();
                gamePanel.requestFocusInWindow();

            } catch (DopoSoccerException e) {
                showError(e);
            }
        }
    }

    /**
     * Shows a dialog with the match result, score, and earned points, then lets
     * the user replay or return to the menu.
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
            + left.getName() + ": +" + left.getMatchPoints() + "  (total: " + left.getPoints() + ")\n"
            + right.getName() + ": +" + right.getMatchPoints() + "  (total: " + right.getPoints() + ")";
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
     * Opens dialogs to pick a saved player profile and upgrade its stats using
     * earned points.
     */
    private void showUpgradeMenu() {
        String[] savedPlayers = game.getSavedPlayers();
        if (savedPlayers.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Todavía no hay jugadores con progreso guardado.\n"
                + "Juega un partido: al terminar, los puntos se guardan con el nombre de cada jugador\n"
                + "(100 al ganador y 25 al perdedor). La primera mejora cuesta 500 puntos.",
                "Mejorar estadísticas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String name = (String) JOptionPane.showInputDialog(this, "Escoge el jugador:", "Mejorar estadísticas",
            JOptionPane.QUESTION_MESSAGE, null, savedPlayers, savedPlayers[0]);
        if (name == null) return;   // el usuario cerró la ventana

        String[] stats = {"speed", "jump", "kick", "survival"};
        String[] options = {"Speed", "Jump", "Kick", "Survival", "Salir"};

        try {
            Player profile = game.getProfile(name);
            boolean keepGoing = true;

            while (keepGoing) {
                String info = "Jugador: " + profile.getName() + "\n"
                    + "Puntos: " + profile.getPoints() + "\n\n";
                for (String stat : stats) {
                    info += stat + ": " + profile.getStat(stat);
                    if (profile.getStat(stat) < Player.MAX_STAT) {
                        info += "   (siguiente punto: " + profile.getUpgradeCost(stat) + " pts)";
                    } else {
                        info += "   (máximo)";
                    }
                    info += "\n";
                }
                info += "\n¿Qué estadística quieres mejorar?";

                int choice = JOptionPane.showOptionDialog(this, info, "Mejorar estadísticas",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[4]);

                if (choice < 0 || choice == 4) {
                    keepGoing = false;
                } else {
                    try {
                        game.upgradeStat(name, stats[choice]);
                    } catch (DopoSoccerException e) {
                        showError(e);   // por ejemplo: no tiene puntos suficientes
                    }
                }
            }
        } catch (DopoSoccerException e) {
            showError(e);
        }
    }

    /**
     * Switches to the requested screen card, adjusts the window size, and
     * centers the frame.
     *
     * @param screen the card name of the screen to show
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
     * Shows an error message dialog to the user and logs the exception.
     *
     * @param e the DopoSoccerException to display and log
     */
    private void showError(DopoSoccerException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        Log.record(e);
    }

    /**
     * Starts the DopoSoccer graphical application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DopoSoccerGUI gui=new DopoSoccerGUI();
        gui.setVisible(true);
    }
}