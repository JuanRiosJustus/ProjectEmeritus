package main.ui.custom;

import main.utils.StringUtils;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class JKeyProgress extends JPanel {
    private final JProgressBar progressBar;
    private final JLabel label;
    public JKeyProgress(int width, int height, String name) {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = .25;
        gbc2.weighty = 1;
        gbc2.anchor = GridBagConstraints.WEST;
        label = new JLabel(name == null || name.isEmpty() ? "" : StringUtils.spaceByCapitalization(name));
        label.setOpaque(false);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setPreferredSize(new Dimension((int) (width * .25), height));
        add(label, gbc2);

        gbc2.gridx = 1;
        gbc2.weightx = .75;
        gbc2.fill = SwingConstants.HORIZONTAL;
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setOpaque(false);
        progressBar.setPreferredSize(new Dimension((int) (width * .75), height));
        progressBar.setMinimumSize(progressBar.getPreferredSize());
        progressBar.setMaximumSize(progressBar.getPreferredSize());
        progressBar.setValue(0);
        add(progressBar, gbc2);

        setPreferredSize(new Dimension(width, height));
        setBorder(new EmptyBorder(5, 5, 5,5));
//        setBackground(ColorPalette.getRandomColor());
    }
    public int getValue() { return progressBar.getValue(); }
    public void setValue(int num) { progressBar.setValue(num); }
    public String getKey() { return label.getText(); }
    public void setKey(String str) { label.setText(str); }
}
