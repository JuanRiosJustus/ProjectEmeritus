package main.game.main.ui;

import javax.swing.JButton;
import javax.swing.JPanel;

public class UiShowingManager {

//    private Map<String, JPanel>
    public void link(JPanel source, JButton sourceToDestination, JPanel destination, JButton destinationToSource) {
        sourceToDestination.addActionListener(e -> {
            source.setVisible(false);
            destination.setVisible(true);
        });

        destinationToSource.addActionListener(e -> {
            destination.setVisible(false);
            source.setVisible(true);
        });
    }
}
