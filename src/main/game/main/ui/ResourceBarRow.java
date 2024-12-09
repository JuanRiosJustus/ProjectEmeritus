package main.game.main.ui;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.HealthBar;
import main.ui.outline.OutlineLabel;

import javax.swing.*;
import java.awt.*;

public class ResourceBarRow extends GameUI {
    private final OutlineLabel mLabel; // Label to display the name or identifier
    private final HealthBar mHealthBar; // Custom health bar to display health
    private static final Float VERTICAL_PADDING_MULTIPLIER = .2f; // Percentage padding to apply vertically
    private static final Float HORIZONTAL_PADDING_MULTIPLIER = .05f; // Percentage padding to apply horizontally

    public ResourceBarRow(int width, int height, Color color) {
        // Configure the main layout and size of the HealthBarRow
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));

        int verticalPadding = (int) (width * .01);
        setBorder(BorderFactory.createEmptyBorder(0, verticalPadding, 0, verticalPadding));
        setBackground(color);

        // Calculate the width for the label, which takes up 25% of the row's width
        int labelContainerWidth = (int) (width * .25);
        int labelContainerHeight = height;
        JPanel mLabelContainer = new GameUI(); // Custom panel for vertical layout
        mLabelContainer.setLayout(new BoxLayout(mLabelContainer, BoxLayout.Y_AXIS)); // Set vertical layout
        mLabelContainer.setBackground(color); // Set background color
        mLabelContainer.setPreferredSize(new Dimension(labelContainerWidth, labelContainerHeight)); // Set size

        // Create and configure the label
        int labelHeight = (int) (labelContainerHeight - (labelContainerHeight * VERTICAL_PADDING_MULTIPLIER));
        int labelWidth = (int) (labelContainerWidth - (labelContainerWidth * HORIZONTAL_PADDING_MULTIPLIER));

        mLabel = new OutlineLabel();
        mLabel.setFont(FontPool.getInstance().getFontForHeight(labelHeight)); // Set font size based on height
        mLabel.setPreferredSize(new Dimension(labelWidth, labelHeight)); // Reserve space for the label
        mLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        mLabelContainer.add(mLabel);

        // Configure the horizontal resource container
        int healthContainerWidth = width - labelWidth; // Remaining width after reserving space for the label
        int healthContainerHeight = labelHeight;

        JPanel mHealthBarContainer = new GameUI(); // Custom panel for vertical layout
        mHealthBarContainer.setLayout(new BoxLayout(mHealthBarContainer, BoxLayout.Y_AXIS)); // Set vertical layout
        mHealthBarContainer.setBackground(color); // Set background color
        mHealthBarContainer.setPreferredSize(new Dimension(healthContainerWidth, healthContainerHeight)); // Set size

        // Width matches the vertical container width
        // Height matches the vertical container height
        int healthBarWidth = (int) (healthContainerWidth - (healthContainerWidth * HORIZONTAL_PADDING_MULTIPLIER));
        int healthBarHeight = (int) (healthContainerHeight - (healthContainerHeight * VERTICAL_PADDING_MULTIPLIER));
        mHealthBar = new HealthBar(healthBarWidth, healthBarHeight, color); // Create a custom health bar
        mHealthBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        mHealthBar.setAlignmentY(Component.CENTER_ALIGNMENT);
        mHealthBarContainer.add(mHealthBar);


        add(mLabelContainer); // Add label to the center of the row
        add(mHealthBarContainer);
    }

    /**
     * Update the current health displayed on the health bar.
     *
     * @param health The current health value.
     */
    public void setCurrentHealth(int health) {
        mHealthBar.setCurrentHealth(health);
    }

    public void setCurrentHealthNoAnimation(int health) {
        mHealthBar.setCurrentHealthNoAnimation(health);
    }
    /**
     * Update the maximum health for the health bar.
     *
     * @param maxHealth The maximum health value.
     */
    public void setMaxHealth(int maxHealth) {
        mHealthBar.setMaxHealth(maxHealth);
    }

    /**
     * Set the label text for this row.
     *
     * @param label The text to display.
     */
    public void setLabel(String label) {
        mLabel.setText(label);
    }
}