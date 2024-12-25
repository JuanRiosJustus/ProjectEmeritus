package main.ui.outline.production;

import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.OutlineTextField;
import main.ui.outline.production.core.OutlineButton;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineButtonAndOutlineField extends GameUI {
    protected OutlineButton mLabel = null;
    protected OutlineTextField mTextField = null;

    public OutlineButtonAndOutlineField(int width, int height, Color color) {
        setLayout(new BorderLayout());

        // Controls header setup
        setPreferredSize(new Dimension(width, height));
        setBackground(color);

        // Back button in controls header
        int controlsHeaderButtonWidth = (int) (width * 0.15);
        int controlsHeaderButtonHeight = height;
        mLabel = new OutlineButton("<");
        int fontSize = (int) (controlsHeaderButtonHeight * .9);
        mLabel.setFont(FontPool.getInstance().getFontForHeight(fontSize));
        mLabel.setBackground(color);
        mLabel.setPreferredSize(new Dimension(controlsHeaderButtonWidth, controlsHeaderButtonHeight));
        SwingUiUtils.setHoverEffect(mLabel);

        // Header label
        int controlsHeaderLabelWidth = width - controlsHeaderButtonWidth;
        int controlsHeaderLabelHeight = height;
        mTextField = new OutlineTextField();
        mTextField.setFont(FontPool.getInstance().getFontForHeight(controlsHeaderLabelHeight));
        mTextField.setPreferredSize(new Dimension(controlsHeaderLabelWidth, controlsHeaderLabelHeight));
        mTextField.setHorizontalAlignment(SwingConstants.CENTER);
//        mControlsHeader.add(mControlsHeaderLabel, BorderLayout.CENTER);

        add(mLabel, BorderLayout.WEST);
        add(mTextField, BorderLayout.CENTER);
    }

    public JButton getButton() { return mLabel; }
    public OutlineTextField getTextField() { return mTextField; }
}
