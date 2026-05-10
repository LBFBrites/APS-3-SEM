package factory;

import hub.MiniGame;
import javax.swing.*;
import java.awt.Color;

public class EcoFactoryGame extends MiniGame {
    private FactoryController controller;

    @Override
    public String getName() {
        return "factory-inator";
    }

    @Override
    public String getDescription() {
        return "Fix the factory issues! Use Filters for Smoke, Bricks for Oil Leaks, and Trucks for Nuclear Waste.";
    }

    @Override
    public String getIconSymbol() {
        return "\uD83C\uDFED";
    } 

    @Override
    public Color getAccentColor() {
        return new Color(200, 100, 60);
    }

    @Override
    public JPanel createGamePanel(Runnable onBack) {
        FactoryModel model = new FactoryModel();
        FactoryView view = new FactoryView(model, onBack);
        controller = new FactoryController(model, view);
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
