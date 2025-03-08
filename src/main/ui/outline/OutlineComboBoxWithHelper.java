package main.ui.outline;

import main.game.stores.pools.FontPoolV1;
import main.ui.custom.StringComboBox;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineComboBoxWithHelper extends StringComboBox {

    public OutlineComboBoxWithHelper(String helpText, Color color, int width, int height) {
        this(helpText, color, width, height, SwingConstants.CENTER);
    }

    public OutlineComboBoxWithHelper(String helpText, Color color, int width, int height, int hAlignment) {
        OutlineComboBoxEditor editor = new OutlineComboBoxEditor(color, hAlignment);
        editor.setPreferredSize(new Dimension(width, height));
        setEditor(editor);
        setRenderer(new OutlineComboBoxRenderer(color, hAlignment));
        setFont(FontPoolV1.getInstance().getFontForHeight((int) (height * .9)));
        setEditable(true);
        setBackground(color);

        if (helpText != null) {
//            setToolTipText(helpText);
            setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton button = super.createArrowButton();
                    button.setBackground(color);
                    button.setBorderPainted(true);
                    button.setFocusPainted(false);

                    return button;
                }
            });
            addPopupMenuListener(new PopupMenuListener() {
                private String selected = null;
                @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    selected = getSelectedItem();
                    editor.setItem(helpText);
                }
                @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    selected = getSelectedItem();
                    editor.setItem(selected);
                }
                @Override public void popupMenuCanceled(PopupMenuEvent e) {
                    selected = getSelectedItem();
                    editor.setItem(selected);
                }
            });
        }
    }
}