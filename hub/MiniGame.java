package hub;

import javax.swing.*;

public abstract class MiniGame {
    public abstract String getName();
    public abstract String getDescription();
    public abstract String getIconSymbol();
    public abstract java.awt.Color getAccentColor();
    public abstract JPanel createGamePanel(Runnable onBack);
    public void onClose() {}
}
