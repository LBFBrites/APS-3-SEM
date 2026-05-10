package pipe;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PipeController {
    private final PipeModel model;
    private final PipeView view;
    private Timer countdownTimer;
    private Timer renderTimer;

    public PipeController(PipeModel model, PipeView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!model.running && !model.won && !model.lost) {
                    startGame();
                    return;
                }
                if (model.won || model.lost) {
                    startGame();
                    return;
                }
                for (int i = model.tray.size() - 1; i >= 0; i--) {
                    TrayPiece p = model.tray.get(i);
                    if (e.getX() >= p.x && e.getX() <= p.x + PipeModel.CELL &&
                            e.getY() >= p.y && e.getY() <= p.y + PipeModel.CELL) {
                        model.dragging = p;
                        if (p.placed) {
                            model.grid[model.findGridIdx(p)] = -1;
                            p.placed = false;
                        }
                        model.dragOffX = e.getX() - p.x;
                        model.dragOffY = e.getY() - p.y;
                        model.dragX = e.getX();
                        model.dragY = e.getY();
                        model.tray.remove(i);
                        model.tray.add(p);
                        break;
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (model.dragging != null) {
                    model.dragging.x = e.getX() - model.dragOffX;
                    model.dragging.y = e.getY() - model.dragOffY;
                    model.dragX = e.getX();
                    model.dragY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (model.dragging != null) {
                    int gi = model.getGridCellAt(e.getX(), e.getY());
                    if (gi >= 0 && model.grid[gi] == -1) {
                        model.dragging.x = model.gridOX + (gi % PipeModel.GCOLS) * PipeModel.CELL;
                        model.dragging.y = model.gridOY + (gi / PipeModel.GCOLS) * PipeModel.CELL;
                        model.dragging.placed = true;
                        model.grid[gi] = model.dragging.pipeType;
                    } else if (gi >= 0 && model.grid[gi] != -1) {
                        model.returnToTray(model.dragging);
                    } else {
                        model.returnToTray(model.dragging);
                    }
                    model.dragging = null;
                }
            }
        };
        view.addMouseListener(ma);
        view.addMouseMotionListener(ma);

        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!model.running && !model.won && !model.lost) {
                        startGame();
                    } else if (model.won || model.lost) {
                        startGame();
                    } else {
                        submitAnswer();
                    }
                }
            }
        });

        renderTimer = new Timer(16, e -> {
            if (model.lost && model.floodLevel < 1f) {
                model.floodLevel += 0.008f;
            }
            view.repaint();
        });
    }

    private void startGame() {
        java.util.Arrays.fill(model.grid, -1);
        model.tray.clear();
        model.generatePuzzle();
        model.timeLeft = 30;
        model.running = true;
        model.won = false;
        model.lost = false;
        model.floodLevel = 0;
        if (countdownTimer != null)
            countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            if (!model.running) return;
            model.timeLeft--;
            if (model.timeLeft <= 0) {
                model.running = false;
                model.lost = true;
                ((Timer) e.getSource()).stop();
            }
        });
        countdownTimer.start();
        view.requestFocusInWindow();
    }

    private void submitAnswer() {
        if (!model.running) return;
        for (TrayPiece p : model.tray) {
            if (!p.placed) {
                model.running = false;
                model.lost = true;
                if (countdownTimer != null) countdownTimer.stop();
                return;
            }
        }

        int startIdx = 0;
        boolean[] visited = new boolean[PipeModel.GCOLS * PipeModel.GROWS];
        boolean correct = tracePath(startIdx, PipeModel.T, visited);

        model.running = false;
        if (countdownTimer != null) countdownTimer.stop();
        if (correct) {
            model.won = true;
        } else {
            model.lost = true;
        }
    }

    private boolean tracePath(int idx, int entryDir, boolean[] visited) {
        if (idx < 0 || idx >= PipeModel.GCOLS * PipeModel.GROWS) return false;
        if (visited[idx]) return false;
        int pipe = model.grid[idx];
        if (pipe == -1) return false;
        if ((pipe & entryDir) == 0) return false;

        visited[idx] = true;
        int col = idx % PipeModel.GCOLS, row = idx / PipeModel.GCOLS;

        int exits = pipe & ~entryDir;
        if (exits == 0) return false;

        if (col == PipeModel.GCOLS - 1 && row == PipeModel.GROWS - 1 && (exits & PipeModel.B) != 0)
            return true;

        int[] tryDirs = { PipeModel.T, PipeModel.R, PipeModel.B, PipeModel.L };
        for (int d : tryDirs) {
            if ((exits & d) == 0) continue;
            int nc = col + PipeModel.dx(d), nr = row + PipeModel.dy(d);
            if (nc < 0 || nc >= PipeModel.GCOLS || nr < 0 || nr >= PipeModel.GROWS) continue;
            if (tracePath(nr * PipeModel.GCOLS + nc, PipeModel.opposite(d), visited))
                return true;
        }
        return false;
    }

    public void start() {
        renderTimer.start();
        view.requestFocusInWindow();
    }

    public void stop() {
        if (countdownTimer != null) countdownTimer.stop();
        if (renderTimer != null) renderTimer.stop();
    }
}
