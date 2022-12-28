package ui.screen.editor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class GameEditorSidePanelItem extends JPanel {

//    private JToggleButton m_selectedButton = new JToggleButton("Select");
    public JLabel label;
    public BufferedImage panelImage;
    public int panelIndex = 0;
    public GameEditorSidePanelItem link = null;

    public GameEditorSidePanelItem() {

    }

    public void setIcon(BufferedImage img) {
        ImageIcon icon = new ImageIcon(img);
        removeAll();
        add(new JLabel(icon));
        JPanel topBot = new JPanel();
        topBot.setLayout(new BorderLayout());
//        topBot.add(m_selectedButton, BorderLayout.NORTH);
//        label = new JLabel("0"); //initDropdown();
//        topBot.add(label, BorderLayout.SOUTH);
//        topBot.add(new JRadioButton(), BorderLayout.EAST);
        add(topBot);
    }

    public GameEditorSidePanelItem(BufferedImage image, int index) {
        panelImage = image;
        panelIndex = index;
        ImageIcon icon = new ImageIcon(image);
        add(new JLabel(icon));
        JPanel topBot = new JPanel();
        topBot.setLayout(new BorderLayout());
//        topBot.add(m_selectedButton, BorderLayout.NORTH);
//        label = new JLabel("0"); //initDropdown();
//        topBot.add(label, BorderLayout.SOUTH);
//        topBot.add(new JRadioButton(), BorderLayout.EAST);
        add(topBot);
    }

    public String getLayerType() { return Objects.requireNonNull(label.getText()); }
//    public JToggleButton getSelectedButton() { return m_selectedButton; }
}
