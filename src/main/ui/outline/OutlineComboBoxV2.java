package main.ui.outline;

import main.game.stores.pools.FontPoolV1;
import main.ui.custom.StringComboBox;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Color;
import java.awt.Dimension;

public class OutlineComboBoxV2 extends StringComboBox {

    public OutlineComboBoxV2(String helpText, Color color, int width, int height) {
        OutlineComboBoxEditor editor = new OutlineComboBoxEditor(color);
        editor.setPreferredSize(new Dimension(width, height));
        setEditor(editor);
        setRenderer(new OutlineComboBoxRenderer(color));
        setFont(FontPoolV1.getInstance().getFontForHeight((int) (height * .9)));
        setEditable(true);
        setBackground(color);

        if (helpText != null) {
//            setToolTipText(helpText);
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