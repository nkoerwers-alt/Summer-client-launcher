package com.summerlauncher;

import com.microsoft.aad.msal4j.IAuthenticationResult;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class Main {

    private static JFrame frame;
    private static JPanel content;

    private static final Color BG = new Color(18, 14, 30);
    private static final Color PANEL = new Color(25, 22, 32);
    private static final Color PANEL_2 = new Color(35, 31, 44);
    private static final Color TEXT = new Color(245, 245, 248);
    private static final Color MUTED = new Color(145, 140, 155);
    private static final Color ACCENT = new Color(90, 150, 255);

    private static JLabel accountLabel;
    private static JLabel accountStatus;
    private static JButton launchButton;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            frame = new JFrame("Summer Client");
            frame.setSize(1100, 700);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);

            SunsetBackground root = new SunsetBackground();
            root.setLayout(new BorderLayout());

            root.add(createTopBar(), BorderLayout.NORTH);

            content = new JPanel(new BorderLayout());
            content.setOpaque(false);

            content.add(createHome(), BorderLayout.CENTER);

            root.add(createSidebar(), BorderLayout.WEST);
            root.add(content, BorderLayout.CENTER);

            frame.setContentPane(root);
            frame.setVisible(true);
        });
    }

    private static JPanel createTopBar() {

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(10, 10, 12));
        bar.setPreferredSize(new Dimension(0, 54));

        JLabel logo = new JLabel("SUMMER CLIENT");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.setOpaque(false);

        accountLabel = new JLabel("Microsoft Account");
        accountLabel.setForeground(Color.WHITE);
        accountLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton minimize = windowButton("-");
        JButton close = windowButton("X");

        minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        close.addActionListener(e -> System.exit(0));

        right.add(accountLabel);
        right.add(minimize);
        right.add(close);

        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private static JButton windowButton(String text) {

        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(42, 34));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(10, 10, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private static JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(14, 13, 18));
        sidebar.setPreferredSize(new Dimension(190, 0));

        JLabel title = new JLabel("SUMMER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 18, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(title);

        JButton play = navButton("PLAY", true);
        JButton skins = navButton("SKINS", false);
        JButton capes = navButton("CAPES", false);
        JButton mods = navButton("MODS", false);
        JButton worlds = navButton("WORLDS", false);
        JButton settings = navButton("SETTINGS", false);

        play.addActionListener(e -> showPage(createHome()));
        skins.addActionListener(e -> showPage(createSimplePage(
                "SKINS",
                "Manage your Minecraft skins."
        )));
        capes.addActionListener(e -> showPage(createSimplePage(
                "CAPES",
                "Manage your Minecraft capes."
        )));
        mods.addActionListener(e -> showPage(createSimplePage(
                "MODS",
                "Your installed Minecraft mods."
        )));
        worlds.addActionListener(e -> showPage(createSimplePage(
                "WORLDS",
                "Your Minecraft worlds."
        )));
        settings.addActionListener(e -> showPage(createSettings()));

        sidebar.add(play);
        sidebar.add(skins);
        sidebar.add(capes);
        sidebar.add(mods);
        sidebar.add(worlds);
        sidebar.add(settings);

        sidebar.add(Box.createVerticalGlue());

        JLabel version = new JLabel("SUMMER CLIENT 1.0");
        version.setForeground(new Color(80, 78, 88));
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));
        version.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 0));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(version);

        return sidebar;
    }

    private static JButton navButton(String text, boolean selected) {

        JButton button = new JButton(text);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        button.setPreferredSize(new Dimension(190, 58));

        button.setForeground(selected ? Color.WHITE : MUTED);
        button.setBackground(selected ? PANEL_2 : new Color(14, 13, 18));

        button.setFont(new Font("SansSerif", Font.BOLD, 13));

        button.setBorder(
                BorderFactory.createEmptyBorder(0, 22, 0, 0)
        );

        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private static void showPage(JPanel panel) {

        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private static JPanel createHome() {

        JPanel home = new JPanel(new GridBagLayout());
        home.setOpaque(false);
        home.setBorder(
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        );

        GridBagConstraints main = new GridBagConstraints();
        main.gridx = 0;
        main.gridy = 0;
        main.weightx = 1;
        main.weighty = 1;
        main.fill = GridBagConstraints.BOTH;

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 30));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        accountStatus = new JLabel("Sign in with your Microsoft account");
        accountStatus.setForeground(MUTED);
        accountStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(welcome);
        center.add(Box.createVerticalStrut(5));
        center.add(accountStatus);
        center.add(Box.createVerticalStrut(30));

        JPanel userCard = new JPanel();
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setBackground(PANEL);
        userCard.setBorder(
                BorderFactory.createEmptyBorder(18, 35, 18, 35)
        );
        userCard.setMaximumSize(new Dimension(230, 180));
        userCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user = new JLabel("USER");
        user.setForeground(Color.WHITE);
        user.setFont(new Font("SansSerif", Font.BOLD, 16));
        user.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel skin = new JPanel();
        skin.setPreferredSize(new Dimension(90, 95));
        skin.setMaximumSize(new Dimension(90, 95));
        skin.setBackground(new Color(45, 40, 55));

        JLabel skinText = new JLabel("SKIN");
        skinText.setForeground(MUTED);
        skinText.setFont(new Font("SansSerif", Font.BOLD, 12));

        skin.add(skinText);

        userCard.add(user);
        userCard.add(Box.createVerticalStrut(10));
        userCard.add(skin);

        center.add(userCard);
        center.add(Box.createVerticalStrut(25));

        launchButton = new JButton("PLAY");
        launchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        launchButton.setPreferredSize(new Dimension(230, 58));
        launchButton.setMaximumSize(new Dimension(230, 58));
        launchButton.setBackground(new Color(55, 55, 62));
        launchButton.setForeground(Color.WHITE);
        launchButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        launchButton.setBorderPainted(false);
        launchButton.setFocusPainted(false);
        launchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        launchButton.setEnabled(MicrosoftAuth.isLoggedIn());

        launchButton.addActionListener(e -> launchMinecraft());

        center.add(launchButton);
        center.add(Box.createVerticalStrut(8));

        JButton versionsButton = new JButton("VERSION 1.21.11");
        versionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionsButton.setPreferredSize(new Dimension(230, 42));
        versionsButton.setMaximumSize(new Dimension(230, 42));
        versionsButton.setBackground(PANEL);
        versionsButton.setForeground(MUTED);
        versionsButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionsButton.setBorderPainted(false);
        versionsButton.setFocusPainted(false);
        versionsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        versionsButton.addActionListener(e -> showPage(createVersions()));

        center.add(versionsButton);

        main.anchor = GridBagConstraints.CENTER;
        home.add(center, main);

        return home;
    }

    private static JPanel createVersions() {

        JPanel panel = basePage();

        panel.add(pageTitle(
                "VERSIONS",
                "Select the Minecraft version to launch."
        ));

        JPanel card = cardPanel();

        JLabel version = new JLabel("Minecraft 1.21.11");
        version.setForeground(Color.WHITE);
        version.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel status = new JLabel("Selected version");
        status.setForeground(MUTED);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.add(version, BorderLayout.CENTER);
        card.add(status, BorderLayout.EAST);

        panel.add(card);

        return panel;
    }

    private static JPanel createSettings() {

        JPanel panel = basePage();

        panel.add(pageTitle(
                "SETTINGS",
                "Configure Summer Client."
        ));

        JPanel java = cardPanel();

        JLabel javaText = new JLabel(
                "Java " + System.getProperty("java.version")
        );
        javaText.setForeground(Color.WHITE);
        javaText.setFont(new Font("SansSerif", Font.BOLD, 14));

        java.add(javaText, BorderLayout.CENTER);

        panel.add(java);

        return panel;
    }

    private static JPanel createSimplePage(
            String title,
            String description
    ) {

        JPanel panel = basePage();

        panel.add(pageTitle(title, description));

        JPanel empty = cardPanel();

        JLabel text = new JLabel("Nothing here yet.");
        text.setForeground(MUTED);
        text.setFont(new Font("SansSerif", Font.PLAIN, 14));

        empty.add(text, BorderLayout.CENTER);

        panel.add(empty);

        return panel;
    }

    private static JPanel basePage() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        );

        return panel;
    }

    private static JPanel cardPanel() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL);
        card.setBorder(
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        );
        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 90)
        );

        return card;
    }

    private static JPanel pageTitle(
            String title,
            String subtitle
    ) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private static void login() {

        accountStatus.setText("Opening Microsoft sign-in...");

        CompletableFuture<IAuthenticationResult> future =
                MicrosoftAuth.login();

        future.thenAccept(result ->
                SwingUtilities.invokeLater(() -> {

                    accountLabel.setText(
                            result.account().username()
                    );

                    accountStatus.setText(
                            "Microsoft account connected"
                    );

                    if (launchButton != null) {
                        launchButton.setEnabled(true);
                        launchButton.setBackground(ACCENT);
                    }
                })
        ).exceptionally(error -> {

            SwingUtilities.invokeLater(() -> {

                accountStatus.setText(
                        "Microsoft sign-in failed"
                );

                JOptionPane.showMessageDialog(
                        frame,
                        error.getCause() != null
                                ? error.getCause().getMessage()
                                : error.getMessage(),
                        "Microsoft Login",
                        JOptionPane.ERROR_MESSAGE
                );
            });

            return null;
        });
    }

    private static void launchMinecraft() {

        if (!MicrosoftAuth.isLoggedIn()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please sign in with your Microsoft account first.",
                    "Microsoft Account Required",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {
            MinecraftLauncher.launch();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    frame,
                    e.getMessage(),
                    "Minecraft Launch Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
