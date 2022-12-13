package graphics.temporary;

import javax.swing.*;

public class JImageLabel extends JPanel {

    protected JLabel m_field;
    protected JLabel m_value;

    public JImageLabel(ImageIcon imageIcon, String value) {
        m_field = new JLabel();
        m_field.setIcon(imageIcon);
        add(m_field);
        m_value = new JLabel(value);
        add(m_value);
    }

    public void setImage(ImageIcon image) { m_field.setIcon(image); }
    public void setLabel(String value) { m_value.setText(value); }
}
