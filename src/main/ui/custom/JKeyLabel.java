package main.ui.custom;

import main.utils.StringUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class JKeyLabel extends JPanel {

    public static final String DEFAULT = "~";
    private final JLabel value;
    private final JLabel key;
    public JKeyLabel(int width, int height) { this(width, height, DEFAULT); }
    public JKeyLabel(int width, int height, String name) {
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
        add(key, gbc2);

        gbc2.anchor = GridBagConstraints.EAST;
        gbc2.gridx = 1;
        value = new JLabel();
        value.setOpaque(false);
        add(value, gbc2);

        setPreferredSize(new Dimension(width, height));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 5, 0,5));
    }

//    public JLabel getValue() { return value; }
//    public JLabel getKey() { return key; }
    public void setLabel(String txt) {
        value.setText(txt == null || txt.isBlank() ? DEFAULT : txt);
    }
    public String getValue() { return value.getText(); }
}
