package main.ui.custom;

import main.utils.StringUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JKeyValue extends JPanel {

    public static final String DEFAULT = "";
    private final JLabel value;
    private final JLabel key;
    public JKeyValue(int width, int height) { this(width, height, DEFAULT); }
    public JKeyValue(int width, int height, String name) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 1;
        gbc2.weighty = 1;
        gbc2.anchor = GridBagConstraints.WEST;
        key = new JLabel(StringUtils.spaceByCapitalization(name));
        key.setOpaque(false);
        key.setFont(key.getFont().deriveFont(Font.BOLD));
        key.setPreferredSize(new Dimension((int) (width * .25), height));
        add(key, gbc2);

        gbc2.anchor = GridBagConstraints.EAST;
        gbc2.gridx = 1;
        value = new JLabel();
        value.setOpaque(false);
        value.setPreferredSize(new Dimension((int) (width * .75), height));
        add(value, gbc2);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 5, 0,5));
    }

    public void setValue(String txt) {
        if (txt == null || value.getText().equalsIgnoreCase(txt)) { return; }
        value.setText(txt);
    }
    public void setValueColor(Color color) { value.setForeground(color); }
    public String getValue() { return value.getText(); }
    public void setKey(String txt) { key.setText(txt == null || key.getText().isBlank() ? DEFAULT : txt); }
    public String getKey() { return key.getText(); }
    public void switchOnKey() { key.setVisible(!key.isVisible()); }
    public void switchOnValue() { value.setVisible(!value.isVisible()); }
    public void setKeyAndValue(String key, String value) { setKey(key); setValue(value); }
}
