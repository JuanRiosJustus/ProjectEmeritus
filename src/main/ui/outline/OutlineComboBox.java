package main.ui.outline;

import main.game.stores.pools.FontPool;
import main.ui.custom.StringComboBox;

import java.awt.*;

public class OutlineComboBox extends StringComboBox {

    public OutlineComboBox() { this(Color.ORANGE, 5); }
    public OutlineComboBox(Color color, int width, int height) {
        OutlineComboBoxEditor editor = new OutlineComboBoxEditor(color);
        editor.setPreferredSize(new Dimension(width, height));
        setEditor(new OutlineComboBoxEditor(color));
        setRenderer(new OutlineComboBoxRenderer(color));
        setFont(FontPool.getInstance().getFontForHeight((int) (height * .8)));
        setEditable(true);
        setBackground(color);
    }

    public OutlineComboBox(Color color, int height) {
        setEditor(new OutlineComboBoxEditor(color));
        setRenderer(new OutlineComboBoxRenderer(color));
        setFont(FontPool.getInstance().getFontForHeight((int) (height * .8)));
        setEditable(true);
        setBackground(color);
    }
}