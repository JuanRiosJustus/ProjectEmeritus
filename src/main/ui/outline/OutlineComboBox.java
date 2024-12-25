package main.ui.outline;

import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.ui.custom.StringComboBox;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class OutlineComboBox extends StringComboBox {

    public OutlineComboBox() { this(Color.ORANGE, 5); }
    public OutlineComboBox(Color color, int width, int height) {
        OutlineComboBoxEditor editor = new OutlineComboBoxEditor(color);
        editor.setPreferredSize(new Dimension(width, height));
        setEditor(new OutlineComboBoxEditor(color));
        setRenderer(new OutlineComboBoxRenderer(color));
        setFont(FontPool.getInstance().getFontForHeight((int) (height * .9)));
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                // Return null so that no arrow button is created
                return null;
            }
        });
        setEditable(true);
        setBackground(color);
    }

    public OutlineComboBox(Color color, int height) {

        ComboBoxEditor comboBoxEditor = new OutlineComboBoxEditor(color);
        setEditor(comboBoxEditor);
        setRenderer(new OutlineComboBoxRenderer(color));
        setFont(FontPool.getInstance().getFontForHeight((int) (height * .9)));

        addPopupMenuListener(new PopupMenuListener() {
                                  @Override
                                  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                                      // The popup is about to become visible, so we can set the editor text
                                      comboBoxEditor.setItem("Type here...");
                                  }

                                  @Override
                                  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                                      // Popup closing, no special action needed here
                                  }

                                  @Override
                                  public void popupMenuCanceled(PopupMenuEvent e) {
                                      // Popup canceled, no special action needed
                                  }
        });

        setEditable(true);
        setBackground(color);
    }
}