package factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FactoryController {
    private final FactoryModel model;
    private final FactoryView view;
    private Timer loopTimer;

    public FactoryController(FactoryModel model, FactoryView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!model.running && !model.gameOver) {
                    model.startGame();
                    view.requestFocusInWindow();
                    return;
                }
                if (model.gameOver) {
                    model.startGame();
                    view.requestFocusInWindow();
                    return;
                }

                int mx = e.getX(), my = e.getY();
                
                for (int i = 0; i < 3; i++) {
                    int tx = 250 + i * 150;
                    int ty = 480;
                    if (mx >= tx && mx <= tx + 100 && my >= ty && my <= ty + 80) {
                        model.selectedTool = i;
                        view.repaint();
                        return;
                    }
                }

                if (model.selectedTool != -1) {
                    for (int i = model.issues.size() - 1; i >= 0; i--) {
                        Issue is = model.issues.get(i);
                        float dist = (float) Math.hypot(mx - is.x, my - is.y);
                        if (dist < 50) { 
                            if ((is.type == Issue.SMOKE && model.selectedTool == FactoryModel.TOOL_FILTER) ||
                                (is.type == Issue.OIL && model.selectedTool == FactoryModel.TOOL_BRICK) ||
                                (is.type == Issue.NUKE && model.selectedTool == FactoryModel.TOOL_TRUCK)) {
                                model.score += 20;
                                model.issues.remove(i);
                            } else {
                                model.lives--;
                                if (model.lives <= 0) {
                                    model.running = false;
                                    model.gameOver = true;
                                }
                            }
                            model.selectedTool = -1; 
                            view.repaint();
                            break;
                        }
                    }
                }
            }
        });

        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!model.running) {
                        model.startGame();
                    }
                }
                if (model.running) {
                    if (e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_NUMPAD1)
                        model.selectedTool = FactoryModel.TOOL_FILTER;
                    if (e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_NUMPAD2)
                        model.selectedTool = FactoryModel.TOOL_BRICK;
                    if (e.getKeyCode() == KeyEvent.VK_3 || e.getKeyCode() == KeyEvent.VK_NUMPAD3)
                        model.selectedTool = FactoryModel.TOOL_TRUCK;
                }
                view.repaint();
            }
        });

        loopTimer = new Timer(16, e -> {
            if (model.running) {
                model.updateGame();
            }
            view.repaint();
        });
    }

    public void start() {
        loopTimer.start();
        view.requestFocusInWindow();
    }

    public void stop() {
        if (loopTimer != null) {
            loopTimer.stop();
        }
    }
}
