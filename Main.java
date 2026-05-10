import hub.GameHub;
import trash.RecycleRushGame;
import pipe.PipePuzzleGame;
import factory.EcoFactoryGame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            GameHub hub = new GameHub();

            hub.registerMiniGame(0, new RecycleRushGame());
            hub.registerMiniGame(1, new PipePuzzleGame());
            hub.registerMiniGame(2, new EcoFactoryGame());

            hub.setVisible(true);
        });
    }
}
