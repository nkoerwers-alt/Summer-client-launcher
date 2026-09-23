package com.summerlauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SunsetBackground extends JPanel {

    private int mouseX = -1000;
    private int mouseY = -1000;

    public SunsetBackground() {
        setOpaque(true);

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        // Base sunset gradient
        GradientPaint gradient = new GradientPaint(
                0, 0,
                new Color(25, 18, 70),
                w, h,
                new Color(235, 82, 45)
        );

        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // Large soft sunset glow
        float glowRadius = Math.max(w, h) * 0.65f;

        RadialGradientPaint glow = new RadialGradientPaint(
                new Point(
                        (int)(w * 0.72),
                        (int)(h * 0.68)
                ),
                glowRadius,
                new float[]{0f, 0.55f, 1f},
                new Color[]{
                        new Color(255, 180, 70, 120),
                        new Color(255, 70, 110, 55),
                        new Color(20, 10, 60, 0)
                }
        );

        g2.setPaint(glow);
        g2.fillRect(0, 0, w, h);

        // Mouse-reactive circles
        if (mouseX >= 0 && mouseY >= 0) {

            int spacing = 22;
            double influenceRadius = 240;

            for (int x = spacing / 2; x < w; x += spacing) {
                for (int y = spacing / 2; y < h; y += spacing) {

                    double distance = Math.hypot(
                            x - mouseX,
                            y - mouseY
                    );

                    if (distance > influenceRadius) {
                        continue;
                    }

                    double influence =
                            1.0 - (distance / influenceRadius);

                    // Smooth falloff
                    influence =
                            influence * influence;

                    int radius =
                            (int)(3 + influence * 22);

                    int alpha =
                            (int)(15 + influence * 80);

                    g2.setColor(
                            new Color(
                                    255,
                                    190,
                                    90,
                                    alpha
                            )
                    );

                    g2.fillOval(
                            x - radius,
                            y - radius,
                            radius * 2,
                            radius * 2
                    );
                }
            }

            // Soft glow around cursor
            RadialGradientPaint mouseGlow =
                    new RadialGradientPaint(
                            new Point(mouseX, mouseY),
                            170,
                            new float[]{0f, 0.45f, 1f},
                            new Color[]{
                                    new Color(255, 200, 100, 70),
                                    new Color(255, 90, 120, 25),
                                    new Color(255, 80, 120, 0)
                            }
                    );

            g2.setPaint(mouseGlow);

            g2.fillOval(
                    mouseX - 170,
                    mouseY - 170,
                    340,
                    340
            );
        }

        g2.dispose();
    }
}
