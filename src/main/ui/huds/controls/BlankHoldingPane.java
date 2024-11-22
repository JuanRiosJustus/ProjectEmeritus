package main.ui.huds.controls;

import main.game.main.GameModel;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class BlankHoldingPane extends GameUI {
    private JComponent mCurrentComponent = null;
    private final GridBagConstraints mGbc;

    public BlankHoldingPane(int width, int height, int x, int y) {
        super(width, height);

        mGbc = new GridBagConstraints();
//        mGbc.weightx = 0;
//        mGbc.weighty = 0;
        mGbc.weightx = 1;
        mGbc.weighty = 1;
        mGbc.gridx = 0;
        mGbc.gridy = 0;
        mGbc.fill = GridBagConstraints.BOTH;
        mGbc.anchor = GridBagConstraints.NORTHWEST;

        setLayout(new GridBagLayout());

        setOpaque(true);
//        setPreferredLocation(x, y);
//        setBackground(ColorPalette.BLUE);
//        SwingUiUtils.setStylizedRaisedBevelBorder(this);
    }

    public void setup(JComponent component, Color color) {
        if (component == mCurrentComponent) {
            return;
        }
        mCurrentComponent = component;

        add(component, mGbc);

//        component.setPreferredSize(getPreferredSize());
//        component.setMinimumSize(getPreferredSize());
//        component.setMaximumSize(getPreferredSize());
//        component.setOpaque(true);
        SwingUiUtils.recursivelySetBackground(color, component);

//        setPreferredSize(component.getPreferredSize());
        setMinimumSize(component.getPreferredSize());
        setMaximumSize(component.getPreferredSize());
        setSize(component.getPreferredSize());
//        setBackground(color);
    }
}
