package main.ui.custom;

import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.ui.components.OutlineButton;

import javax.swing.*;
import java.awt.*;

public class AccordionPanelV1 extends JPanel {
    private final static Color DEFAULT_CONFIG_COLOR = ColorPalette.getRandomColor();
    private final JButton mToggleButton;
    private final JComponent mContentPanel;
    private final int mCollapsedHeight;
    private final int mExpandedHeight;

    public AccordionPanelV1(String title, JComponent customContent, int width, int height) {
        this(title, customContent, DEFAULT_CONFIG_COLOR, width, height);
    }
    public AccordionPanelV1(String title, JComponent customContent, Color mainColor, int width, int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
        mExpandedHeight = collapsedHeight + customContent.getPreferredSize().height;

        // Set FlowLayout for the main panel with no gaps
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setMinimumSize(new Dimension(width, collapsedHeight));
        setMaximumSize(new Dimension(width, collapsedHeight));
        setPreferredSize(new Dimension(width, collapsedHeight));

        // Create the button for the accordion header
        mToggleButton = new OutlineButton(title);
        mToggleButton.setPreferredSize(new Dimension(width, collapsedHeight));
        mToggleButton.setBackground(getBackground());
        mToggleButton.setFont(FontPool.getInstance().getFontForHeight(collapsedHeight));
        mToggleButton.setFocusPainted(false);
        add(mToggleButton);

        // Use the custom content panel as the content to reveal
        mContentPanel = customContent;
        mContentPanel.setVisible(false); // Start collapsed
        add(mContentPanel);

        // Add action listener to toggle content visibility and adjust panel size
        Color defaultColor = mainColor;
        Color pressedColor = defaultColor.darker().darker();
        mToggleButton.setBackground(defaultColor);
        setBackground(defaultColor);
        mToggleButton.addActionListener(e -> {
            toggleContent();
            mToggleButton.setBackground(mToggleButton.getBackground() == defaultColor ? pressedColor : defaultColor);
        });
    }

    private void toggleContent() {
        mContentPanel.setVisible(!mContentPanel.isVisible());
        setMinimumSize(new Dimension(getWidth(), mContentPanel.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setMaximumSize(new Dimension(getWidth(), mContentPanel.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setPreferredSize(new Dimension(getWidth(), mContentPanel.isVisible() ? mExpandedHeight : mCollapsedHeight));
        revalidate();
        repaint();
    }
}