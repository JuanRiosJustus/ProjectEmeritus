package graphics.temporary;

import javax.swing.*;

public class JImage extends JPanel {

    protected JLabel m_field;

    public JImage(ImageIcon imageIcon) {
        m_field = new JLabel();
        m_field.setIcon(imageIcon);
        add(m_field);
    }

    public void setImage(ImageIcon image) { m_field.setIcon(image); }
    public ImageIcon getImage() { return (ImageIcon) m_field.getIcon(); }
}
