package ui.screen;

import constants.ColorPalette;
import engine.Engine;
import utils.ComponentUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(int width, int height) {
        setSize(width, height);

        setLayout(null);
        int buttonHeight = 50;
        int buttonWidth = 150;

        int y = (int) (getHeight() * .8);

        JButton b1 = ComponentUtils.createJButton("Start");
        b1.setBounds((int) (getWidth() * .15), y, buttonWidth, buttonHeight);
        b1.setOpaque(false);

        JButton b2 = ComponentUtils.createJButton("Load");
        b2.setBounds((int) (getWidth() * .35), y, buttonWidth, buttonHeight);

        JButton b3 = ComponentUtils.createJButton("Settings");
        b3.setBounds((int) (getWidth() * .55), y, buttonWidth, buttonHeight);

        JButton b4 = ComponentUtils.createJButton("Exit");
        b4.setBounds((int) (getWidth() * .75), y, buttonWidth, buttonHeight);
        b4.addActionListener(e -> Engine.get().run());

        add(b1);
        add(b2);
        add(b3);
        add(b4);
        setBackground(ColorPalette.BEIGE);
    }
}
