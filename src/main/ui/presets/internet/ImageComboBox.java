package main.ui.presets.internet;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageComboBox extends JComboBox {
    private final List<ImageIcon> mIcons = new ArrayList<>();
    private final List<String> mStrings = new ArrayList<>();

    public void add() {

        this.addItem(new Object());
        this.addItem(new Object());
        this.addItem(new Object());
        this.addItem(new Object());
        this.addItem(new Object());
        String[] petStrings = {"Bird", "Cat", "Dog", "Rabbit", "Pig"};
        mStrings.addAll(Arrays.asList(petStrings));

        //Create the combo box.
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(200, 130));
        setRenderer(renderer);
        setMaximumRowCount(3);
    }

     class ComboBoxRenderer extends JLabel
            implements ListCellRenderer {
        private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
            int selectedIndex = (Integer) value;

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = mIcons.get(selectedIndex);
            String pet = mStrings.get(selectedIndex);
            setIcon(icon);
            if (icon != null) {
                setText(pet);
                setFont(list.getFont());
            } else {
                setUhOhText(pet + " (no image available)",
                        list.getFont());
            }

            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
    }
}
