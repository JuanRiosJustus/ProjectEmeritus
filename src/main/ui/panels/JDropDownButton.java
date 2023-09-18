package main.ui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class JDropDownButton extends JButton implements ActionListener {
    private JButton jDropDownButton;
    private JPopupMenu jPopupMenu = new JPopupMenu();
    private Object eventSource;

    public Object getEventSource() {
        return eventSource;
    }

    public void setEventSource(Object eventSource) {
        this.eventSource = eventSource;
    }

    public JDropDownButton() {
        super();
        initGUI();
    }

    public JDropDownButton(String str) {
        super(str);
        initGUI();
    }

    public JDropDownButton(Action a) {
        super(a);
        initGUI();
    }

    public JDropDownButton(Icon icon) {
        super(icon);
        initGUI();
    }

    public JDropDownButton(String text, Icon icon) {
        super(text, icon);
        initGUI();
    }

    public JPopupMenu getjPopupMenu() {
        return jPopupMenu;
    }

    public void setjPopupMenu(JPopupMenu jPopupMenu) {
        this.jPopupMenu = jPopupMenu;
    }

    public void add(final JMenuItem jMenuItem) {
        jPopupMenu.add(jMenuItem);
        jMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                eventSource = jMenuItem;
                jButtonActionPerformed(evt);
            }
        });
    }

    public void insert(final JMenuItem jMenuItem, int index) {
        jPopupMenu.insert(jMenuItem, index);
        jMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                eventSource = jMenuItem;
                jButtonActionPerformed(evt);
            }
        });
    }

    public void removeAll() {
        jPopupMenu.removeAll();
    }

    public void remove(JMenuItem jMenuItem) {
        jPopupMenu.remove(jMenuItem);
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        for (MenuElement menu : jPopupMenu.getSubElements()) {
            if (menu instanceof JMenuItem) {
                ((JMenuItem) menu).setEnabled(b);
            }
        }
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);
            {
                jDropDownButton = new JButton();
                jDropDownButton.setBorder(null);
                this.add(jDropDownButton, BorderLayout.EAST);
                this.add(new JLabel(getText() + "        "), BorderLayout.CENTER);
//                jDropDownButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/petersoft/advancedswing/jdropdownbutton/add.png")));
                jDropDownButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        jDropDownButtonActionPerformed(evt);
                    }
                });
                jDropDownButton.setPreferredSize(new Dimension(18, 0));
            }
            // this.setMargin(new Insets(0, 0, 0, 0));
            this.addActionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width + 20, super.getPreferredSize().height);
    }

    private void jDropDownButtonActionPerformed(ActionEvent evt) {
        jPopupMenu.show(this, jDropDownButton.getLocation().x, jDropDownButton.getHeight());
    }

    private void jButtonActionPerformed(ActionEvent evt) {
        this.fireActionPerformed(evt);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventSource = null;
    }

}
