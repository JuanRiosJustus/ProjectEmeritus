package ui.panels;

import constants.Constants;
import graphics.JScene;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class SettingsPanel extends JScene {

    public final JCheckBox showCoordinates = new JCheckBox("Show Coordinates");
    public final JCheckBox autoEndTurns = new JCheckBox("Auto End Turns");
    public final JCheckBox fastForward = new JCheckBox("Fast Forward");

    public final JPanel container = new JPanel();

    public SettingsPanel() {
        super(Constants.SIDE_BAR_WIDTH, Constants.SIDE_BAR_MAIN_PANEL_HEIGHT, "Settings");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.VERTICAL;

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(showCoordinates);
        container.add(autoEndTurns);
        container.add(fastForward);
//        fastForward.setSelected(true);
        autoEndTurns.setSelected(true);
//        showCoordinates.setSelected(true);

        add(container, gbc);
        add(getExitButton(), gbc);
    }
}
