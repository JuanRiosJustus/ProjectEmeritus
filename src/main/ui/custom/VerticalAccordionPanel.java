package main.ui.custom;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineButton;
import main.ui.swing.TranslucentScrollPane;

import javax.swing.*;
import java.awt.*;

public class VerticalAccordionPanel extends JPanel {
    private JScrollPane scrollPane = new JScrollPane();
    private JButton mToggleButton = new JButton();
    private JPanel mContentPanel = new JPanel();
    protected int mWidth;
    protected int mCollapsedHeight;
    protected int mExpandedHeight;
    protected Color mColor;

    public VerticalAccordionPanel() {
        scrollPane = new JScrollPane();
        mToggleButton = new JButton();
        mContentPanel = new JPanel();
        mWidth = 0;
        mCollapsedHeight = 0;
        mExpandedHeight = 0;
        mColor = Color.WHITE;
    }
    public VerticalAccordionPanel(Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        initialize(null, mainColor, width, collapsedHeight, expandedHeight);
    }
    public VerticalAccordionPanel(JPanel custom, Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        initialize(custom, mainColor, width, collapsedHeight, expandedHeight);
    }

    private void toggleContent() {
        scrollPane.setVisible(!scrollPane.isVisible());
        setMinimumSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setMaximumSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        setPreferredSize(new Dimension(getWidth(), scrollPane.isVisible() ? mExpandedHeight : mCollapsedHeight));
        revalidate();
        repaint();
    }


    public void initialize(JPanel customContent, Color mainColor, int width, int collapsedHeight, int expandedHeight) {
        mCollapsedHeight = collapsedHeight;
        mExpandedHeight = collapsedHeight + expandedHeight;
        mWidth = width;

        // Set FlowLayout for the main panel with no gaps
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setMinimumSize(new Dimension(width, collapsedHeight));
        setMaximumSize(new Dimension(width, collapsedHeight));
        setPreferredSize(new Dimension(width, collapsedHeight));
        setOpaque(true);
        // Create the button for the accordion header
        mToggleButton = new OutlineButton();
//        mToggleButton.setMinimumSize(new Dimension(width, collapsedHeight));
//        mToggleButton.setMaximumSize(new Dimension(width, collapsedHeight));
        mToggleButton.setPreferredSize(new Dimension(width, collapsedHeight));
        mToggleButton.setBackground(getBackground());
        mToggleButton.setFont(FontPool.getInstance().getFontForHeight(collapsedHeight));
        mToggleButton.setFocusPainted(false);
        add(mToggleButton);

        // Use the custom content panel as the content to reveal
        mContentPanel = customContent == null ? new GameUI() : customContent;
        scrollPane = new TranslucentScrollPane(mContentPanel,  width, expandedHeight);
        scrollPane.setVisible(false); // Set collapsed
        add(scrollPane);

        // Add action listener to toggle content visibility and adjust panel size
        mColor = mainColor;
        Color pressedColor = mColor.darker().darker();
        mToggleButton.setBackground(mColor);
        setBackground(mColor);
        mToggleButton.addActionListener(e -> {
            toggleContent();
            mToggleButton.setBackground(mToggleButton.getBackground() == mColor ? pressedColor : mColor);
        });
    }
    public JComponent getContentPanel() { return mContentPanel; }
    public JButton getToggleButton() { return mToggleButton; }
    public boolean isOpen() { return scrollPane.isShowing(); }
}
