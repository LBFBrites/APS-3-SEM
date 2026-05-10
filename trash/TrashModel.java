package trash;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class TrashModel {
    public static final Color[] COLORS = {
            new Color(220, 50, 50),
            new Color(230, 200, 40),
            new Color(50, 120, 220),
            new Color(50, 180, 80)
    };
    public static final String[] COLOR_NAMES = { "RED", "YELLOW", "BLUE", "GREEN" };
    public static final String[] TRASH_LABELS = { "Metal", "Plastic", "Paper", "Glass" };
    public static final String[] TRASH_ICONS = { "\u2699", "\u2B23", "\u2630", "\u25CB" };

    public static final int BIN_W = 60;
    public static final int BIN_H = 50;

    public final ArrayList<Trash> items = new ArrayList<>();
    public final Random rng = new Random();

    public float binX = 400;
    public int binColorIdx = 0;
    public int score = 0;
    public int lives = 3;
    public boolean running = false;
    public boolean gameOver = false;
    public boolean leftHeld = false;
    public boolean rightHeld = false;
    public float spawnTimer = 0;
    public float spawnInterval = 60;
    public float baseSpeed = 2.5f;

    public int flashFrames = 0;
    public Color flashColor = null;

    public void startGame(int width) {
        items.clear();
        score = 0;
        lives = 3;
        binColorIdx = 0;
        binX = width / 2f;
        spawnInterval = 60;
        baseSpeed = 2.5f;
        running = true;
        gameOver = false;
        leftHeld = false;
        rightHeld = false;
    }

    public void tick(int width, int height) {
        float speed = 6f;
        if (leftHeld) binX = Math.max(BIN_W / 2f, binX - speed);
        if (rightHeld) binX = Math.min(width - BIN_W / 2f, binX + speed);

        spawnTimer++;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            float sx = 40 + rng.nextFloat() * (width - 80);
            int ci = rng.nextInt(4);
            items.add(new Trash(sx, baseSpeed + rng.nextFloat() * 1.2f, ci));
            if (spawnInterval > 25) spawnInterval -= 0.15f;
            if (baseSpeed < 5.5f) baseSpeed += 0.008f;
        }

        float binTop = height - 70;
        for (int i = items.size() - 1; i >= 0; i--) {
            Trash t = items.get(i);
            t.y += t.speed;
            t.rot += t.speed * 1.5f;

            if (t.y + 15 >= binTop && t.y - 15 <= binTop + BIN_H) {
                if (Math.abs(t.x - binX) < BIN_W / 2f + 12) {
                    if (t.colorIdx == binColorIdx) {
                        score += 10;
                        flashColor = new Color(80, 255, 130, 120);
                        flashFrames = 10;
                    } else {
                        lives--;
                        flashColor = new Color(255, 60, 60, 150);
                        flashFrames = 12;
                        if (lives <= 0) {
                            running = false;
                            gameOver = true;
                        }
                    }
                    items.remove(i);
                    continue;
                }
            }
            if (t.y > height + 40) items.remove(i);
        }

        if (flashFrames > 0) flashFrames--;
    }
}
