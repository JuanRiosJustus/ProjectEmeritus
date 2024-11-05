package main.ui.presets.editor;

import main.constants.StateLock;
import main.game.stores.pools.FontPool;
import main.ui.components.OutlineLabel;
import main.ui.custom.StringComboBox;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class OutlineLabelToDropDown extends JPanel {

    private JLabel mLeftLabel = new OutlineLabel("");
    private StringComboBox mRightDropDown = new StringComboBox();
    private final StateLock mStateLock = new StateLock();
    public void setup(int width, int height) { setup(null, width, height); }

    public void setup(Color color, int width, int height) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(width, height));
        removeAll();

        mLeftLabel = new OutlineLabel("");
        mLeftLabel.setHorizontalAlignment(JLabel.LEFT);
        mLeftLabel.setPreferredSize(new Dimension(width / 2, height));
        mLeftLabel.setFont(FontPool.getInstance().getFontForHeight(height));

        mRightDropDown.setPreferredSize(new Dimension(width / 2, height));
        mRightDropDown.setEditor(new OutlineComboBoxEditor(color, width / 2, height));
        mRightDropDown.setRenderer(new OutlineComboBoxRenderer(color));
        mRightDropDown.setFont(FontPool.getInstance().getFontForHeight(height));
        mRightDropDown.setEditable(true);

        add(mLeftLabel);
        add(mRightDropDown);
    }

    public void setLeftLabel(String str) {
        if (!mStateLock.isUpdated("left", str)) { return; }
        mLeftLabel.setText(str);
    }
    public void addItem(String str) {
        if (!mStateLock.isUpdated("right", str)) { return; }
        mRightDropDown.addItem(str);
    }
    public String getSelectedItem() { return mRightDropDown.getSelectedItem(); }
}
