package com.summerlauncher;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LaunchLog {

    private static JTextArea logArea;
    private static JFrame frame;

    public static void show() {
        if (frame != null && frame.isVisible()) {
            return;
        }

        frame = new JFrame("Summer Client - Launch Log");
        frame.setSize(700, 450);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(12, 12, 14));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(12, 12, 14));
        logArea.setForeground(new Color(220, 220, 225));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(null);

        frame.add(scroll);
        frame.setVisible(true);

        log("Summer Client launch system started.");
    }

    public static void log(String message) {
        if (logArea == null) {
            return;
        }

        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + time + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
