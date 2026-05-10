package trash;

import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TrashController {
    private final TrashModel model;
    private final TrashView view;
    private Timer loop;

    public TrashController(TrashModel model, TrashView view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        model.leftHeld = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        model.rightHeld = true;
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_NUMPAD1:
                        model.binColorIdx = 0;
                        break;
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        model.binColorIdx = 1;
                        break;
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        model.binColorIdx = 2;
                        break;
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_NUMPAD4:
                        model.binColorIdx = 3;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!model.running || model.gameOver) {
                            model.startGame(view.getWidth() > 0 ? view.getWidth() : 900);
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        model.leftHeld = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        model.rightHeld = false;
                        break;
                }
            }
        });

        loop = new Timer(16, e -> {
            if (model.running) {
                model.tick(view.getWidth() > 0 ? view.getWidth() : 900, view.getHeight() > 0 ? view.getHeight() : 620);
            }
            view.repaint();
        });
    }

    public void start() {
        loop.start();
        view.requestFocusInWindow();
    }

    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }
}
