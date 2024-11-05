package main.ui.presets.editor;

import main.constants.StateLock;
import main.game.stores.pools.FontPool;
import main.ui.components.OutlineLabel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class OutlineLabelToLabel extends JPanel {
    private JLabel leftLabel = new OutlineLabel("");
    private JLabel rightLabel = new OutlineLabel("");
    private final StateLock mStateLock = new StateLock();
    public OutlineLabelToLabel() { }
    public OutlineLabelToLabel(int width, int height) { setup(width, height); }

    public void setup(int width, int height) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setPreferredSize(new Dimension(width, height));
        removeAll();

        leftLabel = new OutlineLabel("Current Height");
        leftLabel.setHorizontalAlignment(JLabel.LEFT);
        leftLabel.setPreferredSize(new Dimension(width / 2, height));
        leftLabel.setFont(FontPool.getInstance().getFontForHeight(height));
//        currentTileHeightLabel.setBackground(ColorPalette.getRandomColor());

        rightLabel = new OutlineLabel("???");
        rightLabel.setHorizontalAlignment(JTextField.RIGHT);
        rightLabel.setPreferredSize(new Dimension(width  / 2, height));
        rightLabel.setFont(FontPool.getInstance().getFontForHeight(height));
//        currentTileHeightField.setBackground(keyValuePanel.getBackground());

        add(leftLabel);
        add(rightLabel);
    }
    public void setLeftLabel(String str) {
        if (!mStateLock.isUpdated("left", str)) { return; }
        leftLabel.setText(str);
    }
    public void setRightLabel(String str) {
        if (!mStateLock.isUpdated("right", str)) { return; }
        rightLabel.setText(str);
    }
}
