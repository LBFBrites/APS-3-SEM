package trash;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class TrashView extends JPanel {
    private final TrashModel model;

    public TrashView(TrashModel model) {
        this.model = model;
        setPreferredSize(new Dimension(900, 620));
        setFocusable(true);
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int w = getWidth(), h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(18, 28, 22), w, h, new Color(12, 20, 30)));
        g2.fillRect(0, 0, w, h);

        g2.setPaint(new GradientPaint(0, h - 30, new Color(35, 55, 40), 0, h, new Color(20, 35, 25)));
        g2.fillRect(0, h - 30, w, 30);

        if (model.flashFrames > 0 && model.flashColor != null) {
            g2.setColor(model.flashColor);
            g2.fillRect(0, 0, w, h);
        }

        for (Trash t : model.items) {
            Color c = TrashModel.COLORS[t.colorIdx];
            Graphics2D gt = (Graphics2D) g2.create();
            gt.translate(t.x, t.y);
            gt.rotate(Math.toRadians(t.rot));

            gt.setColor(new Color(0, 0, 0, 40));
            gt.fillRoundRect(-16, -14, 32, 30, 10, 10);

            gt.setColor(c);
            gt.fillRoundRect(-15, -15, 30, 30, 10, 10);
            gt.setColor(new Color(255, 255, 255, 60));
            gt.fillRoundRect(-12, -13, 10, 8, 4, 4);

            gt.setFont(new Font("Segoe UI", Font.BOLD, 14));
            gt.setColor(new Color(255, 255, 255, 200));
            FontMetrics fm = gt.getFontMetrics();
            String ic = TrashModel.TRASH_ICONS[t.colorIdx];
            gt.drawString(ic, -fm.stringWidth(ic) / 2, fm.getAscent() / 2 - 2);
            gt.dispose();
        }

        float binTop = h - 70;
        Color bc = TrashModel.COLORS[model.binColorIdx];

        g2.setColor(new Color(bc.getRed(), bc.getGreen(), bc.getBlue(), 30));
        g2.fillOval((int) (model.binX - TrashModel.BIN_W), (int) (binTop - 10), TrashModel.BIN_W * 2, TrashModel.BIN_H + 20);

        float bx = model.binX - TrashModel.BIN_W / 2f, by = binTop;
        GeneralPath bin = new GeneralPath();
        bin.moveTo(bx, by);
        bin.lineTo(bx + TrashModel.BIN_W, by);
        bin.lineTo(bx + TrashModel.BIN_W - 5, by + TrashModel.BIN_H);
        bin.lineTo(bx + 5, by + TrashModel.BIN_H);
        bin.closePath();

        g2.setPaint(new GradientPaint(bx, by, bc, bx, by + TrashModel.BIN_H, bc.darker().darker()));
        g2.fill(bin);
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(bc.brighter());
        g2.draw(bin);

        g2.setColor(bc.brighter());
        g2.fillRoundRect((int) bx - 3, (int) by - 6, TrashModel.BIN_W + 6, 8, 6, 6);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(new Color(255, 255, 255, 180));
        FontMetrics fm = g2.getFontMetrics();
        String sym = "\u267B";
        g2.drawString(sym, model.binX - fm.stringWidth(sym) / 2f, by + TrashModel.BIN_H / 2f + 8);

        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, w, 56);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(new Color(60, 200, 100));
        g2.drawString("\u267B trash-inator", 20, 38);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + model.score, w / 2 - 50, 38);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        for (int i = 0; i < 3; i++) {
            g2.setColor(i < model.lives ? new Color(255, 80, 80) : new Color(60, 60, 60));
            g2.drawString("\u2764", w - 100 + i * 22, 38);
        }

        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(w / 2 - 120, 54, 240, 28, 14, 14);
        for (int i = 0; i < 4; i++) {
            boolean active = (i == model.binColorIdx);
            int bx2 = w / 2 - 110 + i * 58;
            g2.setColor(active ? TrashModel.COLORS[i]
                    : new Color(TrashModel.COLORS[i].getRed(), TrashModel.COLORS[i].getGreen(), TrashModel.COLORS[i].getBlue(), 60));
            g2.fillRoundRect(bx2, 58, 48, 20, 10, 10);
            if (active) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(bx2, 58, 48, 20, 10, 10);
            }
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(active ? Color.WHITE : new Color(200, 200, 200, 80));
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(TrashModel.COLOR_NAMES[i], bx2 + (48 - fm2.stringWidth(TrashModel.COLOR_NAMES[i])) / 2, 72);
        }

        if (!model.running && !model.gameOver) {
            drawOverlay(g2, w, h, "trash-inator!", new Color(60, 220, 120),
                    "\u2190 \u2192 or A/D to move  |  1=Red  2=Yellow  3=Blue  4=Green",
                    "Press SPACE to start");
        } else if (model.gameOver) {
            drawOverlay(g2, w, h, "Game Over!  Score: " + model.score, new Color(255, 200, 80),
                    "Match the bin color to the falling trash",
                    "Press SPACE to retry  \u2022  ESC for hub");
        }

        g2.dispose();
    }

    private void drawOverlay(Graphics2D g2, int w, int h, String title, Color tc, String sub1, String sub2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, h / 2 - 80, w, 160);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g2.setColor(tc);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, h / 2 - 20);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g2.setColor(new Color(200, 200, 220));
        fm = g2.getFontMetrics();
        g2.drawString(sub1, (w - fm.stringWidth(sub1)) / 2, h / 2 + 20);
        g2.setColor(new Color(160, 160, 190));
        fm = g2.getFontMetrics();
        g2.drawString(sub2, (w - fm.stringWidth(sub2)) / 2, h / 2 + 50);
    }
}
