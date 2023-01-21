package ui.panels;

import constants.ColorPalette;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;

public class ControlPanelSceneTemplate extends JScene {

    public SelectionPanel selectionPanel;
    public final JPanel topHalf;
    public JPanel topRight;
    public final JPanel bottomHalf;
    public ControlPanelSceneTemplate(int width, int height, String name) {
        super(width, height, name);
        ComponentUtils.setSize(this, width, height);

        int halfHeights = height / 2;

        topHalf = createTopHalf(width, halfHeights);
        add(topHalf);

        bottomHalf = createBottomHalf(width, halfHeights);
        add(bottomHalf);
    }

    private JPanel createTopHalf(int width, int height) {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        selectionPanel = new SelectionPanel((int) (width * .3), height);
        ComponentUtils.setSize(selectionPanel,(int) (width * .3), height);

        gbc.gridy = 0;
        gbc.gridx = 0;
        result.add(selectionPanel, gbc);

        gbc.gridx = 1;
        topRight = createTopRightPanel((int) (width * .7), height);
        ComponentUtils.setSize(topRight, (int) (width * .7), height);
        result.add(topRight, gbc);

        ComponentUtils.setSize(result, width, height);
        return result;
    }

    private JPanel createTopRightPanel(int width, int height) {
        JPanel firstGlancePanel = new JPanel();
//        firstGlancePanel.setBackground(ColorPalette.getRandomColor());
        ComponentUtils.setSize(firstGlancePanel, width, height);
        return firstGlancePanel;
    }

    private JPanel createBottomHalf(int width, int height) {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        ComponentUtils.setSize(result, width, height);

        return result;
    }
}
