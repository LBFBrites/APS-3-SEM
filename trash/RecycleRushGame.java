package trash;

import hub.MiniGame;
import javax.swing.JPanel;
import java.awt.Color;

public class RecycleRushGame extends MiniGame {
    private TrashController controller;

    @Override
    public String getName() {
        return "trash-inator";
    }

    @Override
    public String getDescription() {
        return "Catch falling recyclables! Press 1-4 to pick a bin color. Match the trash color or lose a life!";
    }

    @Override
    public String getIconSymbol() {
        return "\u267B";
    }

    @Override
    public Color getAccentColor() {
        return new Color(60, 200, 100);
    }

    @Override
    public JPanel createGamePanel(Runnable onBack) {
        TrashModel model = new TrashModel();
        TrashView view = new TrashView(model);
        controller = new TrashController(model, view);
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
