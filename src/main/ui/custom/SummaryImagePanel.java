package main.ui.custom;

import main.constants.ColorPalette;

import java.awt.GridBagConstraints;

public class SummaryImagePanel extends ImagePanel {
    private JKeyValue row4;
    public SummaryImagePanel(int width, int height) {
        super(width, height);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor =  GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        row4 = new JKeyValue(width, (int) (height * .5), "Key");
        row4.setValue("Value");
        row4.setOpaque(true);
        row4.setBackground(ColorPalette.getRandomColor());

        row4 = new JKeyValue(width, (int) (height * .5), "Key");
        row4.setValue("Value");
        row4.setOpaque(true);
        row4.setBackground(ColorPalette.getRandomColor());


        gbc.gridy = 2;
        container.add(row4, gbc);
    }
}
