package pipe;

import hub.MiniGame;
import javax.swing.JPanel;
import java.awt.Color;

public class PipePuzzleGame extends MiniGame {
    private PipeController controller;

    @Override
    public String getName() {
        return "pipe-inator";
    }

    @Override
    public String getDescription() {
        return "Connect the sewer pipes before the city floods! Drag pipes into the grid within 30 seconds.";
    }

    @Override
    public String getIconSymbol() {
        return "\u2692";
    }

    @Override
    public Color getAccentColor() {
        return new Color(80, 160, 220);
    }

    @Override
    public JPanel createGamePanel(Runnable onBack) {
        PipeModel model = new PipeModel();
        PipeView view = new PipeView(model);
        controller = new PipeController(model, view);
        controller.start();
        return view;
    }

    @Override
    public void onClose() {
        if (controller != null) {
            controller.stop();
        }
    }
}
