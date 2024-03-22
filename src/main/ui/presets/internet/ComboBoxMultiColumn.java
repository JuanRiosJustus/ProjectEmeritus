package main.ui.presets.internet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class ComboBoxMultiColumn extends JFrame
{
    public ComboBoxMultiColumn()
    {
        getContentPane().setLayout( new GridLayout(0, 2) );

        Vector items = new Vector();
        items.addElement( new Item("123456789", "Car" ) );
        items.addElement( new Item("23", "Plane" ) );
        items.addElement( new Item("345", "Train" ) );
        items.addElement( new Item("4567", "Nuclear Submarine" ) );

        //  Use a JTextArea as a renderer

        JComboBox comboBox1 = new JComboBox( items );
        comboBox1.setRenderer( new TextAreaRenderer(5) );

        getContentPane().add( new JLabel("TextArea Renderer") );
        getContentPane().add( comboBox1 );

        //  Use a JTextPane as a renderer

        JComboBox comboBox2 = new JComboBox( items );
        comboBox2.setRenderer( new TextPaneRenderer(10) );

        getContentPane().add( new JLabel("TextPane Renderer") );
        getContentPane().add( comboBox2 );

        //  Use a JPanel as a renderer

        JComboBox comboBox3 = new JComboBox( items );
        comboBox3.setRenderer( new PanelRenderer(50) );

        getContentPane().add( new JLabel("Panel Renderer") );
        getContentPane().add( comboBox3 );

        //  Using HTML

        JComboBox comboBox4 = new JComboBox( items );
        comboBox4.setRenderer( new HTMLRenderer() );

        getContentPane().add( new JLabel("HTML Renderer") );
        getContentPane().add( comboBox4 );
    }

    class Item
    {
        private String id;
        private String description;

        public Item(String id, String description)
        {
            this.id = id;
            this.description = description;
        }

        public String getId()
        {
            return id;
        }

        public String getDescription()
        {
            return description;
        }

        public String toString()
        {
            return description;
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new ComboBoxMultiColumn();
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }

    /*
     **  Tabs are easier to use in a JTextArea, but not very flexible
     */
    class TextAreaRenderer extends JTextArea implements ListCellRenderer
    {
        public TextAreaRenderer(int tabSize)
        {
            setTabSize(tabSize);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus)
        {
            Item item = (Item)value;
            setText(item.getId() + "\t" + item.getDescription());
            setBackground(isSelected ? list.getSelectionBackground() : null);
            setForeground(isSelected ? list.getSelectionForeground() : null);
            return this;
        }
    }

    /*
     **  Tabs are harder to use in a JTextPane, but much more flexible
     */
    class TextPaneRenderer extends JTextPane implements ListCellRenderer
    {
        public TextPaneRenderer(int tabColumn)
        {
            setMargin( new Insets(0, 0, 0, 0) );

            FontMetrics fm = getFontMetrics( getFont() );
            int width = fm.charWidth( 'w' ) * tabColumn;

            TabStop[] tabs = new TabStop[1];
            tabs[0] = new TabStop( width, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE );
            TabSet tabSet = new TabSet(tabs);

            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setTabSet(attributes, tabSet);
            getStyledDocument().setParagraphAttributes(0, 0, attributes, false);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus)
        {
            Item item = (Item)value;

            if (index == -1)
                setText( item.getDescription() );
            else
                setText(item.getId() + "\t" + item.getDescription());

            setBackground(isSelected ? list.getSelectionBackground() : null);
            setForeground(isSelected ? list.getSelectionForeground() : null);
            return this;
        }
    }

    /*
     **  Use a panel to hold multiple components
     */
    class PanelRenderer implements ListCellRenderer
    {
        private JPanel renderer;
        private JLabel first;
        private JLabel second;

        public PanelRenderer(int firstColumnWidth)
        {
            renderer = new JPanel();
            renderer.setLayout(new BoxLayout(renderer, BoxLayout.X_AXIS) );

            first = new JLabel(" ");
            Dimension d = first.getPreferredSize();
            d.width = firstColumnWidth;
            first.setMaximumSize(d);
            second = new JLabel();
            renderer.add(first );
            renderer.add(second );
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus)
        {
            Item item = (Item)value;

            first.setText( item.getId() );
            second.setText( item.getDescription() );

            renderer.setBackground(isSelected ? list.getSelectionBackground() : null);
            renderer.setForeground(isSelected ? list.getSelectionForeground() : null);

            return renderer;
        }
    }

    /*
     **  Use HTML to format the text
     */
    class HTMLRenderer extends DefaultListCellRenderer
    {
        private static final String START = "<html><table><tr><td width=40>";
        private static final String MIDDLE = "</td><td width=120>";
        private static final String END = "</td></tr></table></html>";

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus)
        {
            super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            Item item = (Item)value;
            setText(START
                    + item.getId()
                    + MIDDLE
                    + item.getDescription()
                    + END);

            return this;
        }
    }

}