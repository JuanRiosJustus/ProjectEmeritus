package main.ui.custom;

import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.components.OutlineButton;
import main.ui.huds.controls.JGamePanel;

import javax.swing.*;
import java.awt.*;

public class VerticalAccordionPanel extends JPanel {
    private final JScrollPane scrollPane;
    private final JButton mToggleButton;
    private final JComponent mContentPanel;
    protected final int mWidth;
    protected final int mCollapsedHeight;
    protected final int mExpandedHeight;

    public VerticalAccordionPanel() {
        scrollPane = new JScrollPane();
        mToggleButton = new JButton();
        mContentPanel = new JPanel();
        mWidth = 0;
        mCollapsedHeight = 0;
        mExpandedHeight = 0;
    }
    public VerticalAccordionPanel(String title, Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        this(title, new GameUI(), mainColor, width, collapsedHeight, expandedHeight);
    }
    public VerticalAccordionPanel(String title, JComponent customContent, Color mainColor,
                                  int width, int collapsedHeight, int expandedHeight) {
        mCollapsedHeight = collapsedHeight;
        mExpandedHeight = collapsedHeight + expandedHeight;
        mWidth = width;

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
        scrollPane = new TranslucentScrollPane(mContentPanel, mainColor,  width, expandedHeight);
        scrollPane.setVisible(false); // Set collapsed
        add(scrollPane);

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
        scrollPane.setVisible(!scrollPane.isVisible());
        setMinimumSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setMaximumSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setPreferredSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        revalidate();
        repaint();
    }
    public boolean isShowingAccordionContent() { return scrollPane.isShowing(); }
    public JComponent getContentPanel() { return mContentPanel; }
}
