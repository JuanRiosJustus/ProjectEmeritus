package ui.panels;

import constants.ColorPalette;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanelInnerTemplate extends JScene {

    public ImagePanel topLeft;

    public final JPanel topRight = new JPanel();
    public final JPanel topThird = new JPanel();
    public final JPanel middleThird = new JPanel();
    public final JPanel bottomThird = new JPanel();

    public final JPanel innerScrollPaneContainer = new JPanel();

    public ControlPanelInnerTemplate(int width, int height, String name) {
        super(width, height, name);

        int topHeight = (int) (height * .45);
        int bottomHeight = (int) (height * .45);
        int navHeight = (int) (height * .05);

        topThird.setPreferredSize(new Dimension(width, topHeight));
        topThird.add(createTopHalf(width, topHeight));
        add(topThird);

        middleThird.setPreferredSize(new Dimension(width, bottomHeight));
        add(middleThird);

        bottomThird.setPreferredSize(new Dimension(width, navHeight));
        bottomThird.setLayout(new GridLayout(1, 4));
        bottomThird.add(getExitButton());
        bottomThird.add(new JButton(""));
        bottomThird.add(new JButton(""));
        bottomThird.add(new JButton(""));
        add(bottomThird);

        setBackground(ColorPalette.BEIGE);
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

    private JPanel createNavigationPanel() {

    }

    // private JScrollPane createBottomHalf(int width, int height) {
    //     JPanel result = innerScrollPaneContainer;       
    //     // result.setBackground(ColorPalette.getRandomColor());
    //     result.setLayout(new GridBagLayout());
    //     result.setPreferredSize(new Dimension(width, (int) (height * 1)));
    //     result.setBorder(new EmptyBorder(10, 10, 10, 10));

    //     GridBagConstraints g = new GridBagConstraints();
    //     g.weighty = 1;
    //     g.weightx = 1;
    //     g.ipadx = 0;
    //     g.ipady = 110;
    //     g.fill = GridBagConstraints.REMAINDER;


    //     JScrollPane scroller = new JScrollPane();
    //     scroller.getViewport().add(result, g);
    //     scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    //     scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    //     scroller.setPreferredSize(new Dimension(width, height));
    //     scroller.getViewport().setPreferredSize(new Dimension(width * 2, height * 2));

    //     scroller.getViewport().setBackground(ColorPalette.BLUE);

    //     scroller.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
    //     scroller.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

    //     return scroller;
    // }
}
