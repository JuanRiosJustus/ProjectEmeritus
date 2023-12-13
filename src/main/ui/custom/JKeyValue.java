package main.ui.custom;

import main.utils.StringUtils;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JKeyValue extends JPanel {
    public static final String DEFAULT = "";
    private final JComponent mValue;
    private final JLabel mKey;
    private final int mWidth;
    private final int mHeight;
    private boolean filled = false;
    public JKeyValue(int width, int height, String name) {
        this(width, height, name, new JLabel());
    }
    public JKeyValue(int width, int height, String name, JComponent component) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 1;
        gbc2.weighty = 1;
        gbc2.anchor = GridBagConstraints.WEST;
        mKey = new JLabel(StringUtils.spaceByCapitalization(name));
        mKey.setOpaque(false);
        mKey.setFont(mKey.getFont().deriveFont(Font.BOLD));
//        key.setPreferredSize(new Dimension((int) (width * .25), height));
        add(mKey, gbc2);

        gbc2.anchor = GridBagConstraints.EAST;
        gbc2.gridx = 1;
        gbc2.weightx = 0;
        mValue = component;
        mValue.setOpaque(false);
//                if (mValue instanceof JProgressBar) {
//            mValue.setMinimumSize(mValue.getPreferredSize());
//            mValue.setMaximumSize(mValue.getPreferredSize());
        if (mValue instanceof JProgressBar) {
            mValue.setMinimumSize(mValue.getPreferredSize());
            mValue.setMaximumSize(mValue.getPreferredSize());
        }
        add(mValue, gbc2);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 5, 0,5));
        mWidth = width;
        mHeight = height;
    }

    public void setValueColor(Color color) { mValue.setForeground(color); }
    public JComponent getValueComponent() { return mValue; }
    public JLabel getKeyComponent() { return mKey; }
    public void setKey(String txt) { mKey.setText(txt == null || mKey.getText().isBlank() ? DEFAULT : txt); }
    public void fill() { fill(true); }
    public void fill(boolean fillValue) {
        if (filled) { return; }
        filled = true;

        if (fillValue) {
            mKey.setVisible(false);
            mValue.setVisible(true);
            mValue.setPreferredSize(new Dimension((int) (mWidth * .9), mHeight));
            mValue.setMinimumSize(mValue.getPreferredSize());
            mValue.setMaximumSize(mValue.getPreferredSize());
        } else {
            mValue.setVisible(false);
            mKey.setVisible(true);
            mKey.setPreferredSize(new Dimension(mWidth, mHeight));
        }
    }
//    public void update
    public void setKeyAndValue(String key, String value) { setKey(key); }
}
