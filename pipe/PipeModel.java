package pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PipeModel {
    public static final int T = 1, R = 2, B = 4, L = 8;
    public static final int PIPE_H = R | L;
    public static final int PIPE_V = T | B;
    public static final int PIPE_TR = T | R;
    public static final int PIPE_TL = T | L;
    public static final int PIPE_BR = B | R;
    public static final int PIPE_BL = B | L;

    public static final int GCOLS = 6, GROWS = 3, CELL = 64;

    public int[] grid = new int[GCOLS * GROWS];
    public int[] solPipe;
    public int[] solPos;
    public ArrayList<TrayPiece> tray = new ArrayList<>();
    public TrayPiece dragging = null;
    public int dragOffX, dragOffY, dragX, dragY;

    public int timeLeft = 30;
    public boolean running = false, won = false, lost = false;
    public float floodLevel = 0;

    public int gridOX, gridOY;
    public int trayOX, trayOY;

    public final Random rng = new Random();

    public static int opposite(int dir) {
        switch (dir) {
            case T: return B;
            case B: return T;
            case R: return L;
            case L: return R;
        }
        return 0;
    }

    public static int dx(int dir) {
        return dir == R ? 1 : dir == L ? -1 : 0;
    }

    public static int dy(int dir) {
        return dir == B ? 1 : dir == T ? -1 : 0;
    }

    public void recalcLayout(int w, int h) {
        if (w <= 0) w = 900;
        if (h <= 0) h = 620;
        gridOX = (w - GCOLS * CELL) / 2;
        gridOY = h / 2 - 10;
        int pCount = solPipe != null ? solPipe.length : 8;
        trayOX = (w - pCount * (CELL + 10)) / 2;
        trayOY = h - CELL - 30;
    }

    public void returnToTray(TrayPiece p) {
        p.placed = false;
        int idx = tray.indexOf(p);
        p.x = trayOX + idx * (CELL + 10);
        p.y = trayOY;
    }

    public int findGridIdx(TrayPiece p) {
        for (int i = 0; i < grid.length; i++) {
            int gx = gridOX + (i % GCOLS) * CELL;
            int gy = gridOY + (i / GCOLS) * CELL;
            if (Math.abs(p.x - gx) < 5 && Math.abs(p.y - gy) < 5)
                return i;
        }
        return -1;
    }

    public int getGridCellAt(int px, int py) {
        for (int i = 0; i < GCOLS * GROWS; i++) {
            int gx = gridOX + (i % GCOLS) * CELL;
            int gy = gridOY + (i / GCOLS) * CELL;
            if (px >= gx && px < gx + CELL && py >= gy && py < gy + CELL)
                return i;
        }
        return -1;
    }

    public void generatePuzzle() {
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<Integer> dirs = new ArrayList<>();
        int cx = 0, cy = 0;
        path.add(cy * GCOLS + cx);

        while (cx < GCOLS - 1 || cy < GROWS - 1) {
            ArrayList<int[]> options = new ArrayList<>();
            if (cx + 1 < GCOLS && !path.contains(cy * GCOLS + cx + 1))
                options.add(new int[] { R, cx + 1, cy });
            if (cy + 1 < GROWS && !path.contains((cy + 1) * GCOLS + cx))
                options.add(new int[] { B, cx, cy + 1 });
            if (cy - 1 >= 0 && !path.contains((cy - 1) * GCOLS + cx))
                options.add(new int[] { T, cx, cy - 1 });

            if (options.isEmpty())
                break;

            int[] choice;
            boolean needRight = cx < GCOLS - 1;
            boolean needDown = cy < GROWS - 1;
            ArrayList<int[]> good = new ArrayList<>();
            for (int[] o : options) {
                if ((o[0] == R && needRight) || (o[0] == B && needDown))
                    good.add(o);
            }
            if (!good.isEmpty() && rng.nextFloat() < 0.7f) {
                choice = good.get(rng.nextInt(good.size()));
            } else {
                choice = options.get(rng.nextInt(options.size()));
            }

            dirs.add(choice[0]);
            cx = choice[1];
            cy = choice[2];
            path.add(cy * GCOLS + cx);
        }

        solPipe = new int[path.size()];
        solPos = new int[path.size()];

        for (int i = 0; i < path.size(); i++) {
            int fromDir = 0, toDir = 0;
            if (i == 0) {
                fromDir = T;
                toDir = dirs.get(0);
            } else if (i == path.size() - 1) {
                toDir = B;
                fromDir = opposite(dirs.get(i - 1));
            } else {
                fromDir = opposite(dirs.get(i - 1));
                toDir = dirs.get(i);
            }
            solPipe[i] = fromDir | toDir;
            solPos[i] = path.get(i);
        }

        recalcLayout(900, 620);
        ArrayList<Integer> order = new ArrayList<>();
        for (int i = 0; i < solPipe.length; i++)
            order.add(i);
        Collections.shuffle(order, rng);

        for (int k = 0; k < order.size(); k++) {
            int i = order.get(k);
            TrayPiece tp = new TrayPiece(solPipe[i], solPos[i]);
            tp.x = trayOX + k * (CELL + 10);
            tp.y = trayOY;
            tray.add(tp);
        }
    }
}
