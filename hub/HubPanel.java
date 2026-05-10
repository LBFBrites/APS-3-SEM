package hub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class HubPanel extends JPanel {

    private static final int CARD_WIDTH = 240;
    private static final int CARD_HEIGHT = 320;
    private static final int CARD_GAP = 36;
    private static final int CARD_ARC = 28;

    private final MiniGame[] slots = new MiniGame[3];
    @SuppressWarnings("unused")
    private final GameHub hub;

    
    private final float[] cardScale = { 1f, 1f, 1f };
    private final float[] cardGlow = { 0f, 0f, 0f };
    private int hoveredCard = -1;
    private Timer animTimer;

    private long startTime;

    
    private final float[][] stars = new float[80][3]; 

    public HubPanel(GameHub hub) {
        this.hub = hub;
        setBackground(new Color(15, 15, 30));
        setPreferredSize(new Dimension(900, 620));
        startTime = System.currentTimeMillis();

        
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = (float) Math.random();
            stars[i][1] = (float) Math.random();
            stars[i][2] = (float) (Math.random() * 0.6 + 0.2);
        }

        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int prev = hoveredCard;
                hoveredCard = getCardAt(e.getX(), e.getY());
                if (prev != hoveredCard) {
                    setCursor(hoveredCard >= 0 && slots[hoveredCard] != null
                            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            : Cursor.getDefaultCursor());
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = getCardAt(e.getX(), e.getY());
                if (idx >= 0 && slots[idx] != null) {
                    hub.launchMiniGame(idx);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredCard = -1;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        
        animTimer = new Timer(16, e -> {
            for (int i = 0; i < 3; i++) {
                float targetScale = (i == hoveredCard && slots[i] != null) ? 1.07f : 1.0f;
                float targetGlow = (i == hoveredCard && slots[i] != null) ? 1.0f : 0.0f;
                cardScale[i] += (targetScale - cardScale[i]) * 0.15f;
                cardGlow[i] += (targetGlow - cardGlow[i]) * 0.12f;
            }
            repaint();
        });
        animTimer.start();
    }

    public void setSlot(int index, MiniGame game) {
        if (index < 0 || index > 2)
            throw new IllegalArgumentException("Slot must be 0-2");
        slots[index] = game;
        repaint();
    }

    public MiniGame getSlot(int index) {
        return slots[index];
    }

    private int getCardAt(int px, int py) {
        int totalWidth = 3 * CARD_WIDTH + 2 * CARD_GAP;
        int startX = (getWidth() - totalWidth) / 2;
        int startY = (getHeight() - CARD_HEIGHT) / 2 + 30;

        for (int i = 0; i < 3; i++) {
            int cx = startX + i * (CARD_WIDTH + CARD_GAP);
            if (px >= cx && px <= cx + CARD_WIDTH && py >= startY && py <= startY + CARD_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        long elapsed = System.currentTimeMillis() - startTime;

        
        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(10, 10, 35),
                w, h, new Color(25, 15, 50));
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        
        for (float[] star : stars) {
            float twinkle = (float) (star[2] + 0.2 * Math.sin(elapsed * 0.002 + star[0] * 20));
            twinkle = Math.max(0.1f, Math.min(1f, twinkle));
            int alpha = (int) (twinkle * 200);
            g2.setColor(new Color(200, 210, 255, alpha));
            int sx = (int) (star[0] * w);
            int sy = (int) (star[1] * h);
            int size = twinkle > 0.6f ? 3 : 2;
            g2.fillOval(sx, sy, size, size);
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
        String title = "\u2605  GAME HUB  \u2605";
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(title)) / 2;
        int ty = 72;

        float glowPhase = (float) (0.5 + 0.5 * Math.sin(elapsed * 0.003));
        g2.setColor(new Color(120, 80, 255, (int) (40 * glowPhase)));
        g2.drawString(title, tx, ty + 2);
        g2.setColor(new Color(220, 200, 255));
        g2.drawString(title, tx, ty);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        String subtitle = "Choose a minigame to play";
        fm = g2.getFontMetrics();
        g2.setColor(new Color(160, 150, 200, 180));
        g2.drawString(subtitle, (w - fm.stringWidth(subtitle)) / 2, ty + 32);

        int totalWidth = 3 * CARD_WIDTH + 2 * CARD_GAP;
        int startX = (w - totalWidth) / 2;
        int startY = (h - CARD_HEIGHT) / 2 + 30;

        for (int i = 0; i < 3; i++) {
            float floatOffset = (float) (4 * Math.sin(elapsed * 0.002 + i * 1.2));
            drawCard(g2, i, startX + i * (CARD_WIDTH + CARD_GAP),
                    (int) (startY + floatOffset), elapsed);
        }

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        String footer = "Press ESC to quit  •  Click a card to play";
        fm = g2.getFontMetrics();
        g2.setColor(new Color(120, 115, 160, 120));
        g2.drawString(footer, (w - fm.stringWidth(footer)) / 2, h - 24);

        g2.dispose();
    }

    private void drawCard(Graphics2D g2, int idx, int x, int y, long elapsed) {
        Graphics2D gc = (Graphics2D) g2.create();

        float scale = cardScale[idx];
        float glow = cardGlow[idx];

        
        int cx = x + CARD_WIDTH / 2;
        int cy = y + CARD_HEIGHT / 2;
        gc.translate(cx, cy);
        gc.scale(scale, scale);
        gc.translate(-cx, -cy);

        MiniGame game = slots[idx];
        Color accent = game != null ? game.getAccentColor() : new Color(80, 80, 100);

        if (glow > 0.05f) {
            int glowAlpha = (int) (50 * glow);
            for (int r = 16; r >= 2; r -= 2) {
                gc.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.max(0, Math.min(255, glowAlpha / (r / 2 + 1)))));
                gc.fill(new RoundRectangle2D.Float(x - r, y - r,
                        CARD_WIDTH + 2 * r, CARD_HEIGHT + 2 * r, CARD_ARC + r, CARD_ARC + r));
            }
        }

        Color cardBg = new Color(30, 28, 55, 220);
        gc.setColor(cardBg);
        RoundRectangle2D cardShape = new RoundRectangle2D.Float(x, y, CARD_WIDTH, CARD_HEIGHT, CARD_ARC, CARD_ARC);
        gc.fill(cardShape);

        gc.setClip(cardShape);
        GradientPaint stripe = new GradientPaint(x, y, accent, x + CARD_WIDTH, y,
                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80));
        gc.setPaint(stripe);
        gc.fillRect(x, y, CARD_WIDTH, 6);
        gc.setClip(null);

        gc.setStroke(new BasicStroke(1.5f));
        gc.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                60 + (int) (40 * glow)));
        gc.draw(cardShape);

        if (game != null) {
            gc.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
            FontMetrics fmIcon = gc.getFontMetrics();
            String icon = game.getIconSymbol();
            int iconX = x + (CARD_WIDTH - fmIcon.stringWidth(icon)) / 2;
            int iconY = y + 100;
            gc.setColor(Color.WHITE);
            gc.drawString(icon, iconX, iconY);

            gc.setFont(new Font("Segoe UI", Font.BOLD, 22));
            FontMetrics fmName = gc.getFontMetrics();
            String name = game.getName();
            gc.setColor(new Color(230, 225, 255));
            gc.drawString(name, x + (CARD_WIDTH - fmName.stringWidth(name)) / 2, y + 155);

            gc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            gc.setColor(new Color(170, 165, 200));
            drawWrappedText(gc, game.getDescription(), x + 20, y + 180, CARD_WIDTH - 40);

            int btnW = 120;
            int btnH = 36;
            int btnX = x + (CARD_WIDTH - btnW) / 2;
            int btnY = y + CARD_HEIGHT - 58;
            @SuppressWarnings("unused")
            float btnGlow = glow;

            RoundRectangle2D btn = new RoundRectangle2D.Float(btnX, btnY, btnW, btnH, 18, 18);
            GradientPaint btnGrad = new GradientPaint(btnX, btnY, accent,
                    btnX + btnW, btnY + btnH, accent.darker());
            gc.setPaint(btnGrad);
            gc.fill(btn);

            gc.setFont(new Font("Segoe UI", Font.BOLD, 15));
            gc.setColor(Color.WHITE);
            FontMetrics fmBtn = gc.getFontMetrics();
            String playText = "\u25B6  PLAY";
            gc.drawString(playText, btnX + (btnW - fmBtn.stringWidth(playText)) / 2,
                    btnY + (btnH + fmBtn.getAscent() - fmBtn.getDescent()) / 2);
        } else {
            gc.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            FontMetrics fm = gc.getFontMetrics();
            String lock = "\uD83D\uDD12";
            gc.setColor(new Color(80, 75, 110));
            gc.drawString(lock, x + (CARD_WIDTH - fm.stringWidth(lock)) / 2, y + 110);

            gc.setFont(new Font("Segoe UI", Font.BOLD, 18));
            fm = gc.getFontMetrics();
            String empty = "Slot " + (idx + 1);
            gc.setColor(new Color(100, 95, 130));
            gc.drawString(empty, x + (CARD_WIDTH - fm.stringWidth(empty)) / 2, y + 155);

            gc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            gc.setColor(new Color(80, 75, 110));
            fm = gc.getFontMetrics();
            String hint = "No game loaded";
            gc.drawString(hint, x + (CARD_WIDTH - fm.stringWidth(hint)) / 2, y + 185);
        }

        gc.dispose();
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = y;

        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth) {
                g2.drawString(line.toString(), x, lineY);
                lineY += fm.getHeight();
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), x, lineY);
        }
    }

    public void stopAnimations() {
        if (animTimer != null)
            animTimer.stop();
    }
}
