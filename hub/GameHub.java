package hub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameHub extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final HubPanel hubPanel;

    private static final String HUB_VIEW = "HUB";
    private static final String GAME_VIEW = "GAME";

    private MiniGame activeGame;

    public GameHub() {
        super("★ Game Hub ★");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(860, 580));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(new Color(15, 15, 30));

        hubPanel = new HubPanel(this);
        mainContainer.add(hubPanel, HUB_VIEW);

        setContentPane(mainContainer);

        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (activeGame != null) {
                    returnToHub();
                } else {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "Exit Game Hub?", "Confirm Exit",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        dispose();
                        System.exit(0);
                    }
                }
                return true;
            }
            return false;
        });

        pack();
        setLocationRelativeTo(null);
    }

    public void registerMiniGame(int slot, MiniGame game) {
        hubPanel.setSlot(slot, game);
    }

    public void launchMiniGame(int index) {
        MiniGame game = hubPanel.getSlot(index);
        if (game == null)
            return;

        activeGame = game;
        JPanel gamePanel = game.createGamePanel(this::returnToHub);

        
        for (Component c : mainContainer.getComponents()) {
            if (GAME_VIEW.equals(c.getName())) {
                mainContainer.remove(c);
                break;
            }
        }

        gamePanel.setName(GAME_VIEW);
        mainContainer.add(gamePanel, GAME_VIEW);
        cardLayout.show(mainContainer, GAME_VIEW);
        gamePanel.requestFocusInWindow();
    }

    public void returnToHub() {
        if (activeGame != null) {
            activeGame.onClose();
            activeGame = null;
        }

        
        for (Component c : mainContainer.getComponents()) {
            if (GAME_VIEW.equals(c.getName())) {
                mainContainer.remove(c);
                break;
            }
        }

        cardLayout.show(mainContainer, HUB_VIEW);
        hubPanel.requestFocusInWindow();
    }
}
