package main.ui.outline.production.labels;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.outline.OutlineLabel;
import main.ui.outline.production.core.OutlineButton;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineButtonBackgroundWithWestLabel extends GameUI {

    protected OutlineLabel mLabel = null;
    protected OutlineButton mImageContainer = null;
    public OutlineButtonBackgroundWithWestLabel(Color color, int width, int height, String helperText) {
        super(width, height);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);


        int componentWidth = height;
        int componentHeight = height;
        mImageContainer = new OutlineButton();
        mImageContainer.setPreferredSize(new Dimension(componentWidth, componentHeight));
        mImageContainer.setMinimumSize(new Dimension(componentWidth, componentHeight));
        mImageContainer.setMaximumSize(new Dimension(componentWidth, componentHeight));
        mImageContainer.setBorder(BorderFactory.createRaisedSoftBevelBorder());


        int leftoverWidth = (width - componentWidth);
        int labelWidth = (int) (leftoverWidth * .98);
        int labelHeight = (int) (height);
        mLabel = new OutlineLabel();
//        mLabel.setOutlineThickness(1);
        mLabel.setBackground(color);
        mLabel.setText(helperText);
        mLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMinimumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMaximumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setFont(FontPoolV1.getInstance().getFontForHeight((int) (labelHeight * .75)));


        JPanel panel = new GameUI(labelWidth, labelHeight);
        panel.setBackground(color);
        panel.setLayout(new BorderLayout());
        panel.add(mLabel);

        int paddingWidth = leftoverWidth - labelWidth;
        int paddingHeight = labelHeight;
        JPanel paddingPanel = new JPanel();
        paddingPanel.setPreferredSize(new Dimension(paddingWidth, paddingHeight));
        paddingPanel.setBackground(color);

        add(panel);
        add(mImageContainer);
        add(paddingPanel);


//        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//        add(centerComponentPanel(color, width, labelHeight, mLabel), BorderLayout.NORTH);
//        add(mComboBox, BorderLayout.CENTER);
    }

    public int getImageWidth() { return (int) mImageContainer.getPreferredSize().getWidth(); }
    public int getImageHeight() { return (int) mImageContainer.getPreferredSize().getHeight(); }
    public JButton getImageContainer() { return mImageContainer; }
}
