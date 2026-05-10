package pipe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class PipeView extends JPanel {
    private final PipeModel model;

    public PipeView(PipeModel model) {
        this.model = model;
        setPreferredSize(new Dimension(900, 620));
        setFocusable(true);
        setBackground(new Color(30, 25, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int w = getWidth(), h = getHeight();
        model.recalcLayout(w, h);

        g2.setPaint(new GradientPaint(0, 0, new Color(80, 150, 220), 0, h / 2 - 40, new Color(160, 200, 240)));
        g2.fillRect(0, 0, w, h / 2 - 40);

        drawCity(g2, w, h);

        int groundY = h / 2 - 40;
        g2.setPaint(new GradientPaint(0, groundY, new Color(100, 80, 50), 0, groundY + 30, new Color(70, 55, 35)));
        g2.fillRect(0, groundY, w, 30);
        
        g2.setColor(new Color(60, 140, 50));
        g2.fillRect(0, groundY, w, 5);

        g2.setPaint(new GradientPaint(0, groundY + 30, new Color(45, 35, 28), 0, h, new Color(30, 22, 18)));
        g2.fillRect(0, groundY + 30, w, h - groundY - 30);

        g2.setColor(new Color(55, 42, 32, 80));
        for (int by = groundY + 30; by < h; by += 20)
            for (int bx = (by / 20 % 2) * 25; bx < w; bx += 50)
                g2.drawRect(bx, by, 50, 20);

        int mainPipeX = model.gridOX + PipeModel.CELL / 2 - 15;
        g2.setColor(new Color(80, 80, 90));
        g2.fillRect(mainPipeX, groundY - 10, 30, model.gridOY - groundY + 15);
        g2.setColor(new Color(100, 100, 110));
        g2.fillRect(mainPipeX - 3, groundY + 35, 36, 8);

        int entryX = model.gridOX + PipeModel.CELL / 2;
        g2.setColor(new Color(60, 180, 80));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("\u25BC IN", entryX - 16, model.gridOY - 6);

        int exitX = model.gridOX + (PipeModel.GCOLS - 1) * PipeModel.CELL + PipeModel.CELL / 2;
        int exitY = model.gridOY + PipeModel.GROWS * PipeModel.CELL;
        g2.setColor(new Color(80, 80, 90));
        g2.fillRect(exitX - 10, exitY, 20, 25);
        g2.setColor(new Color(60, 180, 220));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("OUT \u25BC", exitX - 20, exitY + 40);

        g2.setColor(new Color(40, 120, 160, 150));
        g2.fillRoundRect(exitX - 30, exitY + 48, 60, 30, 12, 12);
        g2.setColor(new Color(80, 200, 240));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.drawString("TREAT", exitX - 22, exitY + 67);

        for (int r = 0; r < PipeModel.GROWS; r++)
            for (int c = 0; c < PipeModel.GCOLS; c++) {
                int gx = model.gridOX + c * PipeModel.CELL, gy = model.gridOY + r * PipeModel.CELL;
                g2.setColor(new Color(40, 32, 26, 180));
                g2.fillRect(gx, gy, PipeModel.CELL, PipeModel.CELL);
                g2.setColor(new Color(70, 58, 45));
                g2.drawRect(gx, gy, PipeModel.CELL, PipeModel.CELL);
                int idx = r * PipeModel.GCOLS + c;
                if (model.grid[idx] != -1)
                    drawPipe(g2, gx, gy, model.grid[idx], model.won);
            }

        for (TrayPiece p : model.tray) {
            if (p == model.dragging)
                continue;
            if (!p.placed)
                drawTrayPiece(g2, p);
        }
        if (model.dragging != null)
            drawTrayPiece(g2, model.dragging);

        if (model.lost && model.floodLevel > 0) {
            int floodH = (int) (h * model.floodLevel * 0.6f);
            Color brown = new Color(100, 70, 30, (int) (180 * Math.min(1, model.floodLevel * 1.5)));
            g2.setColor(brown);
            g2.fillRect(0, h - floodH, w, floodH);
            if (model.floodLevel > 0.5f) {
                int cityFlood = (int) ((model.floodLevel - 0.5f) * 2 * (h / 2));
                g2.setColor(new Color(110, 75, 30, 160));
                g2.fillRect(0, groundY - cityFlood, w, cityFlood + 40);
            }
        }

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, w, 50);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(new Color(80, 160, 220));
        g2.drawString("\u2692 pipe-inator", 16, 34);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(model.timeLeft <= 10 ? new Color(255, 80, 80) : Color.WHITE);
        g2.drawString("Time: " + model.timeLeft + "s", w / 2 - 40, 34);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(180, 190, 210));
        g2.drawString("\u2190 Back (ESC)", w - 110, 34);

        if (!model.running && !model.won && !model.lost) {
            drawOverlay(g2, w, h, "pipe-inator!", new Color(80, 200, 240),
                    "Drag pipe segments into the grid to connect IN \u2192 OUT", "Press SPACE to start");
        } else if (model.won) {
            drawOverlay(g2, w, h, "City Saved! \u2714", new Color(80, 255, 140),
                    "You connected the pipes with " + model.timeLeft + "s remaining!",
                    "Press SPACE to play again \u2022 ESC for hub");
        } else if (model.lost) {
            drawOverlay(g2, w, h, "City Flooded!", new Color(200, 130, 60),
                    "The sewage overflowed! Try connecting faster next time.",
                    "Press SPACE to retry \u2022 ESC for hub");
        }

        g2.dispose();
    }

    private void drawCity(Graphics2D g2, int w, int h) {
        int groundY = h / 2 - 40;
        int[][] buildings = { { 50, 120, 55 }, { 120, 90, 50 }, { 185, 150, 60 }, { 260, 80, 45 }, { 320, 130, 55 },
                { 390, 100, 50 }, { 455, 160, 65 }, { 535, 85, 48 }, { 600, 110, 52 }, { 665, 140, 58 },
                { 740, 95, 50 }, { 805, 125, 55 } };
        for (int[] b : buildings) {
            int bx = b[0], bh = b[1], bw = b[2];
            if (bx + bw > w)
                continue;
            int by = groundY - bh;
            g2.setPaint(new GradientPaint(bx, by, new Color(70, 75, 85), bx + bw, by + bh, new Color(50, 55, 65)));
            g2.fillRect(bx, by, bw, bh);
            g2.setColor(new Color(240, 220, 100, 180));
            for (int wy = by + 12; wy < groundY - 15; wy += 22)
                for (int wx = bx + 8; wx < bx + bw - 10; wx += 16)
                    g2.fillRect(wx, wy, 8, 10);
            g2.setColor(new Color(90, 95, 105));
            g2.fillRect(bx - 2, by, bw + 4, 4);
        }
    }

    private void drawPipe(Graphics2D g2, int x, int y, int pipe, boolean highlight) {
        int cx = x + PipeModel.CELL / 2, cy = y + PipeModel.CELL / 2;
        int pw = 18;
        Color pipeCol = highlight ? new Color(60, 200, 130) : new Color(120, 130, 140);
        Color pipeHi = highlight ? new Color(100, 240, 170) : new Color(150, 160, 170);
        g2.setStroke(new BasicStroke(pw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.setColor(pipeCol);
        if ((pipe & PipeModel.T) != 0) g2.drawLine(cx, y, cx, cy);
        if ((pipe & PipeModel.B) != 0) g2.drawLine(cx, cy, cx, y + PipeModel.CELL);
        if ((pipe & PipeModel.L) != 0) g2.drawLine(x, cy, cx, cy);
        if ((pipe & PipeModel.R) != 0) g2.drawLine(cx, cy, x + PipeModel.CELL, cy);
        g2.setStroke(new BasicStroke(pw - 6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.setColor(pipeHi);
        if ((pipe & PipeModel.T) != 0) g2.drawLine(cx, y, cx, cy);
        if ((pipe & PipeModel.B) != 0) g2.drawLine(cx, cy, cx, y + PipeModel.CELL);
        if ((pipe & PipeModel.L) != 0) g2.drawLine(x, cy, cx, cy);
        if ((pipe & PipeModel.R) != 0) g2.drawLine(cx, cy, x + PipeModel.CELL, cy);
        g2.setColor(pipeCol);
        g2.fillOval(cx - pw / 2, cy - pw / 2, pw, pw);
    }

    private void drawTrayPiece(Graphics2D g2, TrayPiece p) {
        int x = p.x, y = p.y;
        g2.setColor(new Color(55, 45, 38, 200));
        g2.fillRoundRect(x, y, PipeModel.CELL, PipeModel.CELL, 10, 10);
        g2.setColor(new Color(100, 85, 70));
        g2.drawRoundRect(x, y, PipeModel.CELL, PipeModel.CELL, 10, 10);
        drawPipe(g2, x, y, p.pipeType, false);
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
