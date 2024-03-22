package main.ui.components;

import main.game.stores.pools.ColorPalette;
import main.ui.components.elements.DualOutlineLabelComboBoxEditor;
import main.ui.components.elements.DualOutlineLabelRenderer;
import main.ui.components.elements.GenericComboBoxUI;

import javax.swing.JComboBox;

public class DatasheetV2 extends JComboBox<String> {

    public DatasheetV2() {
        this(20);
    }

    public DatasheetV2(int columns) {
        setPrototypeDisplayValue("X".repeat(columns));
        setEditable(false);
        setUI(new GenericComboBoxUI());

        setRenderer(new DualOutlineLabelRenderer(":"));
        setEditor(new DualOutlineLabelComboBoxEditor(":"));
        setBackground(ColorPalette.TRANSPARENT);
        getEditor().getEditorComponent().setBackground(ColorPalette.TRANSPARENT);
//        if (asDictionary) {
//            setRenderer(new DualOutlineLabelRenderer(":"));
//            setEditor(new DualOutlineLabelComboBoxEditor(":"));
//        } else {
////            setEditable(true);
////            setRenderer(new OutlineLabelRenderer());
////            setEditor(new OutlineLabelComboBoxEditor());
////
//            setRenderer(new DualOutlineLabelRenderer(""));
//            setEditor(new DualOutlineLabelComboBoxEditor(""));
//        }
    }

    public void setReadOnly() {
        addActionListener(e -> setSelectedIndex(0));
    }
}
