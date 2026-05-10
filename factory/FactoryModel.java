package factory;

import java.util.ArrayList;
import java.util.Random;

public class FactoryModel {
    public static final int TOOL_FILTER = 0;
    public static final int TOOL_BRICK = 1;
    public static final int TOOL_TRUCK = 2;

    public int selectedTool = -1;
    public ArrayList<Issue> issues = new ArrayList<>();
    public int score = 0;
    public int lives = 3;
    public boolean running = false;
    public boolean gameOver = false;

    public float spawnTimer = 0;
    public float spawnRate = 120;
    public float issueDecayRate = 0.3f;

    public int[] factX = { 150, 400, 650 };
    public int factW = 120;
    public int factH = 150;
    public int groundY = 400;

    private final Random rng = new Random();

    public void startGame() {
        score = 0;
        lives = 3;
        issues.clear();
        selectedTool = -1;
        spawnTimer = 0;
        spawnRate = 120;
        issueDecayRate = 0.2f;
        running = true;
        gameOver = false;
    }

    public void updateGame() {
        spawnTimer++;
        if (spawnTimer >= spawnRate) {
            spawnTimer = 0;
            spawnIssue();
            if (spawnRate > 40) spawnRate -= 2;
            if (issueDecayRate < 0.6f) issueDecayRate += 0.01f;
        }

        for (int i = issues.size() - 1; i >= 0; i--) {
            Issue is = issues.get(i);
            is.timeLeft -= issueDecayRate;
            if (is.timeLeft <= 0) {
                issues.remove(i);
                lives--;
                if (lives <= 0) {
                    running = false;
                    gameOver = true;
                }
            }
        }
    }

    private void spawnIssue() {
        int fIdx = rng.nextInt(3);
        int type = rng.nextInt(3);

        for (Issue i : issues) {
            if (i.factoryIdx == fIdx && i.type == type) return;
        }

        float x = 0, y = 0;
        int fx = factX[fIdx];
        if (type == Issue.SMOKE) {
            x = fx + factW - 20; 
            y = groundY - factH - 40;
        } else if (type == Issue.OIL) {
            x = fx + factW / 2f; 
            y = groundY - 10;
        } else if (type == Issue.NUKE) {
            x = fx - 20; 
            y = groundY - 15;
        }
        issues.add(new Issue(type, fIdx, x, y));
    }
}
