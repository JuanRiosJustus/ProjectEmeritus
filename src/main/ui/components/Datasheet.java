package main.ui.components;

import main.game.stores.pools.ColorPalette;
import main.ui.components.elements.*;

import javax.swing.JComboBox;
import javax.swing.Renderer;
import java.awt.Color;

public class Datasheet extends JComboBox<String> {

    public Datasheet() {
        this(20);
    }

    public Datasheet(int columns) {
        setPrototypeDisplayValue("X".repeat(columns));
        setEditable(false);
        setUI(new GenericComboBoxUI());

        setRenderer(new DualOutlineLabelRenderer(":"));
        setEditor(new DualOutlineLabelComboBoxEditor(":"));
        // Maybe the following are not needed
//        setBackground(ColorPalette.TRANSPARENT);
//        getEditor().getEditorComponent().setBackground(ColorPalette.TRANSPARENT);

//        setBackground(ColorPalette.RED);
//        getEditor().getEditorComponent().setBackground(ColorPalette.RED);
//        getEditor().getEditorComponent().setForeground(ColorPalette.RED);
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

    public void setRendererBackground(Color color) {
        setBackground(color);
        getEditor().getEditorComponent().setBackground(color);
//        setRenderer(new DualOutlineLabelRenderer(color, ":"));
//        DualOutlineLabelRenderer dolr = (DualOutlineLabelRenderer) getRenderer();
//        dolr.setPanelColors(Color.RED);
//        dolr.setPanelColors(color);
//        revalidate();
//        repaint();
//        dolr.setPanelColors(color);
//        dolr.revalidate();
//        dolr.repaint();
    }


    public void setReadOnly() {
        addActionListener(e -> setSelectedIndex(0));
    }
}
