package ui.panels;

import constants.ColorPalette;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanelSceneTemplate extends JScene {

    public SelectionPanel selectionPanel;
    public final JPanel topHalf = new JPanel();
    public final JPanel topRight = new JPanel();
    public final JPanel bottomHalf = new JPanel();
    public final JScrollPane scroller = new JScrollPane();
    public final JPanel innerScrollPaneContainer = new JPanel();
    private final int PADDING = 5;
    public ControlPanelSceneTemplate(int width, int height, String name) {
        super(width, height, name);

        int topHeight = (int) (height * .45);
        int bottomHeight = (int) (height * .5);

        topHalf.setPreferredSize(new Dimension(width, topHeight));
        topHalf.add(createTopHalf(width, topHeight));
        add(topHalf);

        bottomHalf.setPreferredSize(new Dimension(width, bottomHeight));
        bottomHalf.add(createBottomHalf(width, bottomHeight));
        add(bottomHalf);
    }

    private JPanel createTopHalf(int width, int height) {

        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        selectionPanel = new SelectionPanel((int) (width * .25), height);
//        selectionPanel.setBackground(ColorPalette.getRandomColor());

        gbc.gridy = 0;
        gbc.gridx = 0;
        result.add(selectionPanel, gbc);

        gbc.gridx = 1;
        topRight.setPreferredSize(new Dimension((int) (width * .7), height));
        result.add(topRight, gbc);

        result.setPreferredSize(new Dimension(width, height));

        return result;
    }

    private JScrollPane createBottomHalf(int width, int height) {
        JPanel result = innerScrollPaneContainer;
//        result.setBackground(ColorPalette.getRandomColor());
        result.setLayout(new GridBagLayout());
        result.setPreferredSize(new Dimension(width, (int) (height * 2)));
        result.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints g = new GridBagConstraints();
        g.weighty = 1;
        g.weightx = 1;
        g.ipadx = 0;
        g.ipady = 110;
        g.fill = GridBagConstraints.REMAINDER;


        scroller.getViewport().add(result, g);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setPreferredSize(new Dimension(width, height));
        scroller.getViewport().setPreferredSize(new Dimension(width, height));

        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scroller.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        return scroller;
    }
}
