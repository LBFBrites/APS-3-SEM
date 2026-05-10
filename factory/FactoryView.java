package factory;

import javax.swing.*;
import java.awt.*;

public class FactoryView extends JPanel {
    private final FactoryModel model;
    private final Runnable onBack;

    public FactoryView(FactoryModel model, Runnable onBack) {
        this.model = model;
        this.onBack = onBack;
        setPreferredSize(new Dimension(900, 620));
        setFocusable(true);
        setBackground(new Color(20, 25, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(50, 60, 70), 0, model.groundY, new Color(90, 100, 110)));
        g2.fillRect(0, 0, w, model.groundY);

        g2.setPaint(new GradientPaint(0, model.groundY, new Color(40, 45, 50), 0, h, new Color(20, 25, 30)));
        g2.fillRect(0, model.groundY, w, h - model.groundY);

        for (int i = 0; i < 3; i++) {
            int fx = model.factX[i];
            int fy = model.groundY - model.factH;
            
            g2.setColor(new Color(110, 115, 120));
            g2.fillRect(fx + model.factW - 30, fy - 30, 20, 40);
            
            g2.setColor(new Color(130, 135, 145));
            g2.fillRect(fx, fy, model.factW, model.factH);
            g2.setColor(new Color(100, 105, 115));
            g2.fillRect(fx, fy, model.factW / 2, model.factH); 

            g2.setColor(new Color(80, 85, 95));
            Polygon roof = new Polygon(new int[] { fx - 10, fx + model.factW / 2, fx + model.factW + 10 },
                    new int[] { fy, fy - 30, fy }, 3);
            g2.fillPolygon(roof);
        }

        long time = System.currentTimeMillis();
        for (Issue is : model.issues) {
            boolean flash = is.timeLeft < 30 && (time % 500 < 250);

            if (is.type == Issue.SMOKE) {
                g2.setColor(new Color(80, 220, 80, 180));
                int size = (int) (40 + Math.sin(time / 200.0) * 10);
                g2.fillOval((int) is.x - size / 2, (int) is.y - size / 2, size, size);
                g2.fillOval((int) is.x - size / 2 - 10, (int) is.y - size / 2 - 15, size - 10, size - 10);
            } else if (is.type == Issue.OIL) {
                g2.setColor(new Color(20, 20, 20, 220));
                int width = 80;
                g2.fillOval((int) is.x - width / 2, (int) is.y - 10, width, 20);
                g2.fillRoundRect((int) is.x - 10, (int) is.y - 30, 20, 30, 5, 5);
            } else if (is.type == Issue.NUKE) {
                g2.setColor(new Color(220, 200, 40));
                g2.fillRoundRect((int) is.x - 15, (int) is.y - 20, 30, 40, 5, 5);
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.drawString("\u2622", is.x - 11, is.y + 10);
            }

            g2.setColor(Color.BLACK);
            g2.fillRect((int) is.x - 20, (int) is.y - 40, 40, 6);
            g2.setColor(flash ? Color.RED : Color.GREEN);
            g2.fillRect((int) is.x - 20, (int) is.y - 40, (int) (40 * (is.timeLeft / 100f)), 6);
        }

        int[] toolX = { 250, 400, 550 };
        String[] toolNames = { "1. FILTER", "2. BRICK", "3. TRUCK" };
        Color[] toolColors = { new Color(80, 180, 220), new Color(180, 80, 60), new Color(220, 180, 40) };
        String[] toolIcons = { "\u2601", "\uD83E\uDDF1", "\uD83D\uDE9A" }; 

        for (int i = 0; i < 3; i++) {
            int tx = toolX[i], ty = 480;
            boolean sel = (model.selectedTool == i);

            g2.setColor(sel ? toolColors[i].brighter() : new Color(60, 65, 70));
            g2.fillRoundRect(tx, ty, 100, 80, 15, 15);
            g2.setStroke(new BasicStroke(sel ? 4f : 2f));
            g2.setColor(sel ? Color.WHITE : new Color(100, 105, 110));
            g2.drawRoundRect(tx, ty, 100, 80, 15, 15);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
            g2.setColor(sel ? Color.BLACK : Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(toolIcons[i], tx + (100 - fm.stringWidth(toolIcons[i])) / 2, ty + 45);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            fm = g2.getFontMetrics();
            g2.drawString(toolNames[i], tx + (100 - fm.stringWidth(toolNames[i])) / 2, ty + 70);
        }

        if (model.selectedTool != -1) {
            Point mp = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mp, this);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            g2.setColor(Color.WHITE);
            g2.drawString(toolIcons[model.selectedTool], mp.x - 20, mp.y + 10);
            setCursor(getToolkit().createCustomCursor(new java.awt.image.BufferedImage(1, 1, 2), new Point(0, 0), "null"));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, w, 50);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(new Color(200, 100, 60));
        g2.drawString("\uD83C\uDFED factory-inator", 16, 34);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + model.score, w / 2 - 40, 34);

        g2.setColor(new Color(255, 80, 80));
        for (int i = 0; i < 3; i++) {
            if (i < model.lives)
                g2.drawString("\u2764", w - 160 + i * 25, 34);
        }

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(180, 190, 210));
        g2.drawString("\u2190 Back (ESC)", w - 80, 34);

        if (!model.running && !model.gameOver) {
            drawOverlay(g2, w, h, "factory-inator", new Color(200, 100, 60),
                    "Select a tool (1,2,3) and click the matching issue to fix it!", "Press SPACE to start");
        } else if (model.gameOver) {
            drawOverlay(g2, w, h, "Game Over!", new Color(255, 80, 80),
                    "Final Score: " + model.score, "Press SPACE to retry \u2022 ESC for hub");
        }

        g2.dispose();
    }

    private void drawOverlay(Graphics2D g2, int w, int h, String t, Color tc, String s1, String s2) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, h / 2 - 70, w, 140);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.setColor(tc);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t, (w - fm.stringWidth(t)) / 2, h / 2 - 20);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g2.setColor(new Color(210, 210, 230));
        fm = g2.getFontMetrics();
        g2.drawString(s1, (w - fm.stringWidth(s1)) / 2, h / 2 + 15);
        g2.setColor(new Color(170, 170, 190));
        fm = g2.getFontMetrics();
        g2.drawString(s2, (w - fm.stringWidth(s2)) / 2, h / 2 + 42);
    }
}
