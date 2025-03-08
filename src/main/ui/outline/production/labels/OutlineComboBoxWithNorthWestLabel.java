package main.ui.outline.production.labels;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.outline.OutlineComboBoxWithHelper;
import main.ui.outline.OutlineLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class OutlineComboBoxWithNorthWestLabel extends GameUI {

    protected OutlineLabel mLabel = null;
    protected OutlineComboBoxWithHelper mComboBox = null;
    protected String mHelperText;
    public OutlineComboBoxWithNorthWestLabel(Color color, int width, int height, String helperText) {
        super(width, height);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        setBackground(color);
        setOpaque(true);

        mHelperText = helperText;


        int labelWidth = width;
        int labelHeight = (int) (height * .4);
        mLabel = new OutlineLabel();
        mLabel.setTextOutlineThickness(1);
        mLabel.setBackground(color);
        mLabel.setText(helperText);
        mLabel.setPreferredSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMinimumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setMaximumSize(new Dimension(labelWidth, labelHeight));
        mLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        mLabel.setAlignmentX(SwingConstants.LEFT);
        mLabel.setFont(FontPoolV1.getInstance().getFontForHeight(labelHeight));

        JPanel labelPanel = new GameUI();
        labelPanel.setBackground(color);
        labelPanel.setLayout(new BorderLayout());
        labelPanel.add(mLabel, BorderLayout.CENTER);

        int componentWidth = width;
        int componentHeight = height - labelHeight;
        mComboBox = new OutlineComboBoxWithHelper(helperText, color, componentWidth, componentHeight, SwingConstants.LEFT);
//        mComboBox.setAlignmentX(SwingConstants.LEFT);

        mComboBox.setPreferredSize(new Dimension(componentWidth, componentHeight));
        mComboBox.setMinimumSize(new Dimension(componentWidth, componentHeight));
        mComboBox.setMaximumSize(new Dimension(componentWidth, componentHeight));
        mComboBox.setBorder(new EmptyBorder(0, 5, 0, 5));


        add(labelPanel);
        add(mComboBox);
//        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    public void addItem(String item) { mComboBox.addItem(item); }
    public void addActionListener(ActionListener al) { mComboBox.addActionListener(al); }
    public OutlineComboBoxWithHelper getDropDown() { return mComboBox; }
    public void addAdditionalContext(String value) { mLabel.setText(mHelperText + " - " + value); }
}
