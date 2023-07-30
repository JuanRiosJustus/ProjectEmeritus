package main.ui.panels;

import main.graphics.JScene;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.game.entity.Entity;

import java.awt.*;

public abstract class ControlPanelInnerTemplate extends JScene {

    public ImagePanel topLeft;
    protected Entity observing;

    public final JPanel topRight = new JPanel();
    public final JPanel topThird = new JPanel();
    public final JPanel middleThird = new JPanel();
    public final JPanel bottomThird = new JPanel();

    public final JButton button1 = getExitButton();
    public final JButton button2 = new JButton("2");
    public final JButton button3 = new JButton("3");
    public final JButton button4 = new JButton("4");

    public final JPanel innerScrollPaneContainer = new JPanel();

    public ControlPanelInnerTemplate(int width, int height, String name) {
        super(width, height, name);

        int topHeight = (int) (height * .45);
        int inv = (int) (height * .025);
        int bottomHeight = (int) (height * .4);
        int navHeight = (int) (height * .05);

        topThird.setPreferredSize(new Dimension(width, topHeight));
        topThird.add(createTopHalf(width, topHeight));
        add(topThird);

        // add(Box.createVerticalStrut(inv));

        middleThird.setPreferredSize(new Dimension(width, bottomHeight));
        add(middleThird);

        bottomThird.setPreferredSize(new Dimension(width, navHeight));
        bottomThird.setLayout(new GridLayout(1, 4));
        bottomThird.add(button1);
        bottomThird.add(button2);
        bottomThird.add(button3);
        bottomThird.add(button4);
        add(bottomThird);
    }

    private JPanel createTopHalf(int width, int height) {

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;

        topLeft = new ImagePanel((int) (width * .25), height);
        result.add(topLeft, gbc);

        gbc.gridx = 1;
        topRight.setPreferredSize(new Dimension((int) (width * .7), height));

        result.add(topRight, gbc);
        result.setPreferredSize(new Dimension(width, height));

        return result;
    }

    protected abstract JScrollPane createTopRightPanel(JComponent reference);
    protected abstract JScrollPane createMiddlePanel(JComponent reference);
    protected abstract void update();
}
